package com.example.springtestbed.security.jwt;

import com.example.springtestbed.users.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtRequestFilter(JwtUtil jwtUtil,
                            UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null) {
            String token = null;
            String username = null;

            // extract actual token from auth header, ignoring 'Bearer' text
            var bearer = "Bearer ";
            if (requestTokenHeader.startsWith(bearer)) {
                token = requestTokenHeader.substring(bearer.length());
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT token", e);
                } catch (ExpiredJwtException e) {
                    log.warn("JWT token has expired", e);
                }
            } else {
                // TODO - this should technically throw some sort of exception so the client gets an unauth'd response, but that doesn't happen right now
                log.warn("JWT token does not begin with '" + bearer + "'");
            }

            var securityContext = SecurityContextHolder.getContext();
            if (username != null && securityContext.getAuthentication() == null) {
                var userDetails = userService.loadUserByUsername(username);

                // manually set auth if token is valid
                if (jwtUtil.validateToken(token, userDetails)) {
                    var usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    var details = new WebAuthenticationDetailsSource().buildDetails(request);
                    usernamePasswordAuthToken.setDetails(details);

                    // after setting the auth in the context, specify that the current user is auth'd
                    // in order to pass spring security configuration
                    securityContext.setAuthentication(usernamePasswordAuthToken);
                }
            }
        }

        // continue through filter chain
        filterChain.doFilter(request, response);
    }

}
