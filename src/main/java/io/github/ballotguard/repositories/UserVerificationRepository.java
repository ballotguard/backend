package io.github.ballotguard.repositories;

import io.github.ballotguard.entities.user.UserVerificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVerificationRepository extends MongoRepository<UserVerificationEntity, String> {

}
