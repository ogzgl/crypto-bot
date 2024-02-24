package org.example.cryptotest.repositories;

import org.example.cryptotest.entities.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    Optional<CryptoPrice> findBySymbolAndCreatedAtGreaterThanEqual(String symbol, Instant createdAt);
    Optional<CryptoPrice> findFirstBySymbolAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(String symbol, Instant createdAt);
}

