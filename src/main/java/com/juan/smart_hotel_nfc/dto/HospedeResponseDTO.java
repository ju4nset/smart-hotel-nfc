package com.juan.smart_hotel_nfc.dto;

import lombok.Data;

@Data
public class HospedeResponseDTO {

    private String nome;
    private String cpf;
    private long id;


    // CONTRUTOR VAZIO PARA O FRAMEWORK
    public HospedeResponseDTO(){

    }

    public HospedeResponseDTO(String nome, String cpf, long id) {
        this.nome = nome;
        this.cpf = cpf;
        this.id = id;
    }






}
