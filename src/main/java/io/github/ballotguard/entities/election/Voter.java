package io.github.ballotguard.entities.election;

import java.util.UUID;

public class Voter {
    private String voterEmail;
    private String uniqueString;
    private boolean hasVoted;

    Voter() {
        uniqueString = UUID.randomUUID().toString();
    }
}
