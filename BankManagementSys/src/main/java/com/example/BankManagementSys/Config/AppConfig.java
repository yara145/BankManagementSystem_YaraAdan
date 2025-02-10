package com.example.BankManagementSys.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // ✅ Marks this as a configuration class
public class AppConfig {

    @Bean // ✅ Registers RestTemplate as a Spring Bean (needed for API calls)
    public RestTemplate restTemplate() {
        return new RestTemplate(); // ✅ Allows making HTTP requests to fetch exchange rates
    }
}
