package io.github.ballotguard.services.vote;

import io.github.ballotguard.entities.election.ElectionEntity;
import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.repositories.ElectionRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.VotingStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class CastVoteService {

    @Autowired
    private VotingStringUtil votingStringUtil;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    public ResponseEntity castVote(String votingString, String optionId) throws Exception {

        String[] castingInfo = votingStringUtil.decrypt(votingString);
        String electionId = castingInfo[0];
        String voterId = castingInfo[1];

        Optional<ElectionEntity> election = electionRepository.findByElectionId(electionId);

        if (!election.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(createResponseUtil.createResponseBody(false, "Election not found in database"));
        }

        if (Instant.now().isBefore(Instant.ofEpochMilli(election.get().getStartTime()))) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(createResponseUtil.createResponseBody(false, "Election has not started yet"));
        }

        if (Instant.now().isAfter(Instant.ofEpochMilli(election.get().getEndTime()))) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(createResponseUtil.createResponseBody(false, "Election has ended"));
        }

        for (Voter voter : election.get().getVoters()) {
            if (voterId.equals(voter.getVoterId()) && !voter.isHasVoted()) {
                voter.setHasVoted(true);
                election.get().getVoteCount().put(optionId, election.get().getVoteCount().get(optionId) + 1);
                election.get().setTotalVotes(election.get().getTotalVotes() + 1);
                electionRepository.save(election.get());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(createResponseUtil.createResponseBody(true, "Successfully casted vote"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createResponseUtil.createResponseBody(false, "This voter is not valid in this election"));
    }
}
