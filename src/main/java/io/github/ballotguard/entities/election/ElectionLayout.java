package io.github.ballotguard.entities.election;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ElectionLayout {
    private String pollType;
    private String electionCardId;
    private ArrayList<Option> electionOptions;
}
