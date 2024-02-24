package org.example.cryptotest.services;

import org.example.cryptotest.config.CryptoAppConfig;
import org.example.cryptotest.dto.TelegramUpdate;
import org.example.cryptotest.entities.User;
import org.example.cryptotest.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TelegramWebhookService {

    private final UserRepository userRepository;
    private final CryptoAppConfig cryptoAppConfig;

    private final TelegramClient telegramClient;
    private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookService.class);


    public TelegramWebhookService(UserRepository userRepository, CryptoAppConfig cryptoAppConfig, TelegramClient telegramClient) {
        this.userRepository = userRepository;
        this.cryptoAppConfig = cryptoAppConfig;
        this.telegramClient = telegramClient;
    }

    public void handleRegistration(TelegramUpdate telegramUpdate) throws Exception {
        String username = telegramUpdate.getMessage().getFrom().getUsername();
        String message = telegramUpdate.getMessage().getText();
        String chatId = String.valueOf(telegramUpdate.getMessage().getChat().getId());
        try {
            validateUserCount();
            if (message.startsWith("priceCheck")) {
                handleRegistration(username, chatId, message);
            } else if (message.startsWith("priceCheckRestart")) {
                handleRestartProcess(username, chatId, message);
            }
            else {
                throw new Exception("Unknown command, use priceCheck for registration " +
                        "and priceCheckRestart for updating initial time");
            }
            logger.info("Successfully handled user in action");
        } catch (Exception e) {
            logger.error(e.getMessage());
            telegramClient.sendMessage(e.getMessage(), chatId);
        }
    }

    private void handleRestartProcess(String username, String chatId, String message) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User does not exist, use priceCheck message."));
        user.setStartTime(Instant.now());
        user.setThreshold(parseThreshold(message));
        userRepository.save(user);
        telegramClient.sendMessage("Successfully changed initial time for checking prices.", chatId);
    }

    private void handleRegistration(String username, String chatId, String message) throws Exception {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setStartTime(Instant.now());
        newUser.setThreshold(parseThreshold(message));
        newUser.setChatId(chatId);
        userRepository.save(newUser);
        telegramClient.sendMessage("Successfully registered for price changes.", chatId);
    }

    public void validateUserCount() throws Exception {
        if (userRepository.count() >= cryptoAppConfig.getMaxUserPool()) {
            throw new Exception("Max user count reached.");
        }
    }

    public int parseThreshold(String message) {
        try {
            String[] parts = message.split(" ");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            logger.error("Could not parse threshold, using default value of 5");
        }
        return 5;
    }
}
