package org.example.cryptotest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TelegramMessage {
    @JsonProperty("message_id")
    private int messageId;
    private TelegramUser from;
    private TelegramChat chat;
    private int date;
    private String text;
}
