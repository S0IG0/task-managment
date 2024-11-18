package org.soigo.task.auth.jwt.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.soigo.task.auth.jwt.service.JwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.header.start}")
    String jwtHeaderStart;

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("Converting HTTP request to Authentication object");
        String token = tokenExtractor(request);

        if (!jwtService.validateAccessToken(token)) {
            return null;
        }

        String username = jwtService.getUsernameFromToken(token);
        if (username == null) {
            return null;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        log.debug("Authentication token created for user: {}", username);
        return authenticationToken;
    }

    private @Nullable String tokenExtractor(@NotNull HttpServletRequest request) {
        log.info("Extracting JWT from Authorization header");
        String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (headerValue == null || !headerValue.startsWith(jwtHeaderStart + " ")) {
            log.warn("Authorization header is missing or does not start with the expected prefix: {}", jwtHeaderStart);
            return null;
        }

        String token = headerValue.replace(jwtHeaderStart + " ", "");
        log.debug("JWT extracted from header: {}", token);
        return token;
    }
}
