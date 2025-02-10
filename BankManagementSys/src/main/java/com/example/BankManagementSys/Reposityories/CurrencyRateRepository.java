package com.example.BankManagementSys.Reposityories;

import com.example.BankManagementSys.Entities.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByCurrencyCode(String currencyCode);
}
