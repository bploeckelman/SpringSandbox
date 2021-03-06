package com.example.springtestbed.security;

import com.example.springtestbed.security.jwt.JwtUtil;
import com.example.springtestbed.users.UserService;
import lombok.RequiredArgsConstructor;
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
public class LoginController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private record LoginRequest(String username, String password) {}
    private record LoginResponse(String token) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var username = loginRequest.username();
        var password = loginRequest.password();
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        var authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        var userDetails = userService.loadUserByUsername(username);
        var token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
