
package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.ReservaRequestDTO;
import com.juan.smart_hotel_nfc.dto.ReservaResponseDTO;
import com.juan.smart_hotel_nfc.service.CheckoutService;
import com.juan.smart_hotel_nfc.service.ReservaService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

    private static final Logger log =
            LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;
    private final CheckoutService checkoutService;

    // Injeção dos serviços pelo construtor.
    public ReservaController(
            ReservaService reservaService,
            CheckoutService checkoutService
    ) {
        this.reservaService = reservaService;
        this.checkoutService = checkoutService;
    }

    // POST /api/reserva
    // Cadastra a reserva e tenta gerar a cobrança no Asaas.
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> cadastrarReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {

        // 1. Criar a reserva no PostgreSQL.
        ReservaResponseDTO reserva =
                reservaService.cadastraReserva(reservaRequestDTO);

        log.info(
                "Reserva {} criada com sucesso.",
                reserva.getId()
        );

        // 2. Tentar criar a cobrança no Asaas.
        try {

            String invoiceUrl =
                    checkoutService.criarLinkPagamento(reserva.getId());

            // 3. Adicionar o link à resposta.
            reserva.setInvoiceUrl(invoiceUrl);

            log.info(
                    "Checkout criado para a reserva {}.",
                    reserva.getId()
            );

        } catch (Exception e) {

            // A reserva já foi cadastrada.
            // Registrar a falha sem excluir a reserva.
            log.error(
                    "Falha ao criar checkout da reserva {}.",
                    reserva.getId(),
                    e
            );

            reserva.setAvisoCheckout(
                    "Reserva registrada, mas o pagamento "
                            + "não está disponível no momento. "
                            + "Não crie outra reserva. "
                            + "Verifique o pagamento no sistema."
            );
        }

        // 4. Devolver a reserva ao React.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reserva);
    }

    // GET /api/reserva
    // Lista todas as reservas.
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {

        List<ReservaResponseDTO> reservas =
                reservaService.ListarTodasAsReservas();

        return ResponseEntity.ok(reservas);
    }
}
