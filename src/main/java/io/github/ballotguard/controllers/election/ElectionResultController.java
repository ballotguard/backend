package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.election.ElectionResultService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/election/")
@Slf4j
public class ElectionResultController {

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private ElectionResultService electionResultService;

    @GetMapping("result")
    public ResponseEntity getElectionResult(@RequestBody Map<String, Object> requestBody) {

        UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
        if(!authenticatedUser.isVerified()){
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(createResponseUtil.createResponseBody(false, "User is not verified"));
        }

        String electionId = requestBody.get("electionId").toString();
        if(electionId == null || electionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
        }

        return electionResultService.getElectionResult(electionId);

    }

}
