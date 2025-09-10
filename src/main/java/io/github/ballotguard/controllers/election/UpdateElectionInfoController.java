package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.services.election.UpdateElectionInfoService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/user/election/update")
@Slf4j
public class UpdateElectionInfoController {
    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private UpdateElectionInfoService updateElectionInfoService;

    @Autowired
    private ElectionRepository electionRepository;


    @Transactional
    @PatchMapping
    public ResponseEntity updateElection(@RequestBody ElectionEntity newElection) throws Exception {
        try{
            // newElection name

            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
            Optional<ElectionEntity> oldElection = electionRepository.findByElectionId(newElection.getElectionId());

            if(oldElection.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "No election with this id exists"));
            }

            if(!authenticatedUser.getUserElectionsId().contains(newElection.getElectionId()) || !oldElection.get().getCreatorId().equals(authenticatedUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }


            return updateElectionInfoService.updateElectionInfo(authenticatedUser, oldElection.get(), newElection);


        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating election"));
        }

    }

//    @Transactional
//    @PatchMapping("name")
//    public ResponseEntity updateElectionName(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            String electionId = (String) requestBody.get("electionId");
//            String newElectionName = requestBody.get("newElectionName").toString();
//            if(electionId == null || electionId.isEmpty() || newElectionName == null || newElectionName.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id or election name or both is empty"));
//            }
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//           if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionInfoService.updateElectionName(electionId, newElectionName, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election name"));
//        }
//    }
//
//    @Transactional
//    @PatchMapping("description")
//    public ResponseEntity updateElectionDescription(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            String electionId = (String) requestBody.get("electionId");
//            String newElectionDescription = requestBody.get("newElectionDescription").toString();
//
//            if(electionId == null || electionId.isEmpty() || newElectionDescription == null || newElectionDescription.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id or election description or both is empty"));
//            }
//         UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionInfoService.updateElectionDescription(electionId, newElectionDescription, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election description"));
//        }
//    }
//
//    @Transactional
//    @PatchMapping("poll-type")
//    public ResponseEntity updateElectionPollType(@RequestBody Map<String, Object> requestBody){
//
//        try{
//
//            String electionId = (String) requestBody.get("electionId");
//            String newPollType = (String) requestBody.get("newPollType");
//
//            if(electionId == null || electionId.isEmpty() || newPollType == null || newPollType.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id or poll type or both is empty"));
//            }
//
//            if( newPollType.equals("radio") && newPollType.equals("checkbox")) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Poll type is invalid"));
//            }
//
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionInfoService.updateElectionPollType(electionId, newPollType, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election poll type"));
//        }
//    }
//
//    @Transactional
//    @PatchMapping("card-id")
//    public ResponseEntity updateElectionCardId(@RequestBody Map<String, Object> requestBody){
//
//        try{
//
//            String electionId = (String) requestBody.get("electionId");
//            String newElectionCardId = (String) requestBody.get("newElectionCardId");
//
//            if(electionId == null || electionId.isEmpty() || newElectionCardId == null || newElectionCardId.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id or election card id or both is empty"));
//            }
//
//            try{
//                if( Integer.parseInt(newElectionCardId) <0 && Integer.parseInt(newElectionCardId)>5) {
//                    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                            .body(createResponseUtil.createResponseBody(false, "Election card id is invalid"));
//                }
//            }catch (java.lang.NumberFormatException e) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election card id is invalid"));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionInfoService.updateElectionCardId(electionId, newElectionCardId, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while changing the election card id"));
//        }
//    }
}
