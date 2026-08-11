package com.juan.smart_hotel_nfc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Data
@Entity
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //ADICIONANDO UM NÚMERO FÍSICO NA PORTA
    @Column(nullable = false, unique = true)
    private String numero;

    // IMPOSSIVEL O SISTEMA CRIAR UM QUARTO SEM CATEGORIA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuartoCategoria categoria;

    //IMPOSSÍVEL O QUARTO SER CRIADO SEM UM STATUS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuarto statusQuarto;

    //UMA TRAVA QUE É IMPOSSÍVEL TER UM QUARTO OCUPADO SEM HOSPEDE
    @ManyToOne
    @JoinColumn(name = "hospede_id", nullable = true)
    private Hospede hospede;

}
