package org.example.cryptotest.controller;

import org.example.cryptotest.dto.TelegramUpdate;
import org.example.cryptotest.services.TelegramWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TelegramWebhookController {

    private final TelegramWebhookService telegramWebhookService;

    public TelegramWebhookController(TelegramWebhookService telegramWebhookService) {
        this.telegramWebhookService = telegramWebhookService;
    }

    @PostMapping("/telegram")
    public ResponseEntity telegramIncomingRequest(@RequestBody TelegramUpdate telegramUpdate) throws Exception {
        telegramWebhookService.handleRegistration(telegramUpdate);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
