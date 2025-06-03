package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.election.FindElectionService;
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
import java.util.Optional;

@RestController
@RequestMapping("/election")
@Slf4j
public class FindElectionController {

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private FindElectionService findElectionService;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @GetMapping("find")
    public ResponseEntity getElectionDetailsById(@RequestBody Map<String, Object> requestBody) {
        try{
            String electionId = requestBody.get("electionId").toString();
            if(electionId != null || electionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return findElectionService.findElectionById(electionId, authenticatedUser.getUserId());

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching election details."));
        }
    }

    @GetMapping("find-all")
    public ResponseEntity getAllElectionInfoByUser() {
        try{

            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            return findElectionService.findAllElectionByUser(authenticatedUser);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching all election info."));
        }
    }

}
