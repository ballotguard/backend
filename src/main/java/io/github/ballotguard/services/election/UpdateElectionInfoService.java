package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.ElectionLayout;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import io.github.ballotguard.utilities.MatchTextPatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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


    ResponseEntity updateElectionName(String electionId, String newElectionName) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(election.get().getElectionStartingTime().after(Timestamp.from(Instant.now().minus(Duration.ofMinutes(20))))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            election.get().setElectionName(newElectionName);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election name changed successfully"));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election name could not be changed"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    ResponseEntity updateElectionDescription(String electionId, String newElectionDescription) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }
            if(election.get().getElectionStartingTime().after(Timestamp.from(Instant.now().minus(Duration.ofMinutes(20))))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            election.get().setElectionDescription(newElectionDescription);

            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election description changed successfully"));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election description could not be changed"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    ResponseEntity updateElectionLayout(String electionId, ElectionLayout newElectionLayout) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(election.get().getElectionStartingTime().after(Timestamp.from(Instant.now().minus(Duration.ofMinutes(20))))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            election.get().setElectionLayout(newElectionLayout);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election layout updated successfully"));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election layout could not be updated"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
