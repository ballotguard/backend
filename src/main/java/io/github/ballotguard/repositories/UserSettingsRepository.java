package io.github.ballotguard.repositories;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.entities.user.UserSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserSettingsRepository extends MongoRepository<UserSettingsEntity, String> {
    Optional<UserSettingsEntity> findById(String uid);
}
