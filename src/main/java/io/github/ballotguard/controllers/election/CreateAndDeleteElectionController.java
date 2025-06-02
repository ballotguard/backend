package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.election.CreateAndDeleteElectionService;
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
@RequestMapping("election")
@Slf4j
public class CreateAndDeleteElectionController {
    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private CreateAndDeleteElectionService createAndDeleteElectionService;

    @GetMapping("create")
    public ResponseEntity createNewElection(@RequestBody ElectionEntity election) {
        try {
            Optional<UserEntity> authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
            if (authenticatedUser.isPresent()) {
                return createAndDeleteElectionService.creatElection(election, authenticatedUser.get());
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Authentication information is invalid"));
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while creating election."));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity deleteElection(@RequestBody Map<String, Object> requestBody) {

        try {
            String electionId = (String) requestBody.get("electionId");
            if(electionId != null || electionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }
            Optional<UserEntity> authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
            if(!authenticatedUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Authentication information is invalid"));
            }else if(!authenticatedUser.get().getUserElectionsId().contains(electionId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            return createAndDeleteElectionService.deleteElection(electionId, authenticatedUser.get());

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while creating election."));
        }
    }

}
