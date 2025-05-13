package io.github.ballotguard.controllers;

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

    @GetMapping("/public/refresh-jwt")
    public ResponseEntity<Map> refreshJwt(@RequestBody Map<String, Object> requestBody) {
        String refreshToken = (String) requestBody.get("refreshToken");
        try{
            if(jwtUtil.validateToken(refreshToken, true)){
                String email = jwtUtil.extractEmail(refreshToken, true);
                String newJwt = jwtUtil.generateToken(email, false);
                Map<String, Object> response = new HashMap<>();
                response.put("jwt", newJwt);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Refresh Token is not valid");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
