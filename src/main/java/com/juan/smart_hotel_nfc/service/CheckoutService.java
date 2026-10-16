package com.juan.smart_hotel_nfc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.juan.smart_hotel_nfc.dto.AsaasClienteRequestDTO;
import com.juan.smart_hotel_nfc.dto.AsaasClienteResponseDTO;
import com.juan.smart_hotel_nfc.dto.AsaasCobrancaRequestDTO;
import com.juan.smart_hotel_nfc.dto.AsaasCobrancaResponseDTO;
import com.juan.smart_hotel_nfc.model.Hospede;
import com.juan.smart_hotel_nfc.model.Reserva;
import com.juan.smart_hotel_nfc.model.StatusPagamento;
import com.juan.smart_hotel_nfc.model.StatusReserva;
import com.juan.smart_hotel_nfc.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.NoSuchElementException;

@Service
public class CheckoutService {
    private final ReservaRepository reservaRepository;
    private final AsaasService asaasService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CheckoutService(ReservaRepository reservaRepository, AsaasService asaasService) {
        this.reservaRepository = reservaRepository;
        this.asaasService = asaasService;
    }

    @Transactional
    public String criarLinkPagamento(Long reservaId){
        // 1. Buscar reserva no PostgreSQL.
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() ->
                        new NoSuchElementException("Reserva não encontrada.")
                );

        // 2. Aceitar somente reservas pendentes.
        if (reserva.getStatusReserva() != StatusReserva.PENDENTE
                || reserva.getStatusPagamento() != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Reserva não está pendente.");
        }

        // 3. Não criar outra cobrança para a mesma reserva.
        if (reserva.getAsaasPaymentId() != null
                && !reserva.getAsaasPaymentId().isBlank()) {
            throw new IllegalStateException(
                    "Esta reserva já possui uma cobrança."
            );
        }

        // 4. Buscar ou cadastrar o cliente Asaas.
        String customerId = buscarOuCriarCliente(reserva.getHospede());

        // 5. Montar a cobrança com o valor do banco.
        AsaasCobrancaRequestDTO request =
                new AsaasCobrancaRequestDTO();

        request.setCustomer(customerId);
        request.setBillingType("UNDEFINED");
        request.setValue(reserva.getValorTotal());
        request.setDueDate(
                LocalDate.now(ZoneId.of("America/Sao_Paulo")).plusDays(1)
        );
        request.setDescription(
                "Aurea Hotel - Reserva " + reserva.getId()
        );
        request.setExternalReference(
                String.valueOf(reserva.getId())
        );

        // 6. Enviar para o Asaas Sandbox.
        AsaasCobrancaResponseDTO cobranca =
                asaasService.criarCobranca(request);

        if (cobranca.getId() == null
                || cobranca.getId().isBlank()
                || cobranca.getInvoiceUrl() == null
                || cobranca.getInvoiceUrl().isBlank()) {
            throw new IllegalStateException(
                    "Resposta incompleta da cobrança Asaas."
            );
        }

        if (cobranca.getValue() == null
                || cobranca.getValue().compareTo(reserva.getValorTotal()) != 0
                || !String.valueOf(reserva.getId())
                .equals(cobranca.getExternalReference())) {
            throw new IllegalStateException(
                    "A cobrança não corresponde à reserva."
            );
        }

        // 7. Salvar o ID do pagamento.
        reserva.setAsaasPaymentId(cobranca.getId());
        reservaRepository.saveAndFlush(reserva);

        // 8. Devolver somente o link.
        return cobranca.getInvoiceUrl();
    }

    private String buscarOuCriarCliente(Hospede hospede) {

        String cpf = hospede.getCpf().replaceAll("\\D", "");

        try {
            String resposta = asaasService.buscarCLientePorCpfCnpj(cpf);
            JsonNode clientes = objectMapper.readTree(resposta).path("data");

            if (!clientes.isArray()) {
                throw new IllegalStateException(
                        "Resposta inválida da consulta de clientes."
                );
            }

            for (JsonNode cliente : clientes) {
                String cpfAsaas = cliente.path("cpfCnpj")
                        .asText().replaceAll("\\D", "");

                String id = cliente.path("id").asText();

                if (cpf.equals(cpfAsaas) && !id.isBlank()) {
                    return id;
                }
            }

            AsaasClienteRequestDTO novoCliente =
                    new AsaasClienteRequestDTO();

            novoCliente.setName(hospede.getNome());
            novoCliente.setCpfCnpj(cpf);
            novoCliente.setMobilePhone(
                    hospede.getTelefone().replaceAll("\\D", "")
            );

            AsaasClienteResponseDTO criado =
                    asaasService.criarCliente(novoCliente);

            if (criado.getId() == null || criado.getId().isBlank()) {
                throw new IllegalStateException(
                        "O Asaas não retornou o ID do cliente."
                );
            }

            return criado.getId();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não foi possível identificar o cliente Asaas.",
                    e
            );
        }
    }
}
