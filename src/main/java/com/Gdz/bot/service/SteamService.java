package com.Gdz.bot.service;

import com.Gdz.bot.dto.SteamStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SteamService {

    private final RestTemplate restTemplate;

    @Value("${steam.stats.service.url}")
    private String steamUrl;

    public SteamStatsDto getStats(Long telegramId) {

        String url = steamUrl + "/stats/" + telegramId;

        return restTemplate.getForObject(url, SteamStatsDto.class);
    }
}