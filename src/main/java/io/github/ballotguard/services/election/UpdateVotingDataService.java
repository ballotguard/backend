package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.ElectionLayout;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
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
import java.util.*;

@Slf4j
@Service
public class UpdateVotingDataService {
    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;


    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;



    ResponseEntity updateVoters(String electionId, ArrayList<Voter> newVoters) throws Exception {
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

            for(Voter voter : newVoters){

                if(voter.getVoterEmail()==null || voter.getVoterEmail().isEmpty()){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Voter email cannot be empty"));
                }else if(!matchTextPatternUtil.isValidEmail(voter.getVoterEmail())){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "One or all of the voter's email is invalid"));
                }

                voter.setHasVoted(false);
                voter.setUniqueString(UUID.randomUUID().toString());
            }


            election.get().setVoters(newVoters);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Voters added successfully"));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Voters could not be added"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    ResponseEntity updateOptions(String electionId, ArrayList<Option> newOptions) throws Exception {
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

            for(Option option : newOptions){
                if(option.getOptionName() == null || option.getOptionName().isEmpty() ){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot be empty"));
                }else if(option.getOptionName().length()>30){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "Option name cannot more than 30 characters"));
                }

                option.setOptionId(UUID.randomUUID().toString());
            }

            election.get().setOptions(newOptions);
            ElectionEntity savedElection = electionRepository.save(election.get());

            if(savedElection!=null){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Options updated successfully"));

            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Options could not be updated"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }




}
