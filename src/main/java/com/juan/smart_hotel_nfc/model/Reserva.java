package com.juan.smart_hotel_nfc.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Reserva {

    //CRIANDO O ID E DIZENDO QUE QUEM FICA RESPONSÁVEL PELO GERAÇÃO DO ID, É O BANCO DE DADOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate dataEntrada;

    @Column(nullable = false)
    private LocalDate dataSaida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private OrigemReserva origemReserva;

    @Column(name = "asaas_payment_id")
    private String asaasPaymentId;

    @ManyToOne
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reserva", nullable = false)
    private StatusReserva statusReserva = StatusReserva.PENDENTE;

}
