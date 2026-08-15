package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.QuartoResponseDTO;
import com.juan.smart_hotel_nfc.service.QuartoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    private final QuartoService quartoService;

    public QuartoController (QuartoService quartoService){
       this.quartoService = quartoService;
    }

    // ENDPOINT para listar todos os quartos
    @GetMapping
    public ResponseEntity <List<QuartoResponseDTO>> ListarQuartos() {
        List <QuartoResponseDTO> quartos = quartoService.ListarTodosOsQuartos();
        return ResponseEntity.ok(quartos); //Retorna o status ok com a lista de quartos

    }
}
