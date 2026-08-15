package com.juan.smart_hotel_nfc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AsaasService {

    private final RestClient restClient;

    public AsaasService(
    @Value("${asaas.api.url") String apiUrl,
    @Value("${asaas.api.token") String apiKey){
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("access_token", apiKey)
                .defaultHeader("User-Agent", "smartHotel/1.0")
                .build();
    }


    public String listarCLientes(){
        return restClient.get()
                .uri("/customers")
                .retrieve()
                .body(String.class);

    }


}
