package io.github.ballotguard.controllers.authentication;

import io.github.ballotguard.utilities.CreateResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private CreateResponseUtil createResponseUtil;

    // Email/password login and signup have been removed.
    // Authentication is now handled exclusively via Google OAuth2.
    // See GoogleAuthController for the /api/v1/auth/google endpoint.

}

