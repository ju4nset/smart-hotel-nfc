package com.juan.smart_hotel_nfc.repository;

import com.juan.smart_hotel_nfc.model.Reserva;
import com.juan.smart_hotel_nfc.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    //Busca todas as reservas cadastradas para determinado quarto
    List<Reserva> findByQuarto_Id(Long quartoId);


    //UMA ANOTAÇÃO DO SPRING DATA PJA QUE PERMITE ESCREVER UMA CONSULTA PERSONALIZADA.
    @Query("""
        SELECT COUNT(r)  
        FROM Reserva r 
        WHERE r.quarto.id = :quartoId 
        AND r.dataEntrada < :saida
        AND r.dataSaida > :entrada 
        AND r.statusReserva NOT IN :statusIgnorado     
    """)

    long contarReservasConflitantes(
            @Param("quartoId") Long quartoId,
            @Param("entrada") LocalDate entrada,
            @Param("sauda") LocalDate saida,
            @Param("statusIgnorado") List<StatusReserva> statusIgnorado
    );

}
