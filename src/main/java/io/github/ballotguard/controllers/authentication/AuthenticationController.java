package io.github.ballotguard.controllers.authentication;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.user.UserService;
import io.github.ballotguard.services.user.UserVerificationService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.JwtUtil;
import io.github.ballotguard.utilities.MatchTextPatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager ;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private MatchTextPatternUtil matchTextPatternUtil;

    @Autowired
    private UserVerificationService userVerificationService;


    @Transactional
    @PostMapping("signup")
    public ResponseEntity<Map> signup (@RequestBody UserEntity userEntity)  {
        try{

            if(userEntity.getEmail().isEmpty() || userEntity.getPassword().isEmpty() || userEntity.getFirstName().isEmpty()){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Email, password or first name is empty"));

            }else if(!matchTextPatternUtil.isValidEmail(userEntity.getEmail())){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "This email address is not valid"));

            }else if(userEntity.getPassword().length() < 8 || userEntity.getPassword().length() > 50){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Password must be between 8 and 50 characters"));

            }else{

                ResponseEntity<UserEntity> userResponse = userService.findUser(userEntity.getEmail(), "email");

                if(userResponse.getStatusCode().equals(HttpStatus.OK)){

                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(createResponseUtil.createResponseBody(false, "Another user with this email already exists"));

                }

                UserEntity createdUser = userService.createUser(userEntity, false);

                if(createdUser != null) {

                    ResponseEntity emailResponse = userVerificationService.sendVerificationCodeEmail(createdUser,
                            "Your email verification code",
                            "Use this code to verify your email in Ballotguard",
                            "Email verification code sent");


                    String message= "User successfully created";
                    if(emailResponse.getStatusCode().equals(HttpStatus.OK)){
                        message += " and verification code sent";
                    }else{
                        message += " but verification code was not sent";
                    }
                    return jwtUtil.generateTokenAndUserinfoResponse(createdUser, message);

                }else {

                    return ResponseEntity.internalServerError()
                            .body(createResponseUtil.createResponseBody(false, "User creation failed, please try again"));

                }
            }

        }catch (Exception e) {

            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while creating new user"));

        }
    }


    @PostMapping("login")
    public ResponseEntity<Map> login (@RequestBody Map<String, Object> requestBody) {

        try{
            String email = (String) requestBody.get("email");
            String password = (String) requestBody.get("password");

            if(email.isEmpty() || password.isEmpty()){
                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Email or password is empty"));

            }else if(!matchTextPatternUtil.isValidEmail(email)){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "This email address is not valid"));

            }else{
                try{
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, password)
                    );

                    return jwtUtil.generateTokenAndUserinfoResponse(userService.findUser(email, "email").getBody(), "Login successful");

                }catch (Exception e) {
                    log.error(e.getMessage());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(createResponseUtil.createResponseBody(false, "Email or password is incorrect"));

                }
            }

        } catch (Exception e) {

            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while logging user in"));

        }
    }



}

