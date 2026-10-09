package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.CategoriaQuartoRequestDTO;
import com.juan.smart_hotel_nfc.dto.CategoriaQuartoResponseDTO;
import com.juan.smart_hotel_nfc.service.CategoriaQuartoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaQuartoController {

    private final CategoriaQuartoService categoriaQuartoService;

            public CategoriaQuartoController(CategoriaQuartoService categoriaQuartoService){
                this.categoriaQuartoService = categoriaQuartoService;
            }

            @PostMapping
            public ResponseEntity<CategoriaQuartoResponseDTO> criarCategoria(
                @RequestBody CategoriaQuartoRequestDTO request){
                CategoriaQuartoResponseDTO response = categoriaQuartoService.criarCategoria(request);
                return ResponseEntity.ok(response);
            }

            @GetMapping
            public ResponseEntity<List<CategoriaQuartoResponseDTO>> listarCategorias(){
                return ResponseEntity.ok(categoriaQuartoService.listarCategorias());
            }


}
