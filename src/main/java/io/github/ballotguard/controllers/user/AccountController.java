package io.github.ballotguard.controllers.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.services.user.UserService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.JwtUtil;
import io.github.ballotguard.utilities.MatchTextPatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AccountController {

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

                    return jwtUtil.generateTokenAndUserinfoResponse(createdUser, "User created");

                }else {

                    return ResponseEntity.internalServerError()
                            .body(createResponseUtil.createResponseBody(false, "User creation failed"));

                }
            }

        }catch (Exception e) {

            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred while creating new user"));

        }
    }


    @GetMapping("login")
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
                            .body(createResponseUtil.createResponseBody(false, "Wrong email or password"));

                }
            }

        } catch (Exception e) {

            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));

        }
    }



}

