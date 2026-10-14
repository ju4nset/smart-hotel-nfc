package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.StatusQuarto;

import java.math.BigDecimal;


public record QuartoResponseDTO(
        Long id,
        String numero,
        Long categoriaId,
        String categoriaNome,
        StatusQuarto statusQuarto) {


}
