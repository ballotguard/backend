package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ElectionService {
    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    ResponseEntity creatElection(ElectionEntity election) throws Exception {
        try{
           if(election.getElectionName().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election name cannot be empty"));

           }else if(election.getElectionName().length()>30){
               return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                       .body(createResponseUtil.createResponseBody(false, "Max election nane length is 30"));

           }else if (election.getElectionCreationTime().before(Timestamp.from(Instant.now().minus(Duration.ofMinutes(2))))){
               return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                       .body(createResponseUtil.createResponseBody(false, "Election cannot start before current time"));

           }else if(election.getElectionLayout().getElectionCardId().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election card id cannot be empty"));

           }else if(election.getElectionLayout().getPollType()==null){
               return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                       .body(createResponseUtil.createResponseBody(false, "Election poll type cannot be empty"));
           }else if(election.getElectionLayout().getPollType()!="CHECKBOX" && election.getElectionLayout().getPollType()!="RADIO"){
               return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                       .body(createResponseUtil.createResponseBody(false, "Election poll type is invalid"));
           }

           for(Option option : election.getOptions()){
               if(option.getOptionName() == null || option.getOptionName().isEmpty() ){
                   return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                           .body(createResponseUtil.createResponseBody(false, "Option name cannot be empty"));
               }else if(option.getOptionName().length()>30){
                   return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                           .body(createResponseUtil.createResponseBody(false, "Option name cannot more than 30 characters"));
               }
           }

           if(election.getVoters().stream().anyMatch(voter -> voter.getVoterEmail()==null || voter.getVoterEmail().isEmpty())){
               return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                       .body(createResponseUtil.createResponseBody(false, "Voter name cannot be empty"));
           }

           election.setElectionId(UUID.randomUUID().toString());
           election.setCreatorId(getAuthenticatedUserUtil.getAuthenticatedUser().get().getUserId());
           election.setElectionCreationTime(Timestamp.from(Instant.now()));
           election.setResultLinkString(UUID.randomUUID().toString());
           election.setTotalVotes((long)0);

           for(Voter voter : election.getVoters()){
               voter.setHasVoted(false);
               voter.setUniqueString(UUID.randomUUID().toString());
           }

           Optional<ElectionEntity> savedElection = electionRepository.save(election);
           if(savedElection.isPresent()){
               return ResponseEntity.status(HttpStatus.CREATED).body(createResponseUtil.createResponseBody(true, "Election eas created successfully", (Map) savedElection.get()));
           }else{
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(createResponseUtil.createResponseBody(false, "Election could not be created"));
           }

        }catch(Exception e){
            throw new Exception();
        }
    }
}
