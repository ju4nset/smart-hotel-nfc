package com.juan.smart_hotel_nfc.repository;

import com.juan.smart_hotel_nfc.model.Quarto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    // Esse métooo vai ser essencial lá na frente para o check-in!
    // Ele busca o quarto diretamente pelo número da porta (Ex: "101")
    Optional<Quarto> findByNumero(String numero);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Quarto q WHERE q.id =:id")
    Optional<Quarto> buscarQuartoParaReserva(@Param("id")Long id);


}
