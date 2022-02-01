package com.example.springtestbed;

import com.example.springtestbed.users.UserService;
import com.example.springtestbed.users.Role;
import com.example.springtestbed.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userDetailsService;

    @GetMapping("/greeting")
    public String greeting() {
        var context = SecurityContextHolder.getContext();
        return "Hello there, " + context.getAuthentication().getName() + "!";
    }

    private record ProfileResponse(int id, String username, Instant created, Instant lastModified, String roles) {
        ProfileResponse(User user) {
            this(
                    user.getId(),
                    user.getUsername(),
                    user.getCreated(),
                    user.getLastModified(),
                    user.getRoles().stream().map(Role::getName)
                            .collect(Collectors.joining(",", "[", "]"))
            );
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userDetailsService.findUser(username)
                // NOTE - this shouldn't happen because the user has already authenticated at this point
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(new ProfileResponse(user));
    }

}
