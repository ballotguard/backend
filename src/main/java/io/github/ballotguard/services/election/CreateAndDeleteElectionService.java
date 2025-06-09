package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.services.vote.SendResultEmailService;
import io.github.ballotguard.services.vote.SendVotingLinkEmailService;
import io.github.ballotguard.services.vote.TaskSchedulerService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GenerateAndValidateStringUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import io.github.ballotguard.utilities.MatchTextPatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
public class CreateAndDeleteElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SendResultEmailService sendResultEmailService;

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private SendVotingLinkEmailService sendVotingLinkEmailService;

    public ResponseEntity creatElection(ElectionEntity election, UserEntity user) throws Exception {
        try {
            if (election.getElectionName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election name cannot be empty"));

            } else if (election.getElectionName().length() > 30) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Max election name length is 30"));

            }
//            else if (!Instant.ofEpochMilli(election.getStartTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                        .body(createResponseUtil.createResponseBody(false, "Election creation and start time must have at least 20 minutes difference"));
//
//            } else if (!Instant.ofEpochMilli(election.getEndTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                        .body(createResponseUtil.createResponseBody(false, "Election creation and end time must have at least 20 minutes difference"));
//
//            }
            else if (election.getElectionLayout().getElectionCardId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election card id cannot be empty"));

            } else if (election.getElectionLayout().getPollType() == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type cannot be empty"));
            } else if (!election.getElectionLayout().getPollType().equals("checkbox") && !election.getElectionLayout().getPollType().equals("radio")) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type is invalid"));
            }

            election.setVoteCount(new HashMap<>());
            for (Option option : election.getOptions()) {
                if (option.getOptionName() == null || option.getOptionName().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be empty"));
                } else if (option.getOptionName().length() > 30) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be more than 30 characters"));
                }
                option.setOptionId(GenerateAndValidateStringUtil.generateUniqueString());
                election.getVoteCount().put(option.getOptionId(), (long)0);
            }

            for (Voter voter : election.getVoters()) {

                if (voter.getVoterEmail() == null || voter.getVoterEmail().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Voter email cannot be empty"));
                } else if (!matchTextPatternUtil.isValidEmail(voter.getVoterEmail())) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "One or all of the voter's email is invalid"));
                }

                voter.setHasVoted(false);
                voter.setVoterId(GenerateAndValidateStringUtil.generateUniqueString());
            }

            election.setElectionId(GenerateAndValidateStringUtil.generateUniqueString());
            election.setCreatorId(getAuthenticatedUserUtil.getAuthenticatedUser().getUserId());
            election.setElectionCreationTime(Instant.now().toEpochMilli());
            election.setUniqueString(GenerateAndValidateStringUtil.generateUniqueString());
            election.setTotalVotes(0L);


            ElectionEntity savedElection = electionRepository.save(election);
            if (savedElection != null) {
                ArrayList<String> userElectionIds = user.getUserElectionsId();
                if (userElectionIds == null) {
                    userElectionIds = new ArrayList<>();
                }
                userElectionIds.add(savedElection.getElectionId());
                user.setUserElectionsId(userElectionIds);
                userRepository.save(user);

                taskSchedulerService.scheduleElectionTask(
                        savedElection.getElectionId(),
                        savedElection.getStartTime(),
                        true,
                        () -> {
                            try {
                                sendVotingLinkEmailService.sendVotingLinkToAllVoters(
                                        savedElection.getVoters(),
                                        savedElection.getStartTime(),
                                        savedElection.getEndTime(),
                                        election.getElectionName(),
                                        election.getElectionDescription(),
                                        election.getElectionId()
                                );
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                taskSchedulerService.scheduleElectionTask(
                        savedElection.getElectionId(),
                        savedElection.getEndTime(),
                        false,
                        () -> {
                            try {
                                Optional<ElectionEntity> finalElectionOpt = electionRepository.findByElectionId(savedElection.getElectionId());
                                if (finalElectionOpt.isPresent()) {
                                    ElectionEntity finalElection = finalElectionOpt.get();
                                    sendResultEmailService.sendResultSummaryToAllVotersAndElectionCreator(
                                            finalElection.getVoters(),
                                            user.getEmail(),
                                            createResponseUtil.createElectionResultMap(finalElection)
                                    );
                                } else {
                                    log.error("Election not found at result sending time: " + savedElection.getElectionId());
                                }
                            } catch (Exception e) {
                                log.error("Error while sending final results email", e);
                                throw new RuntimeException(e);
                            }
                        });


                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(createResponseUtil.createResponseBody(true, "Election is created successfully", "electionInfo", createResponseUtil.createElectionInfoMap(savedElection)));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election could not be created"));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity deleteElection(String electionId, UserEntity user) throws Exception {
        try {
            user.getUserElectionsId().remove(electionId);
            electionRepository.deleteById(electionId);
            userRepository.save(user);

            Optional<ElectionEntity> election1 = electionRepository.findByElectionId(electionId);
            if (election1.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(false, "Election deletion was not completed"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "Election deleted successfully"));

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
