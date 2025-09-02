package io.github.ballotguard.controllers.vote;

import io.github.ballotguard.services.vote.CastVoteService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/vote")
public class CastVoteController {

    @Autowired
    private CastVoteService castVoteService;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Transactional
    @PutMapping("cast")
    public ResponseEntity castVoteUsingUniqueLink(@RequestBody Map<String, Object> requestBody){
        try{
            String electionId = (String) requestBody.get("electionId");
            String voterId = (String) requestBody.get("voterId");
            String optionId = (String) requestBody.get("optionId");

            if(voterId == null || voterId.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Voter Id is empty"));
            }

            if(electionId == null || electionId.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election Id is empty"));
            }

            if(optionId == null || optionId.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Option Id is empty"));
            }

            return castVoteService.castVote(electionId, voterId, optionId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while casting vote. This user can try again."));
        }
    }
}
