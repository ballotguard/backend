package io.github.ballotguard.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("health-check")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
