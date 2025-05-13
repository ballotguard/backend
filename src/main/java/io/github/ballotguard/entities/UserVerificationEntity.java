package io.github.ballotguard.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user-verification-data")
@Getter
@Setter
@AllArgsConstructor
public class UserVerificationEntity {

    @Id
    private String id;

    private String userId;
    private String verificationCode;
    private Instant verificationCodeExpirationTime;

}
