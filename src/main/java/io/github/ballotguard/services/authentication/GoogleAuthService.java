package io.github.ballotguard.services.authentication;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.services.user.UserService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class GoogleAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id:${GOOGLE_CLIENT_ID:}}")
    private String configuredGoogleClientId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Verifies a Google ID token via Google's tokeninfo endpoint, then finds or creates the user.
     *
     * @param idToken Google ID token / credential
     * @return ResponseEntity with JWT, Refresh Token, and User Info
     */
    public ResponseEntity<Map> authenticateGoogleUser(String idToken) {
        try {
            if (idToken == null || idToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Google ID token is required"));
            }

            // Verify Google ID token via Google's official tokeninfo endpoint
            String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken.trim();
            ResponseEntity<Map> tokenInfoResponse;
            try {
                tokenInfoResponse = restTemplate.getForEntity(tokenInfoUrl, Map.class);
            } catch (Exception e) {
                log.error("Google token verification failed: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Invalid or expired Google token"));
            }

            if (!tokenInfoResponse.getStatusCode().is2xxSuccessful() || tokenInfoResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Invalid Google token"));
            }

            Map<String, Object> payload = tokenInfoResponse.getBody();

            String email = (String) payload.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "No email address found in Google account"));
            }

            // Validate that the email is verified by Google
            Object emailVerifiedObj = payload.get("email_verified");
            boolean emailVerified = emailVerifiedObj != null && ("true".equalsIgnoreCase(emailVerifiedObj.toString()) || Boolean.TRUE.equals(emailVerifiedObj));
            if (!emailVerified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createResponseUtil.createResponseBody(false, "Google email is not verified"));
            }

            // Check audience if client ID is configured
            String aud = (String) payload.get("aud");
            if (configuredGoogleClientId != null && !configuredGoogleClientId.trim().isEmpty() && !configuredGoogleClientId.equals(aud)) {
                log.warn("Google token audience mismatch: expected {}, got {}", configuredGoogleClientId, aud);
            }

            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String fullName = (String) payload.get("name");

            if ((firstName == null || firstName.trim().isEmpty()) && fullName != null && !fullName.trim().isEmpty()) {
                String[] parts = fullName.trim().split("\\s+", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            }

            if (firstName == null || firstName.trim().isEmpty()) {
                firstName = email.split("@")[0];
            }
            if (lastName == null) {
                lastName = "";
            }

            return processUserLoginOrCreate(email.toLowerCase().trim(), firstName.trim(), lastName.trim());

        } catch (Exception e) {
            log.error("Error authenticating Google user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "Error processing Google authentication: " + e.getMessage()));
        }
    }

    /**
     * Finds an existing user or creates a new user, returning JWT and Refresh token.
     */
    public ResponseEntity<Map> processUserLoginOrCreate(String email, String firstName, String lastName) throws Exception {
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            if (!user.isAccountEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createResponseUtil.createResponseBody(false, "User account is disabled"));
            }
            return jwtUtil.generateTokenAndUserInfoResponse(user, "Google login successful");
        } else {
            // New user registration via Google
            UserEntity newUser = new UserEntity(
                    null,
                    email,
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    firstName,
                    lastName,
                    true, // isVerified
                    true, // isAccountEnabled
                    Instant.now().getEpochSecond(),
                    null,
                    null,
                    new ArrayList<>(),
                    new ArrayList<>(List.of("USER"))
            );

            UserEntity createdUser = userService.createUser(newUser, true);
            if (createdUser != null) {
                return jwtUtil.generateTokenAndUserInfoResponse(createdUser, "Account successfully created with Google");
            } else {
                return ResponseEntity.internalServerError()
                        .body(createResponseUtil.createResponseBody(false, "Failed to create user account with Google"));
            }
        }
    }
}
