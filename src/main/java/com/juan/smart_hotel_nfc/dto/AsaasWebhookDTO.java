package com.juan.smart_hotel_nfc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasWebhookDTO {

    private String id;
    private String event;
    private PaymentDTO payment;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentDTO{
        private String id;
        private String customer;
        private String status;
        private BigDecimal value;
        private String externalReference;

    }

}
