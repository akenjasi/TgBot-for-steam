package com.Gdz.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authUrl;

    public String requestBindLink(Long telegramId, String steamLink) {

        String url = authUrl + "/bind";

        Map<String, Object> body = Map.of(
                "telegramId", telegramId,
                "steamLink", steamLink
        );


        ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);
        JSONObject json = new JSONObject(response.getBody());

        return String.format("""
                Привязка аккаунта к вашему Тг
                Статус: %s
                Сообщение: %s
                Steam ID: %s
                """,
                json.optString("status", "-"),
                json.optString("message", "-"),
                json.optString("steamId", "-")
        );
    }
}