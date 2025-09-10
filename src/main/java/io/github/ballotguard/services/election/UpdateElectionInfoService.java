package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.repositories.UserRepository;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class UpdateElectionInfoService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;

    @Autowired
    private UpdateElectionTimingService updateElectionTimingService;

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    public ResponseEntity<?> updateElectionInfo(UserEntity user, ElectionEntity oldElection, ElectionEntity newElection) {
        try{
            if (newElection.getElectionName().isEmpty() || newElection.getElectionName().isBlank()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election name cannot be empty"));

            } else if (newElection.getElectionName().length() > 30) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Max election name length is 30"));

            }
//            else if (!Instant.ofEpochMilli(newElection.getStartTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                        .body(createResponseUtil.createResponseBody(false, "Election cannot be edited from 20 minutes before election start time."));
//
//            } else if (!Instant.ofEpochMilli(newElection.getEndTime()).isAfter(Instant.now().plus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
//                        .body(createResponseUtil.createResponseBody(false, "Election cannot be edited after the election has ended"));
//
//            }
            else if (newElection.getElectionLayout().getElectionCardId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election card id cannot be empty"));

            } else if (newElection.getElectionLayout().getPollType() == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type cannot be empty"));
            } else if (!newElection.getElectionLayout().getPollType().equals("checkbox") && !newElection.getElectionLayout().getPollType().equals("radio")) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type is invalid"));
            }

            oldElection.setElectionName(newElection.getElectionName());
            oldElection.setElectionDescription(newElection.getElectionDescription());
            oldElection.setElectionLayout(newElection.getElectionLayout());
            oldElection.setOptions(newElection.getOptions());
            oldElection.setIsOpen(newElection.getIsOpen());
            oldElection.setVoters(newElection.getVoters());
            oldElection.setStartTime(newElection.getStartTime());
            oldElection.setEndTime(newElection.getEndTime());



            for (Option option : oldElection.getOptions()) {
                if (option.getOptionName() == null || option.getOptionName().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be empty"));
                } else if (option.getOptionName().length() > 30) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be more than 30 characters"));
                }
                option.setOptionId(GenerateAndValidateStringUtil.generateUniqueString());
                oldElection.getVoteCount().put(option.getOptionId(), (long)0);
            }


            if(oldElection.getIsOpen()){
                oldElection.setVoters(null);
            }else{
                for (Voter voter : oldElection.getVoters()) {

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
            }

            ElectionEntity savedElection = electionRepository.save(oldElection);

            if(savedElection.getIsOpen()){
                taskSchedulerService.cancelTask(savedElection.getElectionId());
                taskSchedulerService.cancelTask(savedElection.getElectionId()+savedElection.getElectionId());

            }else{
                updateElectionTimingService.updateVotingLinkDistributionTimer(oldElection);
                updateElectionTimingService.updateElectionResultTimer(oldElection, user.getEmail());
            }





            return ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "Election is updated successfully", "electionInfo", createResponseUtil.createElectionInfoMap(savedElection, true)));

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    public ResponseEntity updateElectionName(String electionId, String newElectionName, String signedInUserId) throws Exception {
//        try {
//            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
//
//            if (!election.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
//            }
//
//            if (!election.get().getCreatorId().equals(signedInUserId)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            if (!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createResponseUtil
//                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
//            }
//
//            election.get().setElectionName(newElectionName);
//            ElectionEntity savedElection = electionRepository.save(election.get());
//
//            if (savedElection != null) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(createResponseUtil.createResponseBody(true, "Election name changed successfully"));
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(createResponseUtil.createResponseBody(false, "Election name could not be changed"));
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }
//
//    public ResponseEntity updateElectionDescription(String electionId, String newElectionDescription, String signedInUserId) throws Exception {
//        try {
//            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
//            if (!election.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
//            }
//
//            if (!election.get().getCreatorId().equals(signedInUserId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createResponseUtil.createResponseBody(false, "This election is not owned by current user"));
//            }
//
//            if (!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createResponseUtil
//                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
//            }
//
//            election.get().setElectionDescription(newElectionDescription);
//
//            ElectionEntity savedElection = electionRepository.save(election.get());
//
//            if (savedElection != null) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(createResponseUtil.createResponseBody(true, "Election description changed successfully"));
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(createResponseUtil.createResponseBody(false, "Election description could not be changed"));
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }
//
//    public ResponseEntity updateElectionPollType(String electionId, String pollType, String signedInUserId) throws Exception {
//        try {
//            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
//
//            if (!election.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
//            }
//
//            if (!election.get().getCreatorId().equals(signedInUserId)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            if (!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createResponseUtil
//                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
//            }
//
//            if (election.get().getElectionLayout().getPollType().equals(pollType)) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(createResponseUtil.createResponseBody(false, "Election poll type is already set to " + pollType));
//            }
//
//            election.get().getElectionLayout().setPollType(pollType);
//
//            ElectionEntity savedElection = electionRepository.save(election.get());
//
//            if (savedElection != null) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(createResponseUtil.createResponseBody(true, "Election poll type updated successfully to " + pollType));
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(createResponseUtil.createResponseBody(false, "Election poll type could not be updated"));
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }
//
//    public ResponseEntity updateElectionCardId(String electionId, String electionCardId, String signedInUserId) throws Exception {
//        try {
//            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
//
//            if (!election.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
//            }
//
//            if (!election.get().getCreatorId().equals(signedInUserId)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            if (!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createResponseUtil
//                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
//            }
//
//            if (election.get().getElectionLayout().getElectionCardId().equals(electionCardId)) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body(createResponseUtil.createResponseBody(false, "Election card id is already set to " + electionCardId));
//            }
//
//            election.get().getElectionLayout().setElectionCardId(electionCardId);
//
//            ElectionEntity savedElection = electionRepository.save(election.get());
//
//            if (savedElection != null) {
//                return ResponseEntity.status(HttpStatus.OK)
//                        .body(createResponseUtil.createResponseBody(true, "Election card id updated successfully to " + electionCardId));
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(createResponseUtil.createResponseBody(false, "Election card id could not be updated"));
//            }
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }
}
