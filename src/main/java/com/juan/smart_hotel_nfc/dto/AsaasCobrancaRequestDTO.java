package com.juan.smart_hotel_nfc.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsaasCobrancaRequestDTO {


    @NotBlank
    private String customer; //id do cliente no Asaas

    @NotBlank
    private String billingType; //BOLETO, CREDIT_CARD, PIX, UNDEFINED

    @NotNull
    private BigDecimal value;  //use BigDecimal para segurança, pois é um valor monetário
    @NotNull
    @JsonProperty("dueDate")
    private LocalDate dueDate;
    private String description;
    private String externalReference;
    private Payer payer;                // opcional dados do pagador, úitil para cartão e boleto
    private CreditCard creditCard;      // apenas para billingType = CREDIT_CARD, caso contrário deve ser null
    private Discount discount;
    private Integer installmentCount;
    private String returnUrl;
    private String additionalEmails;



    @Data
    static class Payer{
        private String name;
        private String email;
        @JsonProperty("cpfCnpj")
        private String cpfCnpj;
        private String phone;
        private String mobilePhone;
        private String address;
    }

    @Data
    static class CreditCard{
        private String holderName;
        private String number;
        private String expiryMonth;
        private String expiryYear;
        private String ccv;
        private String creditCardBrand;
    }

    @Data
    static class Discount{
    private BigDecimal value;
    private String type; // PERCENTAGE, FIXED
    }




}
