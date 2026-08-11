package com.juan.smart_hotel_nfc.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;


//@NoArgsConstructor   CRIANDO O CONSTRUTOR VAZIO, OBRIGATÓRIO SPRING BOOT
//@AllArgsContructor   CRIANDO CONSTRUTOR COM ARGUMENTO
@Data
public class HospedeRequestDTO {




    private String nome;
    @CPF(message = "CPF inválido")
    @NotBlank(message = "CPF obrigatório")

    private String cpf;


    // 1. CONSTRUTOR VAZIO (Obrigatório para os frameworks)
    // O Spring Boot e o Jackson usam este construtor para criar o objeto "em branco"
    // assim que o JSON chega da internet, antes de preencher os dados.
    public HospedeRequestDTO() {
    }

    // 2. CONSTRUTOR COM ARGUMENTOS (Para o desenvolvedor)
    // Esse é o construtor que você vai usar se precisar instanciar essa classe manualmente
    // em alguma parte do seu código, passando os dados direto nos parênteses.
    public HospedeRequestDTO(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }



}
