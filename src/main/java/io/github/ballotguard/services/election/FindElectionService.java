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

import java.util.*;

@Service
@Slf4j
public class FindElectionService {
    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;


    public ResponseEntity findElectionById(String electionId, String signedInUserId) throws Exception {
        try{
            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);
            if(!election.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }else if(election.get().getCreatorId() != signedInUserId){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil.createResponseBody(false, "This election is not owned by this user"));
            }

            Map<String, Object> electionMap = new HashMap<>();
            electionMap.put("electionId", electionId);
            electionMap.put("electionName", election.get().getElectionName());
            electionMap.put("electionDescription", election.get().getElectionDescription());

            List<String> voterEmails = new ArrayList<>();
            for (Voter voter : election.get().getVoters()) {
                voterEmails.add(voter.getVoterEmail());
            }
            electionMap.put("voters", voterEmails);
            electionMap.put("options",
                    election.get().getOptions().stream()
                            .map(Option::getOptionName)
                            .toArray(String[]::new)
            );
            electionMap.put("electionStartTime", election.get().getStartTime());
            electionMap.put("electionEndTime", election.get().getEndTime());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil.createResponseBody(true, "Election found", electionMap));



        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity findAllElectionByUser(UserEntity user) throws Exception {
        try{
            List<String> userElectionsId = user.getUserElectionsId();

            ArrayList<Map> userElectionList = new ArrayList<>();
            for(String electionId : userElectionsId){
                Optional<ElectionEntity> election = electionRepository.findById(electionId);

                if(election.isPresent() && election.get().getCreatorId() != user.getUserId()) {
                    Map<String, Object> electionMap = new HashMap<>();
                    electionMap.put("electionId", electionId);
                    electionMap.put("electionName", election.get().getElectionName());
                    userElectionList.add(electionMap);
                }

            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(createResponseUtil
                            .createResponseBody(true, "User elections list is found", createResponseUtil.createMap("userElectionList", userElectionList)));

        }catch(Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
