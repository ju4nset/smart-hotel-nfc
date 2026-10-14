package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.AsaasWebhookDTO;
import com.juan.smart_hotel_nfc.dto.ReservaRequestDTO;
import com.juan.smart_hotel_nfc.dto.ReservaResponseDTO;
import com.juan.smart_hotel_nfc.exception.ReservaConflitanteException;
import com.juan.smart_hotel_nfc.model.*;
import com.juan.smart_hotel_nfc.repository.AsaasWebhookEventoRepository;
import com.juan.smart_hotel_nfc.repository.HospedeRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import com.juan.smart_hotel_nfc.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private TarifaService tarifaService;

    @Autowired
    private HospedeRepository hospedeRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    @Autowired
    private AsaasWebhookEventoRepository asaasWebhookEventoRepository;

    @Transactional
    public ReservaResponseDTO cadastraReserva(ReservaRequestDTO reservaRequestDTO) {

        // 1. Validar as datas antes de consultar o banco.
        validarDatas(reservaRequestDTO.getDataEntrada(), reservaRequestDTO.getDataSaida());

        // 2. Buscar o hóspede e o quarto existentes.
        Hospede hospede = hospedeRepository.findById(reservaRequestDTO.getHospedeId())
                .orElseThrow(() -> new NoSuchElementException("Hóspede não encontrado."));

        // Essa consulta possui bloqueio pessimista para evitar reservas simultâneas.
        Quarto quarto = quartoRepository.buscarQuartoParaReserva(reservaRequestDTO.getQuartoId())
                .orElseThrow(() -> new NoSuchElementException("Quarto não encontrado."));

        validarStatusQuarto(quarto);
        validarCapacidade(
                quarto,
                reservaRequestDTO.getQuantidadeAdultos(),
                reservaRequestDTO.getQuantidadeCriancas()
        );

        // 3. Somar adultos e crianças após validar os valores.
        int quantidadeHospedes = reservaRequestDTO.getQuantidadeAdultos()
                + reservaRequestDTO.getQuantidadeCriancas();

        // 4. Conferir se não há outra reserva no período.
        verificarDisponibilidade(
                quarto.getId(),
                reservaRequestDTO.getDataEntrada(),
                reservaRequestDTO.getDataSaida()
        );

        // 5. Calcular o valor no servidor; não confiar no valor informado pelo cliente.
        BigDecimal valorTotal = tarifaService.calcularValorTotal(
                quarto.getCategoria(),
                quantidadeHospedes,
                reservaRequestDTO.getDataEntrada(),
                reservaRequestDTO.getDataSaida()
        );

        // 6. Montar e salvar a reserva.
        Reserva novaReserva = new Reserva();
        novaReserva.setHospede(hospede);
        novaReserva.setQuarto(quarto);
        novaReserva.setValorTotal(valorTotal);
        novaReserva.setDataEntrada(reservaRequestDTO.getDataEntrada());
        novaReserva.setDataSaida(reservaRequestDTO.getDataSaida());
        novaReserva.setStatusPagamento(StatusPagamento.PENDENTE);
        novaReserva.setStatusReserva(StatusReserva.PENDENTE);
        novaReserva.setOrigemReserva(OrigemReserva.WHATSAPP);
        novaReserva.setQuantidadeAdultos(reservaRequestDTO.getQuantidadeAdultos());
        novaReserva.setQuantidadeCriancas(reservaRequestDTO.getQuantidadeCriancas());

        Reserva reservaSalva = reservaRepository.save(novaReserva);
        return new ReservaResponseDTO(reservaSalva);
    }

    private void validarStatusQuarto(Quarto quarto) {
        if (quarto.getStatusQuarto() != StatusQuarto.ATIVO) {
            throw new IllegalStateException("O quarto não está disponível para reservas.");
        }
    }

    private void validarCapacidade(Quarto quarto, Integer adultos, Integer criancas) {
        if (adultos == null || criancas == null) {
            throw new IllegalArgumentException("Quantidade de pessoas não informada.");
        }

        if (adultos < 1 || criancas < 0) {
            throw new IllegalArgumentException("Quantidade de adultos ou crianças inválida.");
        }

        CategoriaQuarto categoria = quarto.getCategoria();
        int totalHospedes = adultos + criancas;

        if (totalHospedes > categoria.getCapacidadeMaxima()) {
            throw new IllegalArgumentException(
                    "A quantidade de hóspedes ultrapassa a capacidade máxima do quarto."
            );
        }

        if (adultos > categoria.getCapacidadeAdultos()
                || criancas > categoria.getCapacidadeCriancas()) {
            throw new IllegalArgumentException(
                    "Quantidade de adultos ou crianças acima do permitido."
            );
        }
    }

    public List<ReservaResponseDTO> ListarTodasAsReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public void validarReservaParaCobranca(String externalReference) {
        Long reservaId;
        try {
            reservaId = Long.parseLong(externalReference);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("External Reference inválido: " + externalReference);
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada: " + reservaId));

        if (reserva.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new IllegalArgumentException("Reserva já está paga.");
        }

        if (reserva.getAsaasPaymentId() != null && !reserva.getAsaasPaymentId().isBlank()) {
            throw new IllegalArgumentException("Essa reserva já possui uma cobrança válida.");
        }
    }

    // Processamento de notificações recebidas do Asaas.
    // A autenticação da origem do webhook deve ocorrer no controller/filtro.
    @Transactional
    public void atualizarPagamentoWebhook(AsaasWebhookDTO asaasWebhookDTO) {
        if (asaasWebhookDTO == null || asaasWebhookDTO.getPayment() == null) {
            System.out.println("Webhook sem pagamento.");
            return;
        }

        String eventId = asaasWebhookDTO.getId();
        if (eventId == null || eventId.isBlank()) {
            System.out.println("Webhook sem evento ID.");
            return;
        }

        if (asaasWebhookEventoRepository.existsByEventId(eventId)) {
            System.out.println("Webhook já processado: " + eventId);
            return;
        }

        String externalReference = asaasWebhookDTO.getPayment().getExternalReference();
        if (externalReference == null || externalReference.isBlank()) {
            System.out.println("Webhook sem externalReference.");
            return;
        }

        Long reservaId;
        try {
            reservaId = Long.parseLong(externalReference);
        } catch (NumberFormatException e) {
            System.out.println("ExternalReference inválido: " + externalReference);
            return;
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada."));

        String paymentIdRecebido = asaasWebhookDTO.getPayment().getId();
        if (reserva.getAsaasPaymentId() == null
                || !reserva.getAsaasPaymentId().equals(paymentIdRecebido)) {
            System.out.println("Payment do webhook não corresponde à cobrança da reserva.");
            return;
        }

        String evento = asaasWebhookDTO.getEvent();
        if (evento == null) {
            System.out.println("Webhook sem tipo de evento.");
            return;
        }

        switch (evento) {
            case "PAYMENT_RECEIVED":
            case "PAYMENT_CONFIRMED":
                reserva.setStatusPagamento(StatusPagamento.PAGO);
                reserva.setStatusReserva(StatusReserva.CONFIRMADA);
                break;
            case "PAYMENT_OVERDUE":
                reserva.setStatusPagamento(StatusPagamento.VENCIDO);
                break;
            case "PAYMENT_REFUNDED":
                reserva.setStatusPagamento(StatusPagamento.ESTORNADO);
                break;
            case "PAYMENT_DELETED":
                reserva.setStatusPagamento(StatusPagamento.CANCELADO);
                break;
            default:
                System.out.println("Evento Asaas não tratado: " + evento);
                return;
        }

        reservaRepository.save(reserva);

        AsaasWebhookEvento eventoProcessado = new AsaasWebhookEvento();
        eventoProcessado.setEventId(eventId);
        eventoProcessado.setEventType(evento);
        eventoProcessado.setPaymentId(paymentIdRecebido);
        eventoProcessado.setProcessadoEm(LocalDateTime.now());
        asaasWebhookEventoRepository.save(eventoProcessado);
    }

    public void vincularCobrancaAsaas(String externalReference, String paymentId) {
        Long reservaId;
        try {
            reservaId = Long.parseLong(externalReference);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ExternalReference inválido: " + externalReference);
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada: " + reservaId));

        reserva.setAsaasPaymentId(paymentId);
        reservaRepository.save(reserva);
    }

    private ReservaResponseDTO converterParaDTO(Reserva reserva) {
        return new ReservaResponseDTO(reserva);
    }

    private void validarDatas(LocalDate dataEntrada, LocalDate dataSaida) {
        if (dataEntrada == null || dataSaida == null) {
            throw new IllegalArgumentException("As datas de entrada e saída são obrigatórias.");
        }
        if (!dataSaida.isAfter(dataEntrada)) {
            throw new IllegalArgumentException("A data de saída deve ser depois da data de entrada.");
        }
    }

    private void verificarDisponibilidade(
            Long quartoId,
            LocalDate novaEntrada,
            LocalDate novaSaida) {

        List<StatusReserva> statusIgnorado = List.of(
                StatusReserva.CANCELADA,
                StatusReserva.NO_SHOW
        );

        long conflitos = reservaRepository.contarReservasConflitantes(
                quartoId, novaEntrada, novaSaida, statusIgnorado
        );

        if (conflitos > 0) {
            throw new ReservaConflitanteException(
                    "O quarto possui uma reserva para esse período."
            );
        }
    }
}