package com.example.springtestbed.security;

import com.example.springtestbed.security.jwt.JwtRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final ObjectMapper mapper;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // csrf not needed
            .csrf()
                .disable()
                // TODO - review these next two options
//                .headers()
//                .frameOptions().disable()
            // specify paths that require auth and those that don't
            .authorizeRequests()
                .antMatchers("/").anonymous()
                .antMatchers("/authenticate").anonymous()
                .antMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            // use a stateless session, so session won't be used to store user state
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // everyone can logout
            .and()
            .logout()
                .permitAll()
            // configure exception handling
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    var timestamp = LocalDateTime.now(ZoneId.of("UTC"));
                    var status = HttpStatus.UNAUTHORIZED.value();
                    var error = HttpStatus.UNAUTHORIZED.getReasonPhrase();
                    var message = String.format("%d %s", status, error);
                    var path = request.getServletPath();
                    var err = new Error(timestamp, status, error, message, path);
                    var body = mapper.writeValueAsString(err);
                    response.getWriter().write(body);
                }))
            // add a filter to validate tokens with every request
            .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        ;
    }

    private record Error(LocalDateTime timestamp, int status, String error, String message, String path) {}

}
