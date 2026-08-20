package com.juan.smart_hotel_nfc.dto;

import lombok.Data;

@Data
public class AsaasClienteRequestDTO {

    private String name;
    private String cpfCnpj;
    private String email;
    private String mobilePhone;

    public AsaasClienteRequestDTO(){

    }


    /*
    Essa classe vai representar o json que eu vou enviar para criar o cliente no Asaas, então ela precisa ter
    os mesmos campos que o json que o Asaas espera receber.
    */



}
