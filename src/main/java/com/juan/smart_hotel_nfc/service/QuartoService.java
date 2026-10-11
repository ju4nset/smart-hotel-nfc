package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.QuartoRequestDTO;
import com.juan.smart_hotel_nfc.dto.QuartoResponseDTO;
import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.model.StatusQuarto;
import com.juan.smart_hotel_nfc.model.StatusReserva;
import com.juan.smart_hotel_nfc.repository.CategoriaQuartoRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import com.juan.smart_hotel_nfc.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final CategoriaQuartoRepository categoriaQuartoRepository;
    private final ReservaRepository reservaRepository;

    public QuartoService(
            QuartoRepository quartoRepository,
            CategoriaQuartoRepository categoriaQuartoRepository, ReservaRepository reservaRepository) {

        this.quartoRepository = quartoRepository;
        this.categoriaQuartoRepository = categoriaQuartoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<QuartoResponseDTO> listarTodosOsQuartos() {

        List<Quarto> quartos = quartoRepository.findAll();

        return quartos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public QuartoResponseDTO criarQuarto(QuartoRequestDTO request) {

        CategoriaQuarto categoria =
                categoriaQuartoRepository.findById(request.categoriaId())
                        .orElseThrow(() ->
                                new NoSuchElementException("Categoria não encontrada.")
                        );

        Quarto quarto = new Quarto();

        quarto.setNumero(request.numero());
        quarto.setCategoria(categoria);
        quarto.setStatusQuarto(StatusQuarto.ATIVO);

        Quarto quartoSalvo = quartoRepository.save(quarto);

        return converterParaDTO(quartoSalvo);
    }

    private QuartoResponseDTO converterParaDTO(Quarto quarto) {

        return new QuartoResponseDTO(
                quarto.getId(),
                quarto.getNumero(),
                quarto.getCategoria().getId(),
                quarto.getCategoria().getNome(),
                quarto.getStatusQuarto()
        );
    }

    public List<QuartoResponseDTO> buscarQuartosDisponiveis(LocalDate entrada, LocalDate saida, int hospedes){
        // VALIDAR AS DATAS
        if(entrada == null || saida == null ||
            !saida.isAfter(entrada)){
                throw  new IllegalArgumentException("A data de saída deve ser posterior a data de entrada");
        }
        if(hospedes <= 0){
            throw new IllegalArgumentException("A quantidade de hospede deve ser superior a zero");
        }
        // BUSCAR TODAS AS RESERVAS
        List<Quarto> quartos = quartoRepository.findAll();

        //STATUS DE RESERVA QUE NAO BLOQUEIAM QUARTOS
        List<StatusReserva> statusIgnorado = List.of(StatusReserva.CANCELADA, StatusReserva.NO_SHOW);

        return quartos.stream().filter(quarto -> quarto.getStatusQuarto() == StatusQuarto.ATIVO).
                filter(quarto -> quarto.getCategoria().getCapacidadeMaxima() >= hospedes).
                filter(quarto -> reservaRepository.contarReservasConflitantes(quarto.getId(), entrada, saida
                ,statusIgnorado) == 0).map(
                this::converterParaDTO).toList();

    }


}