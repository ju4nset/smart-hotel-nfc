package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.ReservaRequestDTO;
import com.juan.smart_hotel_nfc.dto.ReservaResponseDTO;
import com.juan.smart_hotel_nfc.model.*;
import com.juan.smart_hotel_nfc.repository.HospedeRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import com.juan.smart_hotel_nfc.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.juan.smart_hotel_nfc.dto.AsaasWebhookDTO;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HospedeRepository hospedeRepository;

    @Autowired
    private QuartoRepository quartoRepository;


    public ReservaResponseDTO cadastraReserva(ReservaRequestDTO reservaRequestDTO) {

        //Passando o validar Datas
        validarDatas(reservaRequestDTO .getDataEntrada(), reservaRequestDTO.getDataSaida());
            Hospede hospede = hospedeRepository.findById(reservaRequestDTO.getHospedeId()).orElseThrow(() -> new
                    NoSuchElementException("Hospede não encontrado"));


            Quarto quarto = quartoRepository.findById(reservaRequestDTO.getQuartoId()).orElseThrow(() -> new
                    NoSuchElementException("Quarto não encontrado"));



        // Verifica se o quarto está livre nesse período
        verificarDisponibilidade(quarto.getId(), reservaRequestDTO.getDataEntrada(), reservaRequestDTO.getDataSaida());

        //criar nova reserva
        Reserva novaReserva = new Reserva();

        novaReserva.setHospede(hospede);
        novaReserva.setQuarto(quarto);
        novaReserva.setValorTotal(reservaRequestDTO.getValorTotal());
        novaReserva.setDataEntrada(reservaRequestDTO.getDataEntrada());
        novaReserva.setDataSaida(reservaRequestDTO.getDataSaida());
        novaReserva.setStatusPagamento(StatusPagamento.PENDENTE);
        novaReserva.setOrigemReserva(OrigemReserva.WHATSAPP);

        Reserva reservaSalva = reservaRepository.save(novaReserva);

        return new ReservaResponseDTO(reservaSalva);
    }

    public List<ReservaResponseDTO> ListarTodasAsReservas() {
        List <Reserva> reservas = reservaRepository.findAll();
        return reservas.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // AQUI COMEÇA DO ATUALIZAR WEBHOOK

    @Transactional
    public void atualizarPagamentoWebhook(AsaasWebhookDTO asaasWebhookDTO){

        if(asaasWebhookDTO.getPayment() == null){
            System.out.println("Webhook sem pagamento.");
            return;
        }
        String externalReference = asaasWebhookDTO.getPayment().getExternalReference();

        if(externalReference == null || externalReference.isBlank()){
            System.out.println("Webhook sem externalReference.");
            return;
        }
        Long reservaId;

     try{
         reservaId = Long.parseLong(externalReference);
     }catch (NumberFormatException e){
         System.out.println("ExternalReference inválido: " + externalReference);
         return;
     }
        //Entra a busca da reserva
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow(()->
                new NoSuchElementException("Reserva não encontrada"));


    String evento = asaasWebhookDTO.getEvent();

    switch (evento){
        case "PAYMENT_RECEIVED":
            reserva.setStatusPagamento(StatusPagamento.PAGO);
            break;
        case "PAYMENT_OVERDUE ":
            reserva.setStatusPagamento(StatusPagamento.PENDENTE);
            break;
        case "PAYMENT_REFUNDED":
            reserva.setStatusPagamento(StatusPagamento.ESTORNADO);
            break;
        case "PAYMENT-DELETED":
            reserva.setStatusPagamento(StatusPagamento.CANCELADO);
    }
    reservaRepository.save(reserva);


    }





    private ReservaResponseDTO converterParaDTO(Reserva reserva) {
        return new ReservaResponseDTO(reserva);


    }

    // Confere se as datas informadas são validas
    private void validarDatas(LocalDate dataEntrada, LocalDate dataSaida){
        if(dataEntrada == null || dataSaida == null){
            throw new IllegalArgumentException("A data de entrada e saída são obrigatórias.");
        }
        if(!dataSaida.isAfter(dataEntrada)){
            throw new IllegalArgumentException("A data de saída deve ser depois da data de entrada.");
        }
    }

    private void verificarDisponibilidade(Long quartoId, LocalDate novaEntrada, LocalDate novaSaida){
        List<Reserva> reservasDoQuarto = reservaRepository.findByQuarto_Id(quartoId);

        //Significa que para cada reserva encontrada nesse quarto, faça uma verificação
        for(Reserva reserva : reservasDoQuarto){
            //Reserva Recusada não deve bloquear o quarto.
            if(reserva.getStatusPagamento() == StatusPagamento.CANCELADO){
                continue;
            }
            boolean datasSeCruzam = novaEntrada.isBefore(reserva.getDataSaida()) && novaSaida.isAfter(reserva.getDataEntrada());
            if(datasSeCruzam){
                throw new IllegalArgumentException("O quarto já possui uma reserva nesse período");
            }
        }
    }



}
