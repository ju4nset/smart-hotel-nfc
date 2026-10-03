package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.HospedeRequestDTO;
import com.juan.smart_hotel_nfc.dto.HospedeResponseDTO;
import com.juan.smart_hotel_nfc.model.Hospede;
import com.juan.smart_hotel_nfc.repository.HospedeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HospedeService {


    public HospedeService() {}

    @Autowired
    private HospedeRepository hospedeRepository;

    // 1º - REGRA, CADASTRAR UM HOSPEDE
    public HospedeResponseDTO cadastrarHospede(HospedeRequestDTO hospedeRequest){
        // CONVERTER OS DADOS RECEBIDOS
        Hospede hospedeNovo = new Hospede();
        hospedeNovo.setNome(hospedeRequest.getNome());
        hospedeNovo.setCpf(hospedeRequest.getCpf());
        hospedeNovo.setTelefone(hospedeRequest.getTelefone());


        // 2 O REPOSITORY VAI SALVAR NO BANCO DE DADOS
        Hospede hospedeSalvo = hospedeRepository.save(hospedeNovo);

        // PASSO 3: O "tradutor" pega a Entidade salva e converte para o ResponseDTO (O recibo)
        HospedeResponseDTO hospedeResponseDTO = new HospedeResponseDTO();
        hospedeResponseDTO.setCpf(hospedeSalvo.getCpf());
        hospedeResponseDTO.setNome(hospedeSalvo.getNome());
        hospedeResponseDTO.setId(hospedeSalvo.getId());

        // PASSO 4: Devolve o recibo pronto para o Controller
        return hospedeResponseDTO;
    }

    // 2º - LISTAR TODOS OS HOSPEDES
    public List<HospedeResponseDTO> listarHospedes(){
        List<Hospede> hospedeListBanco = hospedeRepository.findAll();
        // 2. Converte a lista de Entities para uma lista de DTOs
        // O '.stream().map()' é um loop moderno do Java que passa por cada hóspede da lista fazendo a conversão

        return hospedeListBanco.stream().map(hospede -> {
            HospedeResponseDTO dto = new HospedeResponseDTO();
            dto.setCpf(hospede.getCpf());
            dto.setNome(hospede.getNome());
            dto.setId(hospede.getId());
            return dto;
        }).toList();

    }


    // Regra 3 buscar um hospede por id
    public Hospede buscarHospedePorID(long id ){
        return hospedeRepository.findById(id).orElseThrow(()-> new NoSuchElementException
                //orElseThrow diz que se tiver vazio, ele vai laçar essa exeção ao invez de devolver um null
                ("Hospede não encontrado com o ID." + id));
    }







}
