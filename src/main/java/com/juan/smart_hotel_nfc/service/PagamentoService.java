package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.CheckOutResponse;
import com.juan.smart_hotel_nfc.exception.PagamentoException;
import com.juan.smart_hotel_nfc.exception.ReservaNaoEncontradaException;
import com.juan.smart_hotel_nfc.model.Reserva;
import com.juan.smart_hotel_nfc.model.StatusPagamento;
import com.juan.smart_hotel_nfc.repository.ReservaRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PagamentoService {

    private static final Logger log =
            LoggerFactory.getLogger(PagamentoService.class);

    private final ReservaRepository reservaRepository;
    private final PreferenceClient preferenceClient;
    private final PaymentClient paymentClient;

    // Endereço público do sistema, por exemplo a URL do ngrok.
    @Value("${app.public-url}")
    private String publicUrl;

    public PagamentoService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        this.preferenceClient = new PreferenceClient();
        this.paymentClient = new PaymentClient();
    }

    // Busca a reserva, valida o valor e cria o checkout no Mercado Pago.
    public CheckOutResponse criarCheckOut(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);
        validarValor(reserva.getValorTotal());

        PreferenceItemRequest item = montarItem(reserva);
        PreferenceRequest preferenceRequest =
                montarPreferencia(reserva, item);

        return enviarParaMercadoPago(preferenceRequest, reservaId);
    }

    /*
     * Recebe a notificação do Mercado Pago.
     * Procura o tipo do evento e o ID do pagamento
     * tanto no corpo JSON quanto nos parâmetros da URL.
     */
    @Transactional
    public void processarWebhook(
            Map<String, Object> payload,
            Map<String, String> parametros
    ) {
        log.info("Payload do webhook: {}", payload);
        log.info("Parâmetros do webhook: {}", parametros);

        if (payload == null) {
            payload = Map.of();
        }

        if (parametros == null) {
            parametros = Map.of();
        }

        Object tipo = payload.get("type");

        if (tipo == null) {
            tipo = parametros.get("type");
        }

        if (tipo == null) {
            tipo = parametros.get("topic");
        }

        if (tipo == null ||
                !"payment".equalsIgnoreCase(tipo.toString())) {

            log.info(
                    "Webhook ignorado. Tipo recebido: {}",
                    tipo
            );
            return;
        }

        Object pagamentoIdObject = null;
        Object dataObject = payload.get("data");

        if (dataObject instanceof Map<?, ?>) {
            Map<?, ?> data = (Map<?, ?>) dataObject;
            pagamentoIdObject = data.get("id");
        }

        if (pagamentoIdObject == null) {
            pagamentoIdObject = parametros.get("data.id");
        }

        if (pagamentoIdObject == null) {
            pagamentoIdObject = parametros.get("id");
        }

        if (pagamentoIdObject == null) {
            log.warn(
                    "Webhook sem ID. Payload: {}. Parâmetros: {}",
                    payload,
                    parametros
            );
            return;
        }

        try {
            Long pagamentoId =
                    Long.valueOf(pagamentoIdObject.toString());

            processarPagamento(pagamentoId);

        } catch (NumberFormatException exception) {
            log.warn(
                    "ID de pagamento inválido: {}",
                    pagamentoIdObject
            );
        }
    }

    /*
     * Consulta o pagamento diretamente no Mercado Pago.
     * A externalReference contém o ID da nossa reserva.
     */
    private void processarPagamento(Long pagamentoId) {
        try {
            Payment payment = paymentClient.get(pagamentoId);

            String externalReference =
                    payment.getExternalReference();

            if (externalReference == null ||
                    externalReference.isBlank()) {

                throw new IllegalArgumentException(
                        "O pagamento não possui externalReference"
                );
            }

            Long reservaId =
                    Long.valueOf(externalReference);

            Reserva reserva = buscarReserva(reservaId);

            validarPagamento(payment, reserva);
            atualizarStatus(reserva, payment.getStatus());

        } catch (NumberFormatException exception) {
            throw new PagamentoException(
                    "A externalReference não possui um ID de reserva válido",
                    exception
            );

        } catch (MPApiException | MPException exception) {
            log.error(
                    "Erro ao consultar pagamento {}: {}",
                    pagamentoId,
                    exception.getMessage(),
                    exception
            );

            throw new PagamentoException(
                    "Não foi possível consultar o pagamento",
                    exception
            );
        }
    }

    // Confere se o valor e a moeda correspondem à reserva.
    private void validarPagamento(
            Payment payment,
            Reserva reserva
    ) {
        BigDecimal valorPago =
                payment.getTransactionAmount();

        BigDecimal valorReserva =
                reserva.getValorTotal();

        if (valorPago == null ||
                valorReserva == null ||
                valorPago.compareTo(valorReserva) != 0) {

            throw new IllegalArgumentException(
                    "O valor pago não corresponde ao valor da reserva"
            );
        }

        if (!"BRL".equalsIgnoreCase(
                payment.getCurrencyId()
        )) {
            throw new IllegalArgumentException(
                    "A moeda do pagamento é inválida"
            );
        }
    }

    /*
     * Converte o status do Mercado Pago para o status
     * utilizado no nosso sistema e salva a reserva.
     */
    private void atualizarStatus(
            Reserva reserva,
            String statusMercadoPago
    ) {
        if (statusMercadoPago == null) {
            return;
        }

        StatusPagamento novoStatus;

        if ("approved".equalsIgnoreCase(statusMercadoPago)) {
            novoStatus = StatusPagamento.PAGO;

        } else if (
                "rejected".equalsIgnoreCase(statusMercadoPago) ||
                        "cancelled".equalsIgnoreCase(statusMercadoPago)
        ) {
            novoStatus = StatusPagamento.RECUSADO;

        } else if (
                "pending".equalsIgnoreCase(statusMercadoPago) ||
                        "in_process".equalsIgnoreCase(statusMercadoPago) ||
                        "authorized".equalsIgnoreCase(statusMercadoPago)
        ) {
            novoStatus =
                    StatusPagamento.AGUARDANDO_PAGAMENTO;

        } else {
            log.info(
                    "Status do Mercado Pago ainda não tratado: {}",
                    statusMercadoPago
            );
            return;
        }

        // Uma reserva já paga não pode voltar para recusada ou pendente.
        if (reserva.getStatus() == StatusPagamento.PAGO &&
                novoStatus != StatusPagamento.PAGO) {

            log.info(
                    "Reserva {} já está paga. Alteração ignorada",
                    reserva.getId()
            );
            return;
        }

        // Evita salvar novamente quando o status não mudou.
        if (reserva.getStatus() == novoStatus) {
            log.info(
                    "Reserva {} já possui o status {}",
                    reserva.getId(),
                    novoStatus
            );
            return;
        }

        reserva.setStatus(novoStatus);
        reservaRepository.save(reserva);

        log.info(
                "Reserva {} atualizada para {}",
                reserva.getId(),
                novoStatus
        );
    }

    // Busca a reserva no banco ou lança uma exceção.
    private Reserva buscarReserva(Long reservaId) {
        return reservaRepository
                .findById(reservaId)
                .orElseThrow(() ->
                        new ReservaNaoEncontradaException(reservaId)
                );
    }

    // Impede a criação de pagamentos com valor nulo ou negativo.
    private void validarValor(BigDecimal valorTotal) {
        if (valorTotal == null ||
                valorTotal.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A reserva não possui um valor válido para pagamento"
            );
        }
    }

    // Monta o item que aparecerá no checkout.
    private PreferenceItemRequest montarItem(Reserva reserva) {
        return PreferenceItemRequest.builder()
                .id(String.valueOf(reserva.getId()))
                .title("Reserva no Smart Hotel")
                .description(
                        "Pagamento da reserva: " + reserva.getId()
                )
                .quantity(1)
                .currencyId("BRL")
                .unitPrice(reserva.getValorTotal())
                .build();
    }

    /*
     * Monta a preferência com os endereços de retorno,
     * webhook e ID da reserva na externalReference.
     */
    private PreferenceRequest montarPreferencia(
            Reserva reserva,
            PreferenceItemRequest item
    ) {
        String baseUrl =
                publicUrl.replaceAll("/+$", "");

        return PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(
                        String.valueOf(reserva.getId())
                )
                .autoReturn("approved")
                .backUrls(
                        PreferenceBackUrlsRequest.builder()
                                .success(
                                        baseUrl +
                                                "/api/pagamentos/retorno/sucesso"
                                )
                                .pending(
                                        baseUrl +
                                                "/api/pagamentos/retorno/pendente"
                                )
                                .failure(
                                        baseUrl +
                                                "/api/pagamentos/retorno/falha"
                                )
                                .build()
                )
                .notificationUrl(
                        baseUrl +
                                "/api/pagamentos/webhook/mercadopago"
                )
                .build();
    }

    // Envia a preferência e devolve o ID e a URL do checkout.
    private CheckOutResponse enviarParaMercadoPago(
            PreferenceRequest preferenceRequest,
            Long reservaId
    ) {
        try {
            Preference preference =
                    preferenceClient.create(preferenceRequest);

            return new CheckOutResponse(
                    preference.getId(),
                    preference.getInitPoint()
            );

        } catch (MPApiException | MPException exception) {
            log.error(
                    "Erro ao criar preferência de pagamento para reserva {}: {}",
                    reservaId,
                    exception.getMessage(),
                    exception
            );

            throw new PagamentoException(
                    "Não foi possível criar o pagamento no Mercado Pago.",
                    exception
            );
        }
    }
}