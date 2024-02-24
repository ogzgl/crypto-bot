package org.example.cryptotest.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Table(name = "crypto_price")
public class CryptoPrice {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String symbol;

    @Column
    private Double price;

    @Column
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
