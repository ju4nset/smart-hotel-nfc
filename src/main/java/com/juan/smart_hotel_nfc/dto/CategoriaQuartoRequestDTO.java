package com.juan.smart_hotel_nfc.dto;

import java.math.BigDecimal;

public record CategoriaQuartoRequestDTO(String nome, int capacidadeMaxima, int capacidadeAdultos, int capacidadeCriancas,
                                        BigDecimal valorBase, String descricao, BigDecimal adicionalHospede) {


}
