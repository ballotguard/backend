package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.services.election.CreateAndDeleteElectionService;
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
@RequestMapping("/api/v1/user/election")
@Slf4j
public class CreateAndDeleteElectionController {
    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private CreateAndDeleteElectionService createAndDeleteElectionService;

    @Autowired
    private ElectionRepository electionRepository;

    @Transactional
    @PostMapping("create")
    public ResponseEntity createNewElection(@RequestBody ElectionEntity election) {
        try {

            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();
            if(!authenticatedUser.isVerified()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User is not verified"));
            }

                return createAndDeleteElectionService.creatElection(election, authenticatedUser);

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while creating election"));
        }
    }

    @Transactional
    @DeleteMapping("delete")
    public ResponseEntity deleteElection(@RequestBody Map<String, Object> requestBody) {

        try {
            String electionId = (String) requestBody.get("electionId");

            if(electionId == null || electionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.isVerified()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User is not verified"));
            }

            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if (!election.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(!authenticatedUser.getUserElectionsId().contains(electionId) || !election.get().getCreatorId().equals(authenticatedUser.getUserElectionsId())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }


            return createAndDeleteElectionService.deleteElection(electionId, authenticatedUser);

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while deleting election."));
        }
    }

}
