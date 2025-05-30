package io.github.ballotguard.entities.election;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Voter {

    @NonNull
    private String voterEmail;

    private String uniqueString;
    private boolean hasVoted;

}
