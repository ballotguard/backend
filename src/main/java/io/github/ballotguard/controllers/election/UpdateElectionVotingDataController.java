//package io.github.ballotguard.controllers.election;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.github.ballotguard.entities.election.Option;
//import io.github.ballotguard.entities.election.Voter;
//import io.github.ballotguard.entities.user.UserEntity;
//import io.github.ballotguard.services.election.UpdateElectionVotingDataService;
//import io.github.ballotguard.utilities.CreateResponseUtil;
//import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/user/election/update")
//@Slf4j
//public class UpdateElectionVotingDataController {
//    @Autowired
//    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;
//
//    @Autowired
//    private CreateResponseUtil createResponseUtil;
//
//    @Autowired
//    private UpdateElectionVotingDataService updateElectionVotingDataService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Transactional
//    @PatchMapping("voters")
//    public ResponseEntity updateVoters(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            ArrayList<Voter> newVoters = objectMapper.convertValue(
//                    requestBody.get("newVoters"),
//                    new TypeReference<ArrayList<Voter>>() {}
//            );
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
//            return updateElectionVotingDataService.updateVoters(electionId, newVoters, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating voters"));
//        }
//    }
//
//    @Transactional
//    @PatchMapping("options")
//    public ResponseEntity updateOptions(@RequestBody Map<String, Object> requestBody){
//
//        try{
//            ArrayList<Option> newOptions = objectMapper.convertValue(
//                    requestBody.get("newOptions"),
//                    new TypeReference<ArrayList<Option>>() {}
//            );
//            String electionId = (String) requestBody.get("electionId");
//
//            if(electionId == null || electionId.isEmpty() ) {
//                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
//            }
//            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
//
//           if(!authenticatedUser.getUserElectionsId().contains(electionId)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//            }
//
//            return updateElectionVotingDataService.updateOptions(electionId, newOptions, authenticatedUser.getUserId());
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError()
//                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating voters"));
//        }
//    }
//}
