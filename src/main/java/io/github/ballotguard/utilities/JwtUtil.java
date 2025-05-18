package io.github.ballotguard.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    CreateResponseUtil createResponseUtil;

    @Value("${app.jwtKey}")
    private String jwtSecretKey;

    @Value("${app.refreshTokenKey}")
    private String refreshTokenSecretKey;



    private SecretKey getSigningKey(Boolean isRefresh) {
        return isRefresh ? Keys.hmacShaKeyFor(refreshTokenSecretKey.getBytes()) : Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public String extractEmail(String token, Boolean isRefresh) {
        Claims claims = extractAllClaims(token, isRefresh);
        return claims.getSubject();
    }

    public Date extractExpiration(String token, Boolean isRefresh) {
        return extractAllClaims(token, isRefresh).getExpiration();
    }

    private Claims extractAllClaims(String token, Boolean isRefresh) {
        return Jwts.parser()
                .verifyWith(getSigningKey(isRefresh))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token, Boolean isRefresh) {
        return extractExpiration(token, isRefresh).before(new Date());
    }

    public String generateToken(String email, Boolean isRefresh) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, isRefresh);
    }

    private String createToken(Map<String, Object> claims, String subject, Boolean isRefresh) throws Exception {
       try{
           long tokenLifetimeInMillis ;
           if(isRefresh){
               tokenLifetimeInMillis = 1000L * 60 * 60 * 24 * 90;
           }else{
               tokenLifetimeInMillis = 1000L * 60 * 15;
           }

           return Jwts.builder()
                   .claims(claims)
                   .subject(subject)
                   .header().empty().add("typ", "JWT")
                   .and()
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + tokenLifetimeInMillis))
                   .signWith(getSigningKey(isRefresh))
                   .compact();
       }catch(Exception e){
           log.error(e.getMessage());
           throw new Exception(e.getMessage());
       }
    }


    public Boolean validateToken(String token, Boolean isRefresh) {
        try {
            extractAllClaims(token, isRefresh); //this will throw error is jwt in signed with the correct signing key
            return !isTokenExpired(token, isRefresh);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public ResponseEntity<Map> generateJwtAndRefreshTokenResponse(String email, String message) throws Exception {
        try{
            String jwt = generateToken(email, false);
            String refreshToken = generateToken(email, true);

            if(jwt != null && refreshToken != null) {

                Map<String, Object> tokens = new HashMap<>();
                tokens.put("jwt", jwt);
                tokens.put("refreshToken", refreshToken);

                return ResponseEntity.status(HttpStatus.OK).body(createResponseUtil.createResponseBody(true, message, tokens ));
            }else{
                return ResponseEntity.internalServerError()
                        .body(createResponseUtil.createResponseBody(false, "An error occurred"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }


}
