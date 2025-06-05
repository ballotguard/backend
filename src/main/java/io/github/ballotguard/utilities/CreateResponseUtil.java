package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Option;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.entities.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CreateResponseUtil {

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

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

    public Map createElectionInfoMap(ElectionEntity election){
        try{
            Map<String, Object> electionMap = new HashMap<>();
            Map<String, Object> electionLayoutMap = new HashMap<>();
            Map<String, Object> votersMap = new HashMap<>();
            ArrayList<Map> votersArrayList = new ArrayList<>();
            ArrayList<Map> optionsArrayList = new ArrayList<>();


            electionMap.put("electionId", election.getElectionId());
            electionMap.put("electionName", election.getElectionName());
            electionMap.put("electionDescription", election.getElectionDescription());
            electionMap.put("startTime", election.getStartTime());
            electionMap.put("endTime", election.getEndTime());
            electionLayoutMap.put("pollType", election.getElectionLayout().getPollType());
            electionLayoutMap.put("electionCardId", election.getElectionLayout().getElectionCardId());
            electionMap.put("electionLayout", electionLayoutMap);
            for(Option option : election.getOptions()){
                optionsArrayList.add(createMap("optionName", option.getOptionName()));
            }
            electionMap.put("options", optionsArrayList);

            for(Voter voter : election.getVoters()){
                optionsArrayList.add(createMap("optionName", voter.getVoterEmail()));
            }
            electionMap.put("voters", votersArrayList);

            return electionMap;

        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }



}
