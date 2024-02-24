package org.example.cryptotest.jobs;

import org.example.cryptotest.entities.CryptoPrice;
import org.example.cryptotest.repositories.CryptoPriceRepository;
import org.example.cryptotest.services.CryptoPriceNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class CryptoPriceFetcher {

    private final CryptoPriceRepository cryptoPriceRepository;
    private final CryptoPriceNotificationService cryptoPriceNotificationService;

    private static final Logger logger = LoggerFactory.getLogger(CryptoPriceFetcher.class);

    public CryptoPriceFetcher(
            CryptoPriceRepository cryptoPriceRepository,
            CryptoPriceNotificationService cryptoPriceNotificationService
    ) {
        this.cryptoPriceRepository = cryptoPriceRepository;
        this.cryptoPriceNotificationService = cryptoPriceNotificationService;
    }

    @Scheduled(fixedRateString = "${crypto.fixedRate}", initialDelay = 1000)
    public void fetchPrices() throws Exception {
        logger.debug("Starting to fetch the prices");
        RestTemplate restTemplate = new RestTemplate();
        // url can be read from the application yaml
        String API_URL = "https://api.mexc.com/api/v3/ticker/price";
        CryptoPrice[] cryptoPrices = restTemplate.getForObject(API_URL, CryptoPrice[].class);
        if (cryptoPrices != null) {
            logger.info("Successfully fetched " + cryptoPrices.length + " records processing notification and saving.");
            cryptoPriceNotificationService.handleNotifications(Arrays.asList(cryptoPrices));
            cryptoPriceRepository.saveAll(Arrays.asList(cryptoPrices));
        }
        logger.info("Fetching process completed successfully.");
    }

}
