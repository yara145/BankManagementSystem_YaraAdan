package com.example.BankManagementSys.Schedulers;

import com.example.BankManagementSys.Entities.CurrencyRate;
import com.example.BankManagementSys.Reposityories.CurrencyRateRepository;
import com.example.BankManagementSys.Services.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CurrencyExchangeScheduler {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Scheduled(cron = "0 0 2 * * *") // Runs daily at 2 AM
    public void updateExchangeRates() {

        Map<String, BigDecimal> rates = currencyExchangeService.getExchangeRates();
        rates.forEach((currencyCode, rate) -> {
            // Save or update the rate in the database
            CurrencyRate currencyRate = currencyRateRepository.findByCurrencyCode(currencyCode)
                    .orElse(new CurrencyRate());
            currencyRate.setCurrencyCode(currencyCode);
            currencyRate.setRate(rate);
            currencyRate.setLastUpdated(LocalDateTime.now());
            currencyRateRepository.save(currencyRate);
        });
        System.out.println("\uD83D\uDCB2 Exchange rates updated in the database!");


    }
}
