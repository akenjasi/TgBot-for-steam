package com.Gdz.bot.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${ai.stats.service.url}")
    private String aiUrl;

    public String getAiStats(Long telegramId) {
        String url = aiUrl + "/api/ai/summaries/" + telegramId;

        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            return null;
        }

        JSONObject json = new JSONObject(response);

        return String.format("""
                Описание от ИИ
                Статус: %s
                Сообщение: %s
                """,
                json.optString("status", "-"),
                json.optString("summary", "-")
        );
    }
}