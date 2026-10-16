
package com.juan.smart_hotel_nfc.dto;

import com.juan.smart_hotel_nfc.model.Reserva;
import com.juan.smart_hotel_nfc.model.StatusReserva;
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
    private StatusReserva statusDaReserva;
    private Long quartoID;
    private String asaasPaymentId;

    // Campos usados pelo checkout Asaas
    private String invoiceUrl;
    private String avisoCheckout;

    public ReservaResponseDTO() {
    }

    public ReservaResponseDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.dataEntrada = reserva.getDataEntrada();
        this.dataSaida = reserva.getDataSaida();
        this.valorTotal = reserva.getValorTotal();
        this.status = reserva.getStatusPagamento().name();
        this.statusDaReserva = reserva.getStatusReserva();
        this.nomeHospede = reserva.getHospede().getNome();
        this.quartoID = reserva.getQuarto().getId();
        this.asaasPaymentId = reserva.getAsaasPaymentId();
    }
}
