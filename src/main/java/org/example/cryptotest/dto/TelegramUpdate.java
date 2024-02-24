package org.example.cryptotest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TelegramUpdate {
    @JsonProperty("update_id")
    private long updateId;
    private TelegramMessage message;
}

