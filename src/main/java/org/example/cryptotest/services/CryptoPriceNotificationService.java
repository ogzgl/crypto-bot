package org.example.cryptotest.services;

import org.example.cryptotest.entities.CryptoPrice;
import org.example.cryptotest.entities.User;
import org.example.cryptotest.repositories.CryptoPriceRepository;
import org.example.cryptotest.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class CryptoPriceNotificationService {

    private final CryptoPriceRepository cryptoPriceRepository;

    private final UserRepository userRepository;

    private final TelegramClient telegramClient;

    public CryptoPriceNotificationService(CryptoPriceRepository cryptoPriceRepository, UserRepository userRepository, TelegramClient telegramClient) {
        this.cryptoPriceRepository = cryptoPriceRepository;
        this.userRepository = userRepository;
        this.telegramClient = telegramClient;
    }


    public void handleNotifications(List<CryptoPrice> latestPrices) throws Exception {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Map<String, Double> symbolPercentageChanges = new HashMap<>();
            for (CryptoPrice price : latestPrices) {
                Optional<CryptoPrice> storedPriceOptional = cryptoPriceRepository
                        .findFirstBySymbolAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(price.getSymbol(), user.getStartTime());

                storedPriceOptional.ifPresent(storedPrice -> {
                    double percentageChange = ((price.getPrice() - storedPrice.getPrice()) / storedPrice.getPrice()) * 100;
                    if (Math.abs(percentageChange) > user.getThreshold()) {
                        symbolPercentageChanges.put(price.getSymbol(), percentageChange);
                    }
                });
            }
            sendSummaryTelegramNotification(user.getChatId(), symbolPercentageChanges);
        }
    }

    private void sendSummaryTelegramNotification(String chatId, Map<String, Double> symbolPercentageChanges)
            throws Exception {
        StringBuilder messageBuilder = new StringBuilder("Price change summary:")
                .append("\\n");


        for (Map.Entry<String, Double> entry : symbolPercentageChanges.entrySet()) {
            String symbol = entry.getKey();
            double percentageChange = entry.getValue();
            String formattedPercentageChange = String.format("%.2f", Math.abs(percentageChange));
            messageBuilder
                    .append(symbol).append(" - ")
                    .append(formattedPercentageChange).append("% ").append("\\n");
        }
        telegramClient.sendMessage(messageBuilder.toString(), chatId);
    }
}
