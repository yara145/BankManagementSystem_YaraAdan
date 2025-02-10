package com.example.BankManagementSys.Services;

import com.example.BankManagementSys.Entities.CurrencyRate;
import com.example.BankManagementSys.Reposityories.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyExchangeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/ILS?apikey=b8e9087d952d7da6119fb800114aaf36";

    private Map<String, BigDecimal> cachedRates = new HashMap<>();
    private LocalDateTime lastUpdateTime = null;

    public BigDecimal getExchangeRateForCurrency(String currencyCode) {
        if (cachedRates.isEmpty() || lastUpdateTime == null || lastUpdateTime.isBefore(LocalDateTime.now().minusMinutes(10))) {
            updateExchangeRatesFromAPI(); // Fetch new rates only if cache is empty or outdated
            lastUpdateTime = LocalDateTime.now();
        }

        return cachedRates.getOrDefault(currencyCode, BigDecimal.ZERO);
    }

    @Transactional
    public void updateExchangeRatesFromAPI() {
        Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("Failed to fetch exchange rates from API");
        }

        Map<String, Object> rates = (Map<String, Object>) response.get("rates");
        cachedRates.clear();

        rates.forEach((currency, rate) -> {
            BigDecimal exchangeRate = new BigDecimal(rate.toString());

            // ✅ Get or create CurrencyRate object
            CurrencyRate currencyRate = currencyRateRepository.findByCurrencyCode(currency)
                    .orElseGet(() -> {
                        CurrencyRate newRate = new CurrencyRate();
                        newRate.setCurrencyCode(currency);
                        return newRate;
                    });

            currencyRate.setRate(exchangeRate);
            currencyRate.setLastUpdated(LocalDateTime.now());
            currencyRateRepository.save(currencyRate);

            // ✅ Also update the cache
            cachedRates.put(currency, exchangeRate);
        });
    }

    // ✅ Fetch exchange rates from the API and convert them to <String, BigDecimal>
    public Map<String, BigDecimal> getExchangeRates() {
        Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("Failed to fetch exchange rates from API");
        }

        Map<String, Object> ratesObject = (Map<String, Object>) response.get("rates");

        return ratesObject.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new BigDecimal(e.getValue().toString())
                ));
    }

    // ✅ Convert amount from ILS to target currency using DB rates
    public BigDecimal convertFromILS(String targetCurrency, BigDecimal amountInILS) {
        BigDecimal exchangeRate = getExchangeRateForCurrency(targetCurrency);
        return amountInILS.multiply(exchangeRate);
    }

    public BigDecimal convertCurrencyAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount; // No conversion needed
        }

        BigDecimal exchangeRate = getExchangeRateForCurrency(fromCurrency);
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid exchange rate for currency: " + fromCurrency);
        }

        System.out.println("🚀 DEBUG: Converting " + amount + " from " + fromCurrency + " to " + toCurrency);
        System.out.println("Exchange Rate: " + exchangeRate);

        if (fromCurrency.equalsIgnoreCase("ILS")) {
            // ✅ Convert from ILS → Other Currency (Divide)
            return amount.divide(exchangeRate, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        } else if (toCurrency.equalsIgnoreCase("ILS")) {
            // ✅ Convert from Foreign Currency → ILS (Multiply)
            return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        } else {
            // ✅ Convert between two foreign currencies (Convert to ILS first, then to target currency)
            BigDecimal ilsAmount = amount.multiply(exchangeRate); // Convert to ILS first
            BigDecimal targetRate = getExchangeRateForCurrency(toCurrency);
            if (targetRate == null || targetRate.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Invalid exchange rate for currency: " + toCurrency);
            }
            return ilsAmount.divide(targetRate, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public List<CurrencyRate> getAllStoredRates() {
        return currencyRateRepository.findAll();
    }

}
