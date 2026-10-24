package io.github.ballotguard.controllers.authentication;

import io.github.ballotguard.services.authentication.GoogleAuthService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    /**
     * POST /api/v1/auth/google
     * Accepts a Google ID token from the frontend, verifies it, and returns JWT + user info.
     */
    @PostMapping("google")
    public ResponseEntity<Map> googleAuth(@RequestBody Map<String, Object> requestBody) {
        try {
            String idToken = (String) requestBody.get("idToken");
            return googleAuthService.authenticateGoogleUser(idToken);
        } catch (Exception e) {
            log.error("Google auth error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(createResponseUtil.createResponseBody(false, "An error occurred during Google authentication"));
        }
    }
}
