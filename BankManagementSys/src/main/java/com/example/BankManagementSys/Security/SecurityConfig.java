package com.example.BankManagementSys.Security;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*")); // Allow all origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow these methods
        config.setAllowedHeaders(List.of("Authorization", "Content-type", "Origins")); // Allow specific headers
        config.setExposedHeaders(List.of("Authorization")); // Expose Authorization header
        source.registerCorsConfiguration("/**", config); // Apply CORS configuration to all endpoints
        return new CorsFilter(source);
    }
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean(CorsFilter corsFilter) {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        registration.setOrder(0); // Set order of the filter
        return registration;
    }

}
