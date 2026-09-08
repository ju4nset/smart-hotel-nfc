package com.juan.smart_hotel_nfc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasPixQrCodeResponseDTO {

    private String encodedImage;
    private String payload;
    private String expirationDate;

}
