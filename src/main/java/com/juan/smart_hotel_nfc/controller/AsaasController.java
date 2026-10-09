package com.juan.smart_hotel_nfc.controller;


import com.juan.smart_hotel_nfc.dto.*;
import com.juan.smart_hotel_nfc.service.AsaasService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.juan.smart_hotel_nfc.service.ReservaService;


@RestController
@RequestMapping("/api/asaas")
public class AsaasController {

    private final AsaasService asaasService;
    private final ReservaService reservaService;

    @Value("${asaas.webhook.token}")
    private String asaasWebhookToken;

    public AsaasController(AsaasService asaasService, ReservaService reservaService) {
        this.asaasService = asaasService;
        this.reservaService = reservaService;
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

        //1 VERIFICA SE HÁ ALGUMA COBRANÇA COM O MESMO EXTERNAL REFERENCE
        reservaService.validarReservaParaCobranca(cobranca.getExternalReference());

        // 2 CRIANDO COBRANCA
        AsaasCobrancaResponseDTO response = asaasService.criarCobranca(cobranca);

        // 3 VINCULANDO A RESERVA AO PAGAMENTO
        reservaService.vincularCobrancaAsaas(cobranca.getExternalReference(), response.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cobrancas/{id}/pix")
    public ResponseEntity<AsaasPixQrCodeResponseDTO> buscarQrCodePix(@PathVariable String id){

        AsaasPixQrCodeResponseDTO response = asaasService.buscarQrCodePix(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(@RequestHeader(value = "asaas-access-token", required =false)String token,
                                               @RequestBody AsaasWebhookDTO webhook) {

        if(token == null || !token.equals(asaasWebhookToken)){
            System.out.println("Webhook recusado: token inválido.");
            return ResponseEntity.status(401).build();
        }

        System.out.println("Evento ASAAS: " + webhook.getEvent());

        if(webhook.getPayment() !=null){
            System.out.println("PAGAMENTO ASAAS: " + webhook.getPayment().getId());
            System.out.println("STATUS ASAAS: " + webhook.getPayment().getStatus());
        } else{
            System.out.println("Erro no webhook: payment é nulo");
        }

        reservaService.atualizarPagamentoWebhook(webhook);

        return ResponseEntity.ok().build();

    }



}

