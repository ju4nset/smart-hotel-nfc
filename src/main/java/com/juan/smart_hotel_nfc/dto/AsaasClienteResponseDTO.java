package com.juan.smart_hotel_nfc.dto;

import lombok.Data;

@Data
public class AsaasClienteResponseDTO {

    private String id;
    private String nome;
    private String cpfCnpj;
    private String email;


    public AsaasClienteResponseDTO(){

    }

    public AsaasClienteResponseDTO(String id, String nome, String cpfCnpj, String email){
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.id = id;
        this.nome = nome;

    }

    /*
        Response é o que eu vou receber do Asaas quando eu criar um cliente, então ele precisa ter os mesmos campos
        que o json que o Asaas vai me enviar.
     */




}
