package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public Map createResponseBody(boolean success, String message, String email){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("email", email);
        return response;
    }

//    public Map createResponseBody(boolean success, String message, String error){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("error", error);
//        return response;
//    }

    public Map createResponseBody(boolean success, String message, Map data){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

//    public Map createResponseBody(boolean success, String message , Map userinfo){
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", success);
//        response.put("message", message);
//        response.put("data", data);
//        return response;
//    }

    public Map createResponseBody(boolean success, String message, String email, Map data){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("email", email);
        response.put("data", data);
        return response;
    }

    public Map createResponseBody(boolean success, String message, String dataName, String data){
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put(dataName, data);
        return response;
    }

    public Map createMap(String dataName, String data){
        Map<String, Object> response = new HashMap<>();
        response.put(dataName, data);
        return response;
    }

    public Map createMap(String dataName, ArrayList data){
        Map<String, Object> response = new HashMap<>();
        response.put(dataName, data);
        return response;
    }

    public Map createMap(String dataName1, String data1, String dataName2, String data2){
        Map<String, Object> response = new HashMap<>();
        response.put(dataName1, data1);
        response.put(dataName2, data2);
        return response;
    }

    public Map createUserinfoResponse(UserEntity user){
       try{
               Map<String, Object> response = new HashMap<>();
               response.put("email", user.getEmail());
               response.put("firstName", user.getFirstName());
               response.put("lastName", user.getLastName());
               response.put("isVerified", user.isVerified());
               response.put("isAccountEnabled", user.isAccountEnabled());
               return response;

       }catch(Exception e){
           log.error(e.getMessage());
           return null;
       }
    }



}
