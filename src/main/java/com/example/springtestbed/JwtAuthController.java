package com.example.springtestbed;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class JwtAuthController {

    private final JwtUtil jwtUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final AuthenticationManager authenticationManager;

    record AuthRequest(String username, String password) {}
    record AuthResponse(String token) {}

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        var username = authRequest.username();
        var password = authRequest.password();
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        var authentication = new UsernamePasswordAuthenticationToken(username, password);
        // TODO - try catch with custom exceptions?
        authenticationManager.authenticate(authentication);

        var userDetails = jwtUserDetailsService.loadUserByUsername(username);
        var token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

}
