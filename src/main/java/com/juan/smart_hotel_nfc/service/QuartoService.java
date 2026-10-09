package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.QuartoRequestDTO;
import com.juan.smart_hotel_nfc.dto.QuartoResponseDTO;
import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.model.StatusQuarto;
import com.juan.smart_hotel_nfc.repository.CategoriaQuartoRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final CategoriaQuartoRepository categoriaQuartoRepository;

    public QuartoService(
            QuartoRepository quartoRepository,
            CategoriaQuartoRepository categoriaQuartoRepository) {

        this.quartoRepository = quartoRepository;
        this.categoriaQuartoRepository = categoriaQuartoRepository;
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
}