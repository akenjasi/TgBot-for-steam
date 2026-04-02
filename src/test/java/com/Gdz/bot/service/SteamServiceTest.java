package com.Gdz.bot.service;

import com.Gdz.bot.dto.SteamStatsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SteamServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SteamService steamService;

    private SteamStatsDto expectedStats;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(steamService, "steamUrl", "http://localhost:8080/steam");

        expectedStats = new SteamStatsDto();
        expectedStats.setNickname("TestPlayer");
        expectedStats.setGamesCount(100);
        expectedStats.setHoursTotal(500);
        expectedStats.setTopGame("Counter-Strike 2");
        expectedStats.setSteamLevel(50);
        expectedStats.setFriendCount(25);
        expectedStats.setPersonState("Online");
        expectedStats.setCommunityVisibility("Public");
        expectedStats.setAccountCreated(1609459200L);
    }

    @Test
    void getStats_ShouldReturnSteamStats_WhenValidTelegramId() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/steam/stats/" + telegramId;

        when(restTemplate.getForObject(eq(expectedUrl), eq(SteamStatsDto.class)))
                .thenReturn(expectedStats);

        SteamStatsDto result = steamService.getStats(telegramId);

        assertNotNull(result);
        assertEquals("TestPlayer", result.getNickname());
        assertEquals(100, result.getGamesCount());
        assertEquals(500, result.getHoursTotal());
        assertEquals("Counter-Strike 2", result.getTopGame());
        assertEquals(50, result.getSteamLevel());
        assertEquals(25, result.getFriendCount());
        assertEquals("Online", result.getPersonState());
        assertEquals("Public", result.getCommunityVisibility());
        assertEquals(1609459200L, result.getAccountCreated());

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(SteamStatsDto.class));
    }

    @Test
    void getStats_ShouldReturnNull_WhenRestTemplateReturnsNull() {
        Long telegramId = 123456789L;
        String expectedUrl = "http://localhost:8080/steam/stats/" + telegramId;

        when(restTemplate.getForObject(eq(expectedUrl), eq(SteamStatsDto.class)))
                .thenReturn(null);

        SteamStatsDto result = steamService.getStats(telegramId);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(SteamStatsDto.class));
    }

    @Test
    void getStats_ShouldCallCorrectUrl() {
        Long telegramId = 987654321L;
        String expectedUrl = "http://localhost:8080/steam/stats/" + telegramId;

        when(restTemplate.getForObject(anyString(), eq(SteamStatsDto.class)))
                .thenReturn(expectedStats);

        steamService.getStats(telegramId);

        verify(restTemplate).getForObject(eq(expectedUrl), eq(SteamStatsDto.class));
    }
}
