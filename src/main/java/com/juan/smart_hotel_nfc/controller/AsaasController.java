package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.service.AsaasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asaas")
public class AsaasController {

    private final AsaasService asaasService;


    public AsaasController(AsaasService asaasService){
        this.asaasService = asaasService;
    }

    @GetMapping("/clientes")
    public String ListarCLientes(){
        return asaasService.listarCLientes();
    }








}
