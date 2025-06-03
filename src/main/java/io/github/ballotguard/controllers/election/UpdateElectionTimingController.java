package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.election.UpdateElectionTimingService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/election/update")
@Slf4j
public class UpdateElectionTimingController {
    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private UpdateElectionTimingService updateElectionTimingService;

    @PutMapping("start-time")
    public ResponseEntity updateElectionStartTime(@RequestBody Map<String, Object> requestBody){

        try{
            long newStartTime = Long.parseLong(requestBody.get("startTime").toString());
            String electionId = (String) requestBody.get("electionId");

            if(electionId != null || electionId.isEmpty() ) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionTimingService.updateElectionEndTime(electionId, newStartTime);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating voters"));
        }
    }

    @PutMapping("end-time")
    public ResponseEntity updateElectionEndTime(@RequestBody Map<String, Object> requestBody){

        try{
            long newEndTime = Long.parseLong(requestBody.get("endTime").toString());
            String electionId = (String) requestBody.get("electionId");

            if(electionId != null || electionId.isEmpty() ) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return updateElectionTimingService.updateElectionEndTime(electionId, newEndTime);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating voters"));
        }
    }
}
