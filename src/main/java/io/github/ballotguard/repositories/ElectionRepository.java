package io.github.ballotguard.repositories;

import io.github.ballotguard.entities.election.ElectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ElectionRepository extends MongoRepository<ElectionEntity, String> {
    Optional<ElectionEntity> findByElectionId(String electionId);

    Optional<ElectionEntity> save(ElectionEntity election);
}
