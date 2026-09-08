package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.*;
import com.juan.smart_hotel_nfc.service.AsaasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.juan.smart_hotel_nfc.dto.AsaasPixQrCodeResponseDTO;

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

    @GetMapping("/clientes")
    public String ListarClientes(){
        return asaasService.ListarClientes();
    }

    @GetMapping("/clientes/{cpfcnpj}")
    public String buscarClientePorCpfCnpj(@PathVariable String cpfcnpj){
        return asaasService.buscarCLientePorCpfCnpj(cpfcnpj);
    }

    @PostMapping("/cobrancas")
    public ResponseEntity<AsaasCobrancaResponseDTO> criarCobranca
            (@RequestBody AsaasCobrancaRequestDTO cobranca){

        AsaasCobrancaResponseDTO response = asaasService.criarCobranca(cobranca);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cobrancas/{id}/pix")
    public ResponseEntity<AsaasPixQrCodeResponseDTO> buscarQrCodePix(@PathVariable String id){

        AsaasPixQrCodeResponseDTO response = asaasService.buscarQrCodePix(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(@RequestBody AsaasWebhookDTO webhook) {

        System.out.println("Evento ASAAS: " + webhook.getEvent());

        if(webhook.getPayment() !=null){
            System.out.println("PAGAMENTO ASAAS: " + webhook.getPayment().getId());
            System.out.println("STATUS ASAAS: " + webhook.getPayment().getStatus());
        } else{
            System.out.println("Erro no webhook: payment é nulo");
        }
        return ResponseEntity.ok().build();

    }



}

