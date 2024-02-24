package org.example.cryptotest.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private Instant startTime;

    @Column
    private String chatId;

    @Column
    private Integer threshold;
}
