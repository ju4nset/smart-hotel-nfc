package com.juan.smart_hotel_nfc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasCobrancaResponseDTO {

    private String id;
    private String status;
    private String customer;
    private BigDecimal value;
    private String externalReference;
    private String dueDate;
    private String invoiceUrl;



    // Link de pagamento seguro ( quando retornado )
    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("bankSlipUrl")
    private String bankSlipUrl;      // link do boleto ( quando retornado )






}
