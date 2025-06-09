package io.github.ballotguard.services.election;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class ElectionResultService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    public ResponseEntity getElectionResult(String electionId){

        Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

        if(!election.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
        }

        if (!Instant.ofEpochMilli(election.get().getEndTime()).isAfter(Instant.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createResponseUtil
                            .createResponseBody(false, "Election result can only be obtained after the election has ended"));
        }


        return ResponseEntity
                .ok(createResponseUtil.createResponseBody
                        (true, "Election result is generated", "electionResult", createResponseUtil.createElectionResultMap(election.get())));
    }

}
