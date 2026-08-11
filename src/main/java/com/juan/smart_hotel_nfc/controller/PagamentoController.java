package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.CheckOutResponse;
import com.juan.smart_hotel_nfc.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
public class    PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService){
        this.pagamentoService = pagamentoService;
    }


    @PostMapping("/{reservaId}/checkout")
    public ResponseEntity<CheckOutResponse> criarCheckOut(@PathVariable Long reservaId){
        CheckOutResponse response = pagamentoService.criarCheckOut(reservaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> receberNotificacao(@RequestBody(required = false)Map<String, Object> payload,
                                                   @RequestParam Map<String, String> parametros
    ){
        pagamentoService.processarWebhook(payload, parametros);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/retorno/sucesso")
    public ResponseEntity<String> retornoSucesso(){
        return ResponseEntity.ok("Pagamento aprovado!");
    }

    @GetMapping("/retorno/pendente")
    public ResponseEntity<String> retornoPendente(){
        return ResponseEntity.ok("Pagamento pendente!");
    }

    @GetMapping("/retorno/falha")
    public ResponseEntity<String> retornoFalha(){
        return ResponseEntity.ok("Pagamento falhou!");
    }






}
