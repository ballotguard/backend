package io.github.ballotguard.controllers;

import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class RefreshJwtController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private CreateResponseUtil createResponseUtil;

    @GetMapping("/public/refresh-jwt")
    public ResponseEntity<Map> refreshJwt(@RequestBody Map<String, Object> requestBody) {

        try{
            String refreshToken = (String) requestBody.get("refreshToken");

            if(jwtUtil.validateToken(refreshToken, true)){
                String email = jwtUtil.extractEmail(refreshToken, true);
                String newJwt = jwtUtil.generateToken(email, false);

                Map<String, Object> jwtResponse = new HashMap<>();
                jwtResponse.put("jwt", newJwt);

                return ResponseEntity.ok(createResponseUtil.createResponseBody(true, "New JWT generated", jwtResponse));

            }else{

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Refresh token is invalid or expired"));
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred"));
        }
    }
}
