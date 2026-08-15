package com.juan.smart_hotel_nfc.config;


import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void configurarMercadoPago(){
        MercadoPagoConfig.setAccessToken(accessToken);

        System.out.println("Mercado pago configurado com sucesso");

    }




}
