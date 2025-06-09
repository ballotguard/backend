package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.services.vote.SendResultEmailService;
import io.github.ballotguard.services.vote.SendVotingLinkEmailService;
import io.github.ballotguard.services.vote.TaskSchedulerService;
import io.github.ballotguard.utilities.CreateResponseUtil;
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
public class UpdateElectionTimingService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private SendVotingLinkEmailService sendVotingLinkEmailService;

    @Autowired
    private SendResultEmailService sendResultEmailService;
    @Autowired
    private UserRepository userRepository;


    public ResponseEntity updateElectionStartTime(String electionId, long newStartTime, String signedInUserId) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(!election.get().getCreatorId().equals(signedInUserId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            if(!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            if (!Instant.ofEpochMilli(newStartTime).isAfter(Instant.now().plus(Duration.ofMinutes(20)) )) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election creation and star time must have at least 20 minutes difference"));
            }

            election.get().setStartTime(newStartTime);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection != null){
                taskSchedulerService.scheduleElectionTask(savedElection.getElectionId(), savedElection.getStartTime(), true,() -> {
                    try {
                        sendVotingLinkEmailService.sendVotingLinkToAllVoters(
                                savedElection.getVoters(),
                                savedElection.getStartTime(),
                                savedElection.getEndTime(),
                                savedElection.getElectionName(),
                                savedElection.getElectionDescription(),
                                savedElection.getElectionId()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Start time updated successfully"));

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Start time could not be updated"));
            }

        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity updateElectionEndTime(String electionId, long newEndTime, String signedInUserId) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(!election.get().getCreatorId().equals(signedInUserId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            if(!Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            if (!Instant.ofEpochMilli(newEndTime).isAfter(Instant.now().plus(Duration.ofMinutes(20)) )) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election creation and end time must have at least 20 minutes difference"));
            }

            election.get().setEndTime(newEndTime);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection != null){
                UserEntity user = userRepository.findById(election.get().getCreatorId()).get();
                if(user != null){
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


                    return ResponseEntity.status(HttpStatus.OK)
                            .body(createResponseUtil.createResponseBody(true, "End time updated successfully"));
                }else{
                    throw new Exception("Election owner could not be found in database");
                }


            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "End time could not be updated"));
            }

        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
