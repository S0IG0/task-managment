package org.soigo.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.soigo.task.auth.controller.AuthController;
import org.soigo.task.auth.dto.request.CredentialRequest;
import org.soigo.task.auth.dto.request.RefreshTokenRequest;
import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.auth.service.AuthService;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class TestAuthController {

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    PairToken pairToken;
    RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        pairToken = PairToken.builder()
                .access("access_token")
                .refresh("refresh_token")
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refresh("refresh_token")
                .build();
    }

    @Test
    void refreshPairToken_validToken_returnsOK() {
        when(authService.updatePairTokenByRefreshToken(refreshTokenRequest.getRefresh()))
                .thenReturn(pairToken);

        var result = authController.refreshPairToken(refreshTokenRequest);

        verify(authService, times(1)).updatePairTokenByRefreshToken(refreshTokenRequest.getRefresh());
        assertEquals(OK, result.getStatusCode());
        assertEquals(pairToken, result.getBody());
    }

    @Test
    void refreshPairToken_invalidToken_returnsUnauthorized() {
        when(authService.updatePairTokenByRefreshToken("invalid_token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        var invalidRequest = RefreshTokenRequest.builder()
                .refresh("invalid_token")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authController.refreshPairToken(invalidRequest));

        assertEquals("Invalid token", exception.getMessage());
        verify(authService, times(1)).updatePairTokenByRefreshToken("invalid_token");
    }

    @Test
    void logoutCurrentDevice_validAuthorizationHeader_returnsOK() {
        String validAuthHeader = "Bearer access_token";

        var result = authController.logoutCurrentDevice(validAuthHeader);

        verify(authService, times(1)).logoutCurrentDevice("Bearer access_token");
        assertEquals(OK, result.getStatusCode());
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() {
        CredentialRequest invalidCredentialRequest = CredentialRequest.builder()
                .email("invalid@example.com")
                .password("wrongpassword")
                .build();

        when(authService.login(invalidCredentialRequest.getEmail(), invalidCredentialRequest.getPassword()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authController.login(invalidCredentialRequest));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void refreshPairToken_expiredToken_returnsUnauthorized() {
        RefreshTokenRequest expiredRefreshTokenRequest = RefreshTokenRequest.builder()
                .refresh("expired_token")
                .build();

        when(authService.updatePairTokenByRefreshToken("expired_token"))
                .thenThrow(new IllegalArgumentException("Expired token"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authController.refreshPairToken(expiredRefreshTokenRequest));

        assertEquals("Expired token", exception.getMessage());
    }

    @Test
    void refreshPairToken_validToken_returnsDifferentToken() {
        when(authService.updatePairTokenByRefreshToken(refreshTokenRequest.getRefresh()))
                .thenReturn(new PairToken("new_access_token", "new_refresh_token"));

        var result = authController.refreshPairToken(refreshTokenRequest);

        verify(authService, times(1)).updatePairTokenByRefreshToken(refreshTokenRequest.getRefresh());
        assertEquals(OK, result.getStatusCode());
        assertNotEquals(pairToken, result.getBody());
        assertEquals("new_access_token", Objects.requireNonNull(result.getBody()).getAccess());
    }




}
