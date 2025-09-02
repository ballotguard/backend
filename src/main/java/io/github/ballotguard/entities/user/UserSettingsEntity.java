package io.github.ballotguard.entities.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user-settings-data")
@Getter
@Setter
@AllArgsConstructor
public class UserSettingsEntity {
    @Id
    String id;

    private String userEmail;
    private String preferredLanguage;
    private String preferredTheme;
    private boolean pushNotificationsEnabled;
    private boolean emailNotificationsEnabled;

}
