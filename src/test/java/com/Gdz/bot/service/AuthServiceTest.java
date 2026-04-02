package com.Gdz.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "authUrl", "http://localhost:8080/auth");
    }

    @Test
    void requestBindLink_ShouldReturnFormattedString_WhenSuccessfulResponse() {
        Long telegramId = 123456789L;
        String steamLink = "https://steamcommunity.com/id/testuser";
        String expectedUrl = "http://localhost:8080/auth/bind";

        String jsonResponse = """
                {
                    "status": "success",
                    "message": "Аккаунт привязан",
                    "steamId": "76561197960287930"
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(expectedUrl), any(Map.class), eq(String.class)))
                .thenReturn(responseEntity);

        String result = authService.requestBindLink(telegramId, steamLink);

        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Аккаунт привязан"));
        assertTrue(result.contains("76561197960287930"));

        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), any(Map.class), eq(String.class));
    }

    @Test
    void requestBindLink_ShouldReturnFormattedString_WhenErrorResponse() {
        Long telegramId = 123456789L;
        String steamLink = "https://steamcommunity.com/id/testuser";
        String expectedUrl = "http://localhost:8080/auth/bind";

        String jsonResponse = """
                {
                    "status": "error",
                    "message": "Неверная ссылка Steam",
                    "steamId": "-"
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(eq(expectedUrl), any(Map.class), eq(String.class)))
                .thenReturn(responseEntity);

        String result = authService.requestBindLink(telegramId, steamLink);

        assertNotNull(result);
        assertTrue(result.contains("error"));
        assertTrue(result.contains("Неверная ссылка Steam"));

        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), any(Map.class), eq(String.class));
    }

    @Test
    void requestBindLink_ShouldHandleMissingFields_WhenPartialResponse() {
        Long telegramId = 123456789L;
        String steamLink = "https://steamcommunity.com/id/testuser";
        String expectedUrl = "http://localhost:8080/auth/bind";

        String jsonResponse = """
                {
                    "status": "ожидание"
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(expectedUrl), any(Map.class), eq(String.class)))
                .thenReturn(responseEntity);

        String result = authService.requestBindLink(telegramId, steamLink);

        assertNotNull(result);
        assertTrue(result.contains("ожидание"));
        assertTrue(result.contains("-"));

        verify(restTemplate, times(1)).postForEntity(eq(expectedUrl), any(Map.class), eq(String.class));
    }

    @Test
    void requestBindLink_ShouldSendCorrectBody() {
        Long telegramId = 123456789L;
        String steamLink = "https://steamcommunity.com/id/testuser";
        String expectedUrl = "http://localhost:8080/auth/bind";

        String jsonResponse = """
                {
                    "status": "success",
                    "message": "ОК",
                    "steamId": "123"
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(expectedUrl), any(Map.class), eq(String.class)))
                .thenReturn(responseEntity);

        authService.requestBindLink(telegramId, steamLink);

        verify(restTemplate).postForEntity(eq(expectedUrl), argThat(body -> {
            Map<?, ?> bodyMap = (Map<?, ?>) body;
            return bodyMap.get("telegramId").equals(telegramId) &&
                   bodyMap.get("steamLink").equals(steamLink);
        }), eq(String.class));
    }
}
