package io.github.ballotguard.entities.election;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Map;

@Document(collection = "election")
@Getter
@Setter
@AllArgsConstructor
public class ElectionEntity {
    @Id
    private String id;

    @NonNull
    private String creatorId;

    @NonNull
    private ElectionLayout electionLayout;

    private ArrayList<Option> options;

    private ArrayList<Voter> voters;

    private Map<Long, Long> voteCount;

    private Long totalVotes;

    private String resultLinkString;


}
