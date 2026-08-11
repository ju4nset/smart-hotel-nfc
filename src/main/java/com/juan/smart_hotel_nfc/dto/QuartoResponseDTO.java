package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.QuartoCategoria;
import com.juan.smart_hotel_nfc.model.StatusQuarto;


public record QuartoResponseDTO(String numero, QuartoCategoria quartoCategoria,
StatusQuarto statusQuarto) {
}
