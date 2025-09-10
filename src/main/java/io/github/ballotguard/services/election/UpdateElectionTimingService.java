package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
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


    public void updateVotingLinkDistributionTimer(ElectionEntity election) throws Exception {
        try{


                taskSchedulerService.scheduleElectionTask(election.getElectionId(), election.getStartTime(), true,() -> {
                    try {
                        sendVotingLinkEmailService.sendVotingLinkToAllVoters(
                                election.getVoters(),
                                election.getStartTime(),
                                election.getEndTime(),
                                election.getElectionName(),
                                election.getElectionDescription(),
                                election.getElectionId()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "Start time updated successfully"));


        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public void updateElectionResultTimer(ElectionEntity election, String signedInUserEmail) throws Exception {
        try{


                    taskSchedulerService.scheduleElectionTask(
                            election.getElectionId()+election.getElectionId(),
                            election.getEndTime(),
                            false,
                            () -> {
                                try {
                                    Optional<ElectionEntity> finalElectionOpt = electionRepository.findByElectionId(election.getElectionId());
                                    if (finalElectionOpt.isPresent()) {
                                        ElectionEntity finalElection = finalElectionOpt.get();
                                        sendResultEmailService.sendResultSummaryToAllVotersAndElectionCreator(
                                                finalElection.getVoters(),
                                                signedInUserEmail,
                                                createResponseUtil.createElectionResultMap(finalElection)
                                        );
                                    } else {
                                        log.error("Election not found at result sending time: " + election.getElectionId());
                                    }
                                } catch (Exception e) {
                                    log.error("Error while sending final results email", e);
                                    throw new RuntimeException(e);
                                }
                            });


            ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "End time updated successfully"));


        } catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
