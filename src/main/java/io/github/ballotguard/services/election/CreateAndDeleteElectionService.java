package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import io.github.ballotguard.utilities.MatchTextPatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public ResponseEntity creatElection(ElectionEntity election, UserEntity user) throws Exception {
        try {
            if (election.getElectionName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election name cannot be empty"));

            } else if (election.getElectionName().length() > 30) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Max election nane length is 30"));

            } else if (!Instant.ofEpochSecond(election.getStartTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)) )) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election creation and start time must have at least 20 minutes difference"));

            } else if (!Instant.ofEpochSecond(election.getEndTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)) )) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election creation and end time must have at least 20 minutes difference"));

            } else if (election.getElectionLayout().getElectionCardId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election card id cannot be empty"));

            } else if (election.getElectionLayout().getPollType() == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type cannot be empty"));
            } else if (election.getElectionLayout().getPollType() != "CHECKBOX" && election.getElectionLayout().getPollType() != "RADIO") {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type is invalid"));
            }

            for (Option option : election.getOptions()) {
                if (option.getOptionName() == null || option.getOptionName().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be empty"));
                } else if (option.getOptionName().length() > 30) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot more than 30 characters"));
                }

                option.setOptionId(UUID.randomUUID().toString());
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
                voter.setUniqueString(UUID.randomUUID().toString());
            }


            election.setElectionId(UUID.randomUUID().toString());
            election.setCreatorId(getAuthenticatedUserUtil.getAuthenticatedUser().getUserId());
            election.setElectionCreationTime(Instant.now().getEpochSecond());
            election.setResultLinkString(UUID.randomUUID().toString());
            election.setTotalVotes((long) 0);


            ElectionEntity savedElection = electionRepository.save(election);
            if (savedElection != null) {
                ArrayList<String> userElectionIds = user.getUserElectionsId();
                userElectionIds.add(savedElection.getElectionId());
                user.setUserElectionsId(userElectionIds);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(createResponseUtil.createResponseBody(true, "Election eas created successfully", "electionInfo", (Map) savedElection));
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
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if (!election.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            user.getUserElectionsId().remove(electionId);
            electionRepository.deleteById(electionId);
            userRepository.save(user);
            Optional<ElectionEntity> election1 = electionRepository.findByElectionId(electionId);
            if(!election1.isPresent()){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election deletion was not completed"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "Election deleted successfully"));

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
