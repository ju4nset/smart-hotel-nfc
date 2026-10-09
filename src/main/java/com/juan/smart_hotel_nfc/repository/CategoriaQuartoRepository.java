package com.juan.smart_hotel_nfc.repository;

import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaQuartoRepository extends JpaRepository<CategoriaQuarto, Long> {

    Optional<CategoriaQuarto> findByNome(String nome);




}
