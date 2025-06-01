package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CreateElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;

    ResponseEntity creatElection(ElectionEntity election, UserEntity user) throws Exception {
        try{
            if(election.getElectionName().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election name cannot be empty"));

            }else if(election.getElectionName().length()>30){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Max election nane length is 30"));

            }else if (!election.getElectionStartingTime().after(Timestamp.from(Instant.now().plus(Duration.ofMinutes(20))))){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(createResponseUtil.createResponseBody(false, "Election must start at least 20 minutes after creation"));

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

                option.setOptionId(UUID.randomUUID().toString());
            }

            for(Voter voter : election.getVoters()){

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


            election.setElectionId(UUID.randomUUID().toString());
            election.setCreatorId(getAuthenticatedUserUtil.getAuthenticatedUser().get().getUserId());
            election.setElectionCreationTime(Timestamp.from(Instant.now()));
            election.setResultLinkString(UUID.randomUUID().toString());
            election.setTotalVotes((long)0);


            ElectionEntity savedElection = electionRepository.save(election);
            if(savedElection!=null){
                ArrayList<String> userElectionIds = user.getUserElectionsId();
                userElectionIds.add(savedElection.getElectionId());
                user.setUserElectionsId(userElectionIds);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(createResponseUtil.createResponseBody(true, "Election eas created successfully", (Map) savedElection));
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createResponseUtil.createResponseBody(false, "Election could not be created"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

}
