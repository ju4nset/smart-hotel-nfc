package com.juan.smart_hotel_nfc.service;

import com.juan.smart_hotel_nfc.dto.CategoriaQuartoResponseDTO;
import com.juan.smart_hotel_nfc.dto.CategoriaQuartoRequestDTO;
import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import com.juan.smart_hotel_nfc.repository.CategoriaQuartoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaQuartoService {

    private final CategoriaQuartoRepository categoriaQuartoRepository;

    public CategoriaQuartoService(CategoriaQuartoRepository categoriaQuartoRepository) {
        this.categoriaQuartoRepository = categoriaQuartoRepository;
    }

    public CategoriaQuartoResponseDTO criarCategoria(CategoriaQuartoRequestDTO request) {

        if(categoriaQuartoRepository.findByNome(request.nome()).isPresent()){
            throw new IllegalArgumentException("Ja existe uma categoria com esse nome");

        }

        CategoriaQuarto categoria = new CategoriaQuarto();
        categoria.setNome(request.nome());
        categoria.setCapacidadeMaxima(request.capacidadeMaxima());
        categoria.setCapacidadeAdultos(request.capacidadeAdultos());
        categoria.setCapacidadeCriancas(request.capacidadeCriancas());
        categoria.setValorBase(request.valorBase());
        categoria.setDescricao(request.descricao());
        categoria.setValorBase(request.valorBase());

        CategoriaQuarto categoriaSalva = categoriaQuartoRepository.save(categoria);

        return new CategoriaQuartoResponseDTO(
                categoriaSalva.getId(),
                categoriaSalva.getNome(),
                categoriaSalva.getCapacidadeMaxima(),
                categoriaSalva.getCapacidadeAdultos(),
                categoriaSalva.getCapacidadeCriancas(),
                categoriaSalva.getValorBase(),
                categoriaSalva.getDescricao(),
                categoriaSalva.getAdicionalHospede()
        );
    }

    public List<CategoriaQuartoResponseDTO> listarCategorias(){
        return categoriaQuartoRepository.findAll().stream().map(categoria -> new CategoriaQuartoResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getCapacidadeMaxima(),
                categoria.getCapacidadeAdultos(),
                categoria.getCapacidadeCriancas(),
                categoria.getValorBase(),
                categoria.getDescricao(),
                categoria.getAdicionalHospede()

        )).toList();

    }



}
