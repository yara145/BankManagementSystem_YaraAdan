package com.example.BankManagementSys.Controllers;

import com.example.BankManagementSys.Entities.CurrencyRate;
import com.example.BankManagementSys.Services.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currency-rates")
public class CurrencyRateController {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    // ✅ Get all stored currency rates
    @GetMapping("getAll")
    public ResponseEntity<List<CurrencyRate>> getAllCurrencyRates() {
        List<CurrencyRate> rates = currencyExchangeService.getAllStoredRates();
        return rates.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(rates)
                : ResponseEntity.ok(rates);
    }
}
