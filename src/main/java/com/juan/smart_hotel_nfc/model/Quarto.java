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
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaQuarto categoria;

    //IMPOSSÍVEL O QUARTO SER CRIADO SEM UM STATUS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuarto statusQuarto;


}
