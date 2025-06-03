package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.election.UpdateElectionInfoService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/election/update")
@Slf4j
public class UpdateElectionInfoController {
    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private UpdateElectionInfoService updateElectionInfoService;

    @PutMapping("name")
    public ResponseEntity updateElectionName(@RequestBody Map<String, Object> requestBody){

        try{
            String electionId = (String) requestBody.get("electionId");
            String electionName = requestBody.get("electionName").toString();
            if(electionId != null || electionId.isEmpty() || electionName != null || electionName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id or election name is empty"));
            }
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

           if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionInfoService.updateElectionName(electionId, electionName);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election name"));
        }
    }

    @PutMapping("description")
    public ResponseEntity updateElectionDescription(@RequestBody Map<String, Object> requestBody){

        try{
            String electionId = (String) requestBody.get("electionId");
            String electionDescription = requestBody.get("electionDescription").toString();

            if(electionId != null || electionId.isEmpty() || electionDescription != null || electionDescription.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id or election description is empty"));
            }
         UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionInfoService.updateElectionName(electionId, electionDescription);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election description"));
        }
    }

    @PutMapping("poll-type")
    public ResponseEntity updateElectionPollType(@RequestBody Map<String, Object> requestBody){

        try{

            String electionId = (String) requestBody.get("electionId");
            String pollType = requestBody.get("pollType").toString();

            if(electionId != null || electionId.isEmpty() || pollType != null || pollType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id or election name && election card id both is empty"));
            }

            if( pollType != "radio" && pollType != "checkbox") {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Poll type is invalid"));
            }

            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionInfoService.updateElectionName(electionId, pollType);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election description"));
        }
    }

    @PutMapping("polltype")
    public ResponseEntity updateElectionCardId(@RequestBody Map<String, Object> requestBody){

        try{

            String electionId = (String) requestBody.get("electionId");
            String electionCardId = requestBody.get("electionCardId").toString();

            if(electionId != null || electionId.isEmpty() || electionCardId != null || electionCardId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id or election name && election card id both is empty"));
            }

            try{
                if( Integer.parseInt(electionCardId) <0 && Integer.parseInt(electionCardId)>5) {
                    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                            .body(createResponseUtil.createResponseBody(false, "Election card id is invalid"));
                }
            }catch (java.lang.NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election card id is invalid"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionInfoService.updateElectionName(electionId, electionCardId);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election description"));
        }
    }
}
