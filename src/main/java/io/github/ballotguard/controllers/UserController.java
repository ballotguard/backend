package io.github.ballotguard.controllers;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.services.UserService;
import io.github.ballotguard.utilities.GetAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GetAuthenticatedUser getAuthenticatedUser;

    @GetMapping("/user/logged-in-user-info")
    public ResponseEntity<UserEntity> getLoggedInUserInfo() {
        try{
            Optional<UserEntity> authenticatedUser =  getAuthenticatedUser.GetAuthenticatedUser();
            if(authenticatedUser.isPresent()) {
                return ResponseEntity.ok(authenticatedUser.get());
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
