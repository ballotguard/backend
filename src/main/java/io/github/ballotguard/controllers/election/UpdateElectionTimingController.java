//package io.github.ballotguard.controllers.election;
//
//import io.github.ballotguard.entities.user.UserEntity;
//import io.github.ballotguard.services.election.UpdateElectionTimingService;
//import io.github.ballotguard.utilities.CreateResponseUtil;
//import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/user/election/update")
//@Slf4j
//public class UpdateElectionTimingController {
//    @Autowired
//    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;
//
//    @Autowired
//    private CreateResponseUtil createResponseUtil;
//
//    @Autowired
//    private UpdateElectionTimingService updateElectionTimingService;
//
//    @Transactional
//    @PatchMapping("start-time")
//    public ResponseEntity updateElectionStartTime(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            long newStartTime = (int) requestBody.get("newStartTime");
//
//            String electionId = (String) requestBody.get("electionId");
//
//            if(electionId == null || electionId.isEmpty() ) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
//            }
//
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionTimingService.updateElectionStartTime(electionId, newStartTime, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating election start time"));
//        }
//    }
//
//    @Transactional
//    @PatchMapping("end-time")
//    public ResponseEntity updateElectionEndTime(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            long newEndTime = (int) requestBody.get("newEndTime");
//            String electionId = (String) requestBody.get("electionId");
//
//            if(electionId == null || electionId.isEmpty() ) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
//            }
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionTimingService.updateElectionEndTime(electionId, newEndTime, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating voters"));
//        }
//    }
//}
