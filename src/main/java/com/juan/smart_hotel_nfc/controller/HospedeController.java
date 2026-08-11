package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.HospedeRequestDTO;
import com.juan.smart_hotel_nfc.dto.HospedeResponseDTO;
import com.juan.smart_hotel_nfc.model.Hospede;
import com.juan.smart_hotel_nfc.service.HospedeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospede")
public class HospedeController {


    // CHAMAR A CLASSE SERVICE
    @Autowired
    private HospedeService hospedeService;


    // QUANDO O HTTP TIVER UM POST, ELE VAI RODAR ESSE METODO
    @PostMapping
    public ResponseEntity<HospedeResponseDTO> cadastrarHospede(@Valid @RequestBody HospedeRequestDTO hospedeRequest){
        // O @Valid liga aquelas travas de segurança (@NotNull, @CPF) que colocamos no Model!
        HospedeResponseDTO savarHospede = hospedeService.cadastrarHospede(hospedeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savarHospede);
    }

    @GetMapping
    public ResponseEntity<List<HospedeResponseDTO>> buscarTodosOsHospedes(){
        return ResponseEntity.ok(hospedeService.listarHospedes());
    }





}
