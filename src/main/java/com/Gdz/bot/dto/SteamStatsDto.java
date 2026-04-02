package com.Gdz.bot.dto;

import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class SteamStatsDto {

    private String nickname;
    private int gamesCount;
    private int hoursTotal;
    private String topGame;
    private int steamLevel;
    private int friendCount;
    private String personState;
    private String communityVisibility;
    private long accountCreated;


    public String getAccountCreatedDate() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(accountCreated),
                ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("""
            👤 Никнейм: %s
            🎮 Игр в библиотеке: %d
            ⏱️ Всего часов: %d
            🏆 Топ игра: %s
            🌟 Уровень Steam: %d
            👥 Друзей: %d
            📊 Статус: %s
            🔒 Видимость: %s
            📅 Аккаунт создан: %s
            """,
                nickname,
                gamesCount,
                hoursTotal,
                topGame,
                steamLevel,
                friendCount,
                personState,
                communityVisibility,
                getAccountCreatedDate()
        );
    }
}