package com.example.springtestbed;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private static final String TEST_USERNAME = "user";
    private static final String TEST_PASSWORD = "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6";
    private static final Collection<? extends GrantedAuthority> TEST_ROLES = new ArrayList<>();

    // TODO - wire up to user datastore

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (TEST_USERNAME.equals(username)) {
            return new User(TEST_USERNAME, TEST_PASSWORD, TEST_ROLES);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}
