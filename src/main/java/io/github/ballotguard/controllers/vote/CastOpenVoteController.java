package io.github.ballotguard.controllers.vote;

import com.nimbusds.jose.shaded.gson.Gson;
import io.github.ballotguard.services.vote.CastOpenVoteService;
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
@RequestMapping("/api/v1/public/vote/open")
public class CastOpenVoteController {

    @Autowired
    private CastOpenVoteService castOpenVoteService;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Transactional
    @PutMapping("cast")
    public ResponseEntity castSingleOptionVoteUsingUniqueLink(@RequestBody Map<String, Object> requestBody){
        try{
            String electionId = (String) requestBody.get("electionId");
            String optionId = (String) requestBody.get("optionId");


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

            return castOpenVoteService.castVote(electionId, optionId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while casting vote. This user can try again."));
        }
    }

    @Transactional
    @PutMapping("cast/multi")
    public ResponseEntity castMultiOptionVoteUsingUniqueLink(@RequestBody Map<String, Object> requestBody){
        try{
            String electionId = (String) requestBody.get("electionId");

            Gson gson = new Gson();
            // Convert JSON string to String array
            String[] optionIds = gson.fromJson((String) requestBody.get("optionIds"), String[].class);


            if(electionId == null || electionId.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Election Id is empty"));
            }

            if(optionIds == null || optionIds.length == 0){
                return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Option Ids is empty"));
            }

            return castOpenVoteService.castMultiVote(electionId, optionIds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while casting vote. This user can try again."));
        }
    }
}
