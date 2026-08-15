package com.juan.smart_hotel_nfc.repository;

import com.juan.smart_hotel_nfc.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    //Busca todas as reservas cadastradas para determinado quarto
    List<Reserva> findByQuarto_Id(Long quartoId);




}
