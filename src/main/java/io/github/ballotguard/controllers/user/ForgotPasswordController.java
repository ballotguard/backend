package io.github.ballotguard.controllers.user;

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


    @PostMapping("auth/password-reset/code")
    public ResponseEntity sendForgotPasswordCodeUsingEmail (@RequestBody Map<String, Object> requestBody) {
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
                        .body(createResponseUtil.createResponseBody(false, "Authentication failed. User does not exist"));
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));
        }

    }


    @GetMapping("auth/password-reset/verify")
    public ResponseEntity<Map> verifyForgotPasswordVerificationCode(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        String verificationCode = (String) requestBody.get("verificationCode");
        try{
            ResponseEntity<UserEntity> response = userService.findUser(email, "email");

            if(response.getStatusCode() == HttpStatus.OK ){
                ResponseEntity verificationResponse = userVerificationService.verifyVerificationCode(response.getBody(), verificationCode, true, "");

                if(verificationResponse.getStatusCode() == HttpStatus.OK){
                    return  jwtUtil.generateTokenAndUserinfoResponse(response.getBody(), "Forgot password code verified");
                }else{
                    return verificationResponse;
                }


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

    @PutMapping("user/password-reset")
    public ResponseEntity resetPassword(@RequestBody Map<String, Object> requestBody) {
        try{
            String oldPassword = (String) requestBody.get("oldPassword");
            String newPassword = (String) requestBody.get("newPassword");

            return forgotPasswordService.resetPassword(oldPassword, newPassword);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));
        }
    }






}
