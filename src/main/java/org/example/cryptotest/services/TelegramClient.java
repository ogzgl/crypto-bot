package org.example.cryptotest.services;

import org.example.cryptotest.config.TelegramConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TelegramClient {
    private final RestTemplate restTemplate;

    private final TelegramConfig telegramConfig;
    private static final Logger logger = LoggerFactory.getLogger(TelegramClient.class);

    @Autowired
    public TelegramClient(RestTemplate restTemplate, TelegramConfig telegramConfig) {
        this.restTemplate = restTemplate;
        this.telegramConfig = telegramConfig;
    }

    public void sendMessage(String message, String chatID) throws Exception {
        try {
            String apiUrl = telegramConfig.getBaseUrl() + telegramConfig.getToken();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl + "/sendMessage")
                    .queryParam("chat_id", chatID)
                    .queryParam("text", message);
            restTemplate.exchange(builder.toUriString().replaceAll("%20", " "), HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error response : State code: {}, response: {} ", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception err) {
            logger.error("Error: {} ", err.getMessage());
            throw new Exception("This service is not available at the moment!");
        }
    }
}
