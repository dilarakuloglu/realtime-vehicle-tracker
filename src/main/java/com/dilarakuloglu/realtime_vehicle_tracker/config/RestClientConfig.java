package com.dilarakuloglu.realtime_vehicle_tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // OSRM base URL application.properties'ten gelir 
    @Bean
    public RestClient osrmClient(@Value("${osrm.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept-Encoding", "identity")
                .build();
    }
}
