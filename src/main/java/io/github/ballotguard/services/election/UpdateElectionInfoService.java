package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.repositories.ElectionRepository;
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
public class UpdateElectionInfoService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;


    public ResponseEntity updateElectionName(String electionId, String newElectionName) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(Instant.ofEpochSecond(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
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

    public ResponseEntity updateElectionDescription(String electionId, String newElectionDescription) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }
            if(Instant.ofEpochSecond(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
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

    public ResponseEntity updatePollType(String electionId, String pollType) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(Instant.ofEpochSecond(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            if (pollType != "checkbox" && pollType != "radio") {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type is invalid"));
            }

            if(election.get().getElectionLayout().getPollType().equals(pollType)){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type is already set to "+ pollType));
            }


            election.get().getElectionLayout().setPollType(pollType);

            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election poll type updated successfully to "+pollType));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election poll type could not be updated"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity updateElectionCardId(String electionId, String electionCardId) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(Instant.ofEpochSecond(election.get().getStartTime()).isAfter(Instant.now().minus(Duration.ofMinutes(20)))){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil
                                .createResponseBody(false, "Changes to the election aren’t allowed once it is 20 minutes away from starting."));
            }

            election.get().getElectionLayout().setElectionCardId(electionCardId);

            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Election card id updated successfully to" + electionCardId));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election card id could not be updated"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
