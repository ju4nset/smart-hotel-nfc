package com.juan.smart_hotel_nfc.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class CategoriaQuarto {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable =false)
    private String nome;

    @Column(nullable =false)
    private int capacidadeMaxima;

    @Column(nullable = false)
    private int capacidadeAdultos;

    @Column(nullable = false)
    private int capacidadeCriancas;

   @Column(nullable = false, precision =10, scale = 2)
    private BigDecimal valorBase;

    private String descricao;


}
