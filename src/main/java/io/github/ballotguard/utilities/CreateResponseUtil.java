package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CreateResponseUtil {

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Value("${app.cors.allowed-origin}")
    private String corsAllowedOrigin;

    public Map createResponseBody(boolean success, String message){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

//    public Map createResponseBody(boolean success, String message, String email){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("email", email);
//        return response;
//    }

//    public Map createResponseBody(boolean success, String message, String error){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("error", error);
//        return response;
//    }

//    public Map createResponseBody(boolean success, String message, Map data){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("data", data);
//        return response;
//    }

//    public Map createResponseBody(boolean success, String message , Map userinfo){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("data", data);
//        return response;
//    }

//    public Map createResponseBody(boolean success, String message, String email, Map data){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("email", email);
//        response.put("data", data);
//        return response;
//    }

    public Map createResponseBody(boolean success, String message, String dataName, Object data){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put(dataName, data);
        return response;
    }
    public Map createResponseBody(boolean success, String message, String dataName1, Object data1, String dataName2, Object data2, String dataName3, Object data3){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put(dataName1, data1);
        response.put(dataName2, data2);
        response.put(dataName3, data3);
        return response;
    }

    public Map createMap(String dataName, Object data){
        Map<String, Object> response = new HashMap<>();
        response.put(dataName, data);
        return response;
    }

    public Map createMap(String dataName, ArrayList data){
        Map<String, Object> response = new HashMap<>();
        response.put(dataName, data);
        return response;
    }

//    public Map createMap(String dataName1, Object data1, String dataName2, Object data2){
//        Map<String, Object> response = new HashMap<>();
//        response.put(dataName1, data1);
//        response.put(dataName2, data2);
//        return response;
//    }

    public Map createUserInfoMap(UserEntity user){
       try{
               Map<String, Object> userMap = new HashMap<>();
               userMap.put("email", user.getEmail());
               userMap.put("firstName", user.getFirstName());
               userMap.put("lastName", user.getLastName());
               userMap.put("isVerified", user.isVerified());
               userMap.put("isAccountEnabled", user.isAccountEnabled());
               return userMap;

       }catch(Exception e){
           log.error(e.getMessage());
           return null;
       }
    }

    public Map createElectionInfoMap(ElectionEntity election, boolean isElectionOwnersRequest){
        try{
            Map<String, Object> electionMap = new HashMap<>();
            Map<String, Object> electionLayoutMap = new HashMap<>();
            ArrayList<Map> votersArrayList = new ArrayList<>();
            ArrayList<Map> optionsArrayList = new ArrayList<>();

            electionMap.put("electionId", election.getElectionId());
            electionMap.put("electionName", election.getElectionName());
            electionMap.put("electionDescription", election.getElectionDescription());
            electionMap.put("isOpen", election.getIsOpen());
            electionMap.put("startTime", election.getStartTime());
            electionMap.put("endTime", election.getEndTime());
            electionLayoutMap.put("pollType", election.getElectionLayout().getPollType());
            electionLayoutMap.put("electionCardId", election.getElectionLayout().getElectionCardId());
            electionMap.put("electionLayout", electionLayoutMap);

            if(election.getIsOpen()){
                electionMap.put("openElectionLink", corsAllowedOrigin +"/election/open/"+election.getElectionId() );
            }

            for(Option option : election.getOptions()){
                Map<String, Object> optionMap = new HashMap<>();
                optionMap.put("optionName", option.getOptionName());
                optionMap.put("optionId", option.getOptionId());
                optionsArrayList.add(optionMap);

            }
            electionMap.put("options", optionsArrayList);

           if(isElectionOwnersRequest && !election.getIsOpen()){
               for(Voter voter : election.getVoters()){
                   votersArrayList.add(createMap("voterEmail", voter.getVoterEmail()));
               }
               electionMap.put("voters", votersArrayList);
           }

            return electionMap;

        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }



    public Map<String, Object> createElectionResultMap (ElectionEntity election){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("electionName", election.getElectionName());
        resultMap.put("electionId", election.getElectionId());
        resultMap.put("electionDescription", election.getElectionDescription());
        resultMap.put("totalVotes", election.getTotalVotes());

        if(!election.getIsOpen()){
            resultMap.put("totalVoters", election.getVoters().size());
            ArrayList<Map> votersArrayList = new ArrayList<>();
            for(Voter voter : election.getVoters()){
                votersArrayList.add(createMap("voterEmail", voter.getVoterEmail()));
            }
            resultMap.put("voters", votersArrayList);
        }

        ArrayList<Map> optionsArrayList = new ArrayList<>();

        for(Option option : election.getOptions()){
            Map<String, Object> optionMap = new HashMap<>();
            optionMap.put("optionName", option.getOptionName());
            optionMap.put("optionId", option.getOptionId());
            optionMap.put("votes", election.getVoteCount().get(option.getOptionId()));
            optionsArrayList.add(optionMap);

        }
        resultMap.put("options", optionsArrayList);




        return resultMap;
    }



}
