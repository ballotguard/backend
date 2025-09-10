package io.github.ballotguard.controllers.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.entities.user.UserSettingsEntity;
import io.github.ballotguard.services.user.UserSettingsService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @GetMapping
    public ResponseEntity getUserSettings() {
        try{
            UserEntity user = getAuthenticatedUserUtil.getAuthenticatedUser();
            return userSettingsService.getUserSettingsEntity(user.getUserSettingsEntityId());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while fetching user settings"));
        }
    }

    @PutMapping
    public ResponseEntity updateUserSettings(@RequestBody UserSettingsEntity newUserSettingsEntity) {
        try{
            UserEntity user = getAuthenticatedUserUtil.getAuthenticatedUser();
            return userSettingsService.updateUserSettingsEntity(user.getUserSettingsEntityId(), newUserSettingsEntity);

        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while updating user settings"));
        }
    }
}
