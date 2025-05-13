package io.github.ballotguard.controllers;


import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.services.UserVerificationService;
import io.github.ballotguard.utilities.GetAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class EmailVerificationController {

    @Autowired
    private GetAuthenticatedUser getAuthenticatedUser;

    @Autowired
    private UserVerificationService userVerificationService;


    @PostMapping("/verify/send-email-verification-code")
    public ResponseEntity sendVerificationCode() {
        try{
            Optional<UserEntity> userEntity = getAuthenticatedUser.GetAuthenticatedUser();

            if(userEntity.isPresent()){
                if(userEntity.get().isVerified()){
                    Map<String, Object> responseBody= new HashMap<>();
                    responseBody.put("message", "This user is already verified");
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseBody);
                }
                return userVerificationService.sendVerificationCodeEmail(userEntity.get(),
                        "Your email verification code",
                        "Use this code to verify your email in Ballotguard");
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verify/verify-email-verification-code")
    public ResponseEntity verifyVerificationCode(@RequestBody Map<String, Object> requestBody) {
        String verificationCode = (String) requestBody.get("verificationCode");
        try{
            Optional<UserEntity> userEntity = getAuthenticatedUser.GetAuthenticatedUser();
            if(userEntity.isPresent()){
                return userVerificationService.verifyVerificationCode(userEntity.get(), verificationCode, false);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
