package io.github.ballotguard.controllers.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @GetMapping("user")
    public ResponseEntity<Map<String, Object>> getLoggedInUserInfo() {
        try{
            Optional<UserEntity> authenticatedUser =  getAuthenticatedUserUtil.getAuthenticatedUser();
            if(authenticatedUser.isPresent()) {
                Map<String, Object> response= new HashMap<>();
                response.put("email", authenticatedUser.get().getEmail());
                response.put("firstName", authenticatedUser.get().getFirstName());
                response.put("lastName", authenticatedUser.get().getLastName());
                response.put("verified", authenticatedUser.get().isVerified());
                response.put("enabled", authenticatedUser.get().isAccountEnabled());
                return ResponseEntity.status(HttpStatus.OK).body(createResponseUtil.createResponseBody(true, "User found", response));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponseUtil.createResponseBody(false, "User does not exist"));
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));
        }
    }

}
