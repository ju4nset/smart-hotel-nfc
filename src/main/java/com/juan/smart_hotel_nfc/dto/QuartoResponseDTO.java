package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.StatusQuarto;


public record QuartoResponseDTO(
        Long id,
        String numero,
        Long categoriaId,
        String categoriaNome,
        StatusQuarto statusQuarto) {


}
