package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.OrigemReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {

    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private BigDecimal valorTotal;
    private Long hospedeId;
    private Long quartoId;
    private Integer quantidadeAdultos;
    private Integer quantidadeCriancas;


    //   FAZENDO UMA RESERVA VIA POSTMAN

    //{
    //  "dataEntrada": "2026-08-10",
    //  "dataSaida": "2026-08-15",
    //  "valorTotal": 850.00,
    //  "quartoId": 1,
    //  "hospedeId": 1
    //}





}
