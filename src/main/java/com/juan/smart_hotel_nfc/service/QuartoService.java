package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.QuartoResponseDTO;
import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;


    //Injeção de dependencias: o Service chama o repository
    public QuartoService(QuartoRepository quartoRepository) {
        this.quartoRepository = quartoRepository;
    }

    public List ListarTodosOsQuartos(){
        //Buscar todoas as entidades no banco de dados
        List<Quarto> quartos = quartoRepository.findAll();

        // 2 Transformar cada entidade em um DTO de resposta usando Streams do java
        return quartos.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // METODO AUXILIAR PRIVADO : O TRADUTOR DE ENTIDADES PARA DTO

    private QuartoResponseDTO converterParaDTO(Quarto quarto){
        return new QuartoResponseDTO(
                quarto.getNumero(),
                quarto.getCategoria(),
                quarto.getStatusQuarto()
        );
    }








}
