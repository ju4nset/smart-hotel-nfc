package com.juan.smart_hotel_nfc.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private Long hospedeId;
    private Long quartoId;
    private Integer quantidadeAdultos;
    private Integer quantidadeCriancas;
}