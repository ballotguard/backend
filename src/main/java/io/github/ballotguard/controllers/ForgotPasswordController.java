package io.github.ballotguard.controllers;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.services.ForgotPasswordService;
import io.github.ballotguard.services.UserService;
import io.github.ballotguard.services.UserVerificationService;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController

public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserVerificationService userVerificationService;


    @PostMapping("/auth/send-forgot-password-verification-code")
    public ResponseEntity sendForgotPasswordCodeUsingEmail (@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        try{
            ResponseEntity<UserEntity> response = userService.findUser(email, "email");
            if(response.getStatusCode() == HttpStatus.OK){
                return userVerificationService.sendVerificationCodeEmail(response.getBody(),
                        "Your forgot password verification code",
                        "Use this code to continue with resetting your password");
            }else{
                return  ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }


    @GetMapping("/auth/verify-forgot-password-verification-code")
    public ResponseEntity<Map> verifyForgotPasswordVerificationCode(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        String verificationCode = (String) requestBody.get("verificationCode");
        try{
            ResponseEntity<UserEntity> response = userService.findUser(email, "email");
            if(response.getStatusCode() == HttpStatus.OK && userVerificationService.verifyVerificationCode(response.getBody(), verificationCode, true).getStatusCode().equals(HttpStatus.OK)){

                return  jwtUtil.generateJwtAndRefreshToken(response.getBody().getEmail());

            }else{
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/user/reset-password")
    public ResponseEntity resetPassword(@RequestBody Map<String, Object> requestBody) {
        String oldPassword = (String) requestBody.get("oldPassword");
        String newPassword = (String) requestBody.get("newPassword");
        try{
            return forgotPasswordService.resetPassword(oldPassword, newPassword);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }






}
