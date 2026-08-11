package com.juan.smart_hotel_nfc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
@MappedSuperclass
public class Pessoa {


    //CRIANDO O ID E DIZENDO QUE QUEM FICA RESPONSÁVEL PELO GERAÇÃO DO ID, É O BANCO DE DADOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //NECESSÁRIO INFORMAR O NOME
    @NotBlank(message = "Obrigatório informar o nome")
    @Column(nullable = false)
    private String nome;

    // NECESSÁRIO INFORMAR CPF E UM CPF VÁLIDO
    @CPF(message = "CPF inválido")
    @NotBlank(message = "CPF obrigatório")
    @Column(nullable = false)
    private String cpf;

}
