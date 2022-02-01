package com.example.springtestbed.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        var password = user.getPassword();
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();
        return new org.springframework.security.core.userdetails.User(username, password, authorities);
    }

    public Optional<User> findUser(String username) {
        return userRepository.findOneByUsername(username);
    }

}
