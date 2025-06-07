package io.github.ballotguard.controllers.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.ElectionRepository;
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
@RequestMapping("/api/v1/user/election")
@Slf4j
public class FindElectionController {

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private FindElectionService findElectionService;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;
    @Autowired
    private ElectionRepository electionRepository;

    @GetMapping("find")
    public ResponseEntity getElectionDetailsById(@RequestBody Map<String, Object> requestBody) {
        try{
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

            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if (!election.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(!authenticatedUser.getUserElectionsId().contains(electionId) ){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
            }

            if(!election.get().getCreatorId().equals(authenticatedUser.getUserId())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This election is not owned by current user"));
            }

            return findElectionService.findElectionById(election.get());

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

            if(!authenticatedUser.isVerified()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User is not verified"));
            }

            return findElectionService.findAllElectionByUser(authenticatedUser);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching all election info."));
        }
    }

}
