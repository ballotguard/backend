package io.github.ballotguard.controllers.authentication;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.user.ForgotPasswordService;
import io.github.ballotguard.services.user.UserService;
import io.github.ballotguard.services.user.UserVerificationService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    UserVerificationService userVerificationService;

    @Transactional
    @PostMapping("auth/password-reset/code")
    public ResponseEntity sendForgotPasswordCodeUsingEmail(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        try{
            ResponseEntity<UserEntity> response = userService.findUser(email, "email");
            if(response.getStatusCode() == HttpStatus.OK){
                return userVerificationService.sendVerificationCodeEmail(response.getBody(),
                        "Your forgot password verification code",
                        "Use this code to continue with resetting your password",
                        "Forgot password verification code sent");
            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User does not exist"));
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while sending password reset verification code"));
        }

    }


    @Transactional
    @PostMapping("auth/password-reset/verify-and-reset")
    public ResponseEntity<Map> verifyForgotPasswordVerificationCode(@RequestBody Map<String, Object> requestBody) {

        try{
            String email = (String) requestBody.get("email");
            String verificationCode = (String) requestBody.get("verificationCode");
            String newPassword = (String) requestBody.get("newPassword");

            ResponseEntity<UserEntity> userResponse = userService.findUser(email, "email");

            if(userResponse.getStatusCode() == HttpStatus.OK ){
                ResponseEntity verificationResponse = userVerificationService.verifyVerificationCode(userResponse.getBody(), verificationCode, true, "");

                if(verificationResponse.getStatusCode() == HttpStatus.OK ){
                    ResponseEntity resetPasswordResponse = forgotPasswordService.resetPasswordWithoutPreviousPassword(newPassword, email);

                    if(resetPasswordResponse.getStatusCode() == HttpStatus.OK ){
                        return  jwtUtil.generateTokenAndUserInfoResponse(userResponse.getBody(), "Forgot password code was verified and password reset is successful");

                    }else{
                        return resetPasswordResponse;
                    }
                }else{
                    return verificationResponse;
                }

            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .body(createResponseUtil.createResponseBody(false, "User does not exist"));
            }

        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while verifying password reset verification code"));
        }
    }







}
