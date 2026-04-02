package com.Gdz.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiService aiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiService, "aiUrl", "http://localhost:8080/ai");
    }

    @Test
    void getAiStats_ShouldReturnFormattedString_WhenSuccessfulResponse() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/ai/api/ai/summaries/" + telegramId;

        String jsonResponse = """
                {
                    "status": "success",
                    "summary": "Игрок активный геймер с 500+ часами в CS2"
                }
                """;

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn(jsonResponse);

        String result = aiService.getAiStats(telegramId);

        assertNotNull(result);
        assertTrue(result.contains("success"));
        assertTrue(result.contains("Игрок активный геймер с 500+ часами в CS2"));

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void getAiStats_ShouldReturnFormattedString_WhenErrorResponse() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/ai/api/ai/summaries/" + telegramId;

        String jsonResponse = """
                {
                    "status": "error",
                    "summary": "Не удалось создать описание"
                }
                """;

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn(jsonResponse);

        String result = aiService.getAiStats(telegramId);

        assertNotNull(result);
        assertTrue(result.contains("error"));
        assertTrue(result.contains("Не удалось создать описание"));

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void getAiStats_ShouldReturnNull_WhenRestTemplateReturnsNull() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/ai/api/ai/summaries/" + telegramId;

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn(null);

        String result = aiService.getAiStats(telegramId);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void getAiStats_ShouldHandleMissingFields_WhenPartialResponse() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/ai/api/ai/summaries/" + telegramId;

        String jsonResponse = """
                {
                    "status": "ожидание"
                }
                """;

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn(jsonResponse);

        String result = aiService.getAiStats(telegramId);

        assertNotNull(result);
        assertTrue(result.contains("ожидание"));
        assertTrue(result.contains("-"));

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void getAiStats_ShouldCallCorrectUrl() {
        Long telegramId = 987654321L;
        String expectedUrl = "http://localhost:8080/ai/api/ai/summaries/" + telegramId;

        String jsonResponse = """
                {
                    "status": "success",
                    "summary": "Тестовое описание"
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(jsonResponse);

        aiService.getAiStats(telegramId);

        verify(restTemplate).getForObject(eq(expectedUrl), eq(String.class));
    }
}
