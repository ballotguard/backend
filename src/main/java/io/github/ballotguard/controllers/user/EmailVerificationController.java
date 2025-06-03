package io.github.ballotguard.controllers.user;


import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.user.UserVerificationService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private CreateResponseUtil createResponseUtil;


    @PostMapping("email-verification/code")
    public ResponseEntity sendEmailVerificationCode() {
        try{
            Optional<UserEntity> userEntity = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(userEntity.isPresent()){
                if(userEntity.get().isVerified()){

                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "This user is already verified"));
                }
                return userVerificationService.sendVerificationCodeEmail(userEntity.get(),
                        "Your email verification code",
                        "Use this code to verify your email in Ballotguard",
                        "Email verification code sent");
            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Authentication failed. User does not exist"));
            }

        }catch(Exception e){

            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));
        }
    }

    @PostMapping("email-verification/verify")
    public ResponseEntity verifyEmailVerificationCode(@RequestBody Map<String, Object> requestBody) {

        try{
            String verificationCode = (String) requestBody.get("verificationCode");
            Optional<UserEntity> userEntity = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(userEntity.isPresent()){
                return userVerificationService.verifyVerificationCode(userEntity.get(), verificationCode, false, "Email verification code verified. User is now verified");
            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "Authentication failed. User does not exist"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));

        }
    }
}
