package com.juan.smart_hotel_nfc.controller;

import com.juan.smart_hotel_nfc.dto.QuartoResponseDTO;
import com.juan.smart_hotel_nfc.dto.ReservaRequestDTO;
import com.juan.smart_hotel_nfc.dto.ReservaResponseDTO;
import com.juan.smart_hotel_nfc.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

    private final ReservaService reservaService;


    public ReservaController(ReservaService reservaService){
        this.reservaService = reservaService;
    }

    // END POINT para cadastrar uma reserva
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> cadastrarReserva(@Valid @RequestBody ReservaRequestDTO reservaRequestDTO){
        //@ Valid liga as travas de segurança
        ReservaResponseDTO salvarReserva = reservaService.cadastraReserva(reservaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvarReserva); // retorna o status ok com a lista de quartos
    }

    @GetMapping
    public ResponseEntity <List<ReservaResponseDTO>> listarReservas(){
        List <ReservaResponseDTO> reservas = reservaService.ListarTodasAsReservas();
        return ResponseEntity.ok(reservas);
    }



}
