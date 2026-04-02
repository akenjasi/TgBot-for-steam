package com.Gdz.bot.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SteamStatsDtoTest {

    private SteamStatsDto steamStatsDto;

    @BeforeEach
    void setUp() {
        steamStatsDto = new SteamStatsDto();
        steamStatsDto.setNickname("TestPlayer");
        steamStatsDto.setGamesCount(100);
        steamStatsDto.setHoursTotal(500);
        steamStatsDto.setTopGame("Counter-Strike 2");
        steamStatsDto.setSteamLevel(50);
        steamStatsDto.setFriendCount(25);
        steamStatsDto.setPersonState("Online");
        steamStatsDto.setCommunityVisibility("Public");
        steamStatsDto.setAccountCreated(1609459200L); // 2021-01-01 00:00:00 UTC
    }

    @Test
    void getAccountCreatedDate_ShouldReturnFormattedDate() {
        String formattedDate = steamStatsDto.getAccountCreatedDate();

        assertNotNull(formattedDate);
        assertTrue(formattedDate.matches("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        String result = steamStatsDto.toString();

        assertNotNull(result);
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("500"));
        assertTrue(result.contains("Counter-Strike 2"));
        assertTrue(result.contains("50"));
        assertTrue(result.contains("25"));
        assertTrue(result.contains("Online"));
        assertTrue(result.contains("Public"));
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        assertEquals("TestPlayer", steamStatsDto.getNickname());
        assertEquals(100, steamStatsDto.getGamesCount());
        assertEquals(500, steamStatsDto.getHoursTotal());
        assertEquals("Counter-Strike 2", steamStatsDto.getTopGame());
        assertEquals(50, steamStatsDto.getSteamLevel());
        assertEquals(25, steamStatsDto.getFriendCount());
        assertEquals("Online", steamStatsDto.getPersonState());
        assertEquals("Public", steamStatsDto.getCommunityVisibility());
        assertEquals(1609459200L, steamStatsDto.getAccountCreated());
    }

    @Test
    void setters_ShouldSetCorrectValues() {
        SteamStatsDto newDto = new SteamStatsDto();

        newDto.setNickname("NewPlayer");
        newDto.setGamesCount(200);
        newDto.setHoursTotal(1000);
        newDto.setTopGame("Dota 2");
        newDto.setSteamLevel(75);
        newDto.setFriendCount(50);
        newDto.setPersonState("Away");
        newDto.setCommunityVisibility("Private");
        newDto.setAccountCreated(1640995200L); // 2022-01-01 00:00:00 UTC

        assertEquals("NewPlayer", newDto.getNickname());
        assertEquals(200, newDto.getGamesCount());
        assertEquals(1000, newDto.getHoursTotal());
        assertEquals("Dota 2", newDto.getTopGame());
        assertEquals(75, newDto.getSteamLevel());
        assertEquals(50, newDto.getFriendCount());
        assertEquals("Away", newDto.getPersonState());
        assertEquals("Private", newDto.getCommunityVisibility());
        assertEquals(1640995200L, newDto.getAccountCreated());
    }

    @Test
    void toString_ShouldContainAllFields() {
        String result = steamStatsDto.toString();

        assertTrue(result.contains("Никнейм:"));
        assertTrue(result.contains("Игр в библиотеке:"));
        assertTrue(result.contains("Всего часов:"));
        assertTrue(result.contains("Топ игра:"));
        assertTrue(result.contains("Уровень Steam:"));
        assertTrue(result.contains("Друзей:"));
        assertTrue(result.contains("Статус:"));
        assertTrue(result.contains("Видимость:"));
        assertTrue(result.contains("Аккаунт создан:"));
    }

    @Test
    void getAccountCreatedDate_ShouldHandleDifferentTimestamps() {
        // Test with different timestamp
        steamStatsDto.setAccountCreated(1640995200L); // 2022-01-01 00:00:00 UTC
        String formattedDate = steamStatsDto.getAccountCreatedDate();

        assertNotNull(formattedDate);
        assertTrue(formattedDate.contains("2022"));
    }

    @Test
    void defaultValues_ShouldBeZeroOrNull() {
        SteamStatsDto emptyDto = new SteamStatsDto();

        assertNull(emptyDto.getNickname());
        assertEquals(0, emptyDto.getGamesCount());
        assertEquals(0, emptyDto.getHoursTotal());
        assertNull(emptyDto.getTopGame());
        assertEquals(0, emptyDto.getSteamLevel());
        assertEquals(0, emptyDto.getFriendCount());
        assertNull(emptyDto.getPersonState());
        assertNull(emptyDto.getCommunityVisibility());
        assertEquals(0, emptyDto.getAccountCreated());
    }
}
