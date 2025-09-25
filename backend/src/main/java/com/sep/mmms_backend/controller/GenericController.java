package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GenericController {

    @GetMapping("/isAuthenticated")
    public ResponseEntity<Response> isAuthenticated(Authentication authentication) {

        if (authentication != null && !authentication.getName().equals("anonymous") && !authentication.getName().equals("anonymousUser")) {
            if (authentication.isAuthenticated()) {
                log.info("The user: {} is authenticated", authentication.getName());
                return ResponseEntity.ok(new Response("true"));
            }
        }
        return new ResponseEntity<Response>(new Response("false"), HttpStatus.UNAUTHORIZED);
    }
}
