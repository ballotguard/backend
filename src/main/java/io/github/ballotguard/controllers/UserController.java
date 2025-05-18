package io.github.ballotguard.controllers;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.services.UserService;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @GetMapping("/user/logged-in-user-info")
    public ResponseEntity<Map<String, Object>> getLoggedInUserInfo() {
        try{
            Optional<UserEntity> authenticatedUser =  getAuthenticatedUserUtil.getAuthenticatedUser();
            if(authenticatedUser.isPresent()) {
                Map<String, Object> response= new HashMap<>();
                response.put("email", authenticatedUser.get().getEmail());
                response.put("firstName", authenticatedUser.get().getFirstName());
                response.put("lastName", authenticatedUser.get().getLastName());
                response.put("verified", authenticatedUser.get().isVerified());
                response.put("enabled", authenticatedUser.get().isEnabled());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
