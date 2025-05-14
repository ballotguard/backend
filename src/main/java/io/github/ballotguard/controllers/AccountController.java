package io.github.ballotguard.controllers;


import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.services.UserService;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager ;

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("signup")
    public ResponseEntity<Map> signup (@RequestBody UserEntity userEntity)  {
        try{
            if(!userEntity.getEmail().isEmpty() && !userEntity.getPassword().isEmpty()
                    && !userEntity.getFirstName().isEmpty()){

                ResponseEntity<UserEntity> response = userService.findUser(userEntity.getEmail(), "email");

                if(response.getStatusCode().equals(HttpStatus.OK)){
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }

                UserEntity createdUser = userService.createUser(userEntity, false);
                if(createdUser != null) {
                  return jwtUtil.generateJwtAndRefreshToken(createdUser.getEmail());

                }else {
                    return ResponseEntity.internalServerError().build();
                }
            }else{
                log.error(userEntity.getEmail()+userEntity.getPassword()+userEntity.getFirstName()+userEntity.getLastName());
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("login")
    public ResponseEntity<Map> login (@RequestBody Map<String, Object> requestBody) {

        try{
            String email = (String) requestBody.get("email");
            String password = (String) requestBody.get("password");

            if(!email.isEmpty() && !password.isEmpty() ) {
                return checkAuthAndGenerateJwt(email, password);

            }else{
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    private ResponseEntity<Map> checkAuthAndGenerateJwt(String email, String password) {

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return jwtUtil.generateJwtAndRefreshToken(userDetails.getUsername());
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

