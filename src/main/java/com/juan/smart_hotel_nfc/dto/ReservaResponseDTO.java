package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.Reserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReservaResponseDTO {

    private Long id;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private BigDecimal valorTotal;
    private String status;
    private String nomeHospede;
    private String statusDaReserva;
    private Long quartoID;

    public ReservaResponseDTO() {}

    public ReservaResponseDTO(Reserva reserva){
        this.id = reserva.getId();
        this.dataEntrada = reserva.getDataEntrada();
        this.valorTotal = reserva.getValorTotal();
        this.dataSaida = reserva.getDataSaida();
        this.status = reserva.getStatus().name();
        this.nomeHospede = reserva.getHospede().getNome();
        this.quartoID = reserva.getHospede().getId();

    }


}
