package io.github.ballotguard.entities.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

import java.util.ArrayList;

@Document(collection = "user")
@Getter
@Setter
@AllArgsConstructor
public class UserEntity {

    @Id
    private String userId;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String firstName;
    private String lastName;

    private boolean isVerified;
    private boolean isAccountEnabled;
    private Timestamp userCreationTime;

    private String userVerificationEntityId;
    private String userSettingsEntityId;

    private ArrayList<String> userElectionsId;

    private ArrayList<String> roles;

}