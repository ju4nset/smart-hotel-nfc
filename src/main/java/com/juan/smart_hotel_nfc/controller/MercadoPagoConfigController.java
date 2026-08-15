package com.juan.smart_hotel_nfc.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class MercadoPagoConfigController {



    @Value("${mercadopago.public-key}")
    private String publicKey;



    @GetMapping("/mercadopago")
    public Map<String, String> buscarConfiguracao(){
        return Map.of("publicKey", publicKey);
    }

}
