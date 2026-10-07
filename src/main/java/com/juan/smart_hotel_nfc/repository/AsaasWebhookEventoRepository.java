package com.juan.smart_hotel_nfc.repository;

import com.juan.smart_hotel_nfc.model.AsaasWebhookEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsaasWebhookEventoRepository extends JpaRepository<AsaasWebhookEvento, Long> {

    boolean existsByEventId(String eventId);
}
