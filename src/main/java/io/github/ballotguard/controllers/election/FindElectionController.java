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
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
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

    @GetMapping("user/election/find")
    public ResponseEntity getElectionDetailsByIdForElectionOwner(@RequestParam String electionId) {
        try{
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUser.isVerified()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User is not verified"));
            }

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

            return findElectionService.findElectionById(election.get(), true);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching election details."));
        }
    }

    @GetMapping("election/find/open")
    public ResponseEntity getElectionDetailsByIdForOpenElection(@RequestParam String electionId) {
        try{

            if(electionId == null || electionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }

            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if (election.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(election.get().getIsOpen()){
                return findElectionService.findElectionById(election.get(), false);

            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "This election is not public"));
            }




        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching election details."));
        }
    }


    @GetMapping("election/find/voter")
    public ResponseEntity getElectionDetailsByIdForVoter(@RequestParam String electionId, @RequestParam String voterId) {
        try{

            if(electionId == null || electionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
            }

            Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

            if (!election.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
            }

            if(election.get().getVoters().stream().noneMatch(voter -> voter.getVoterId().equals(voterId))){
                log.error("election id "+electionId + " | voter id "+voterId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "This user cannot access this election"));
            }

            if(election.get().getVoters().stream().anyMatch(voter -> voter.getVoterId().equals(voterId) && voter.isHasVoted())){
                log.error("election id "+electionId + " | voter id "+voterId);
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(createResponseUtil.createResponseBody(false, "You have already submitted your vote."));
            }

            if(Instant.ofEpochMilli(election.get().getStartTime()).isAfter(Instant.now())){
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(createResponseUtil.createResponseBody(false, "Election has not started yet"));
            }

            if(Instant.ofEpochMilli(election.get().getEndTime()).isBefore(Instant.now())){
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(createResponseUtil.createResponseBody(false, "Election has already ended"));
            }


            return findElectionService.findElectionById(election.get(), false);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching election details."));
        }
    }

    @GetMapping("user/election/find-all")
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
