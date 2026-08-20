package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.AsaasClienteRequestDTO;
import com.juan.smart_hotel_nfc.dto.AsaasClienteResponseDTO;
import com.juan.smart_hotel_nfc.service.AsaasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asaas")
public class AsaasController {

    private final AsaasService asaasService;



    public AsaasController(AsaasService asaasService){
        this.asaasService = asaasService;

    }

    @PostMapping("/clientes")
    public AsaasClienteResponseDTO criarCliente(@RequestBody AsaasClienteRequestDTO cliente){
        return asaasService.criarCliente(cliente);
    }


}
