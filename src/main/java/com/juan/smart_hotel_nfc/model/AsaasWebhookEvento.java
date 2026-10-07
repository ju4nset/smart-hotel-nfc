package com.juan.smart_hotel_nfc.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AsaasWebhookEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique =true)
    private String eventId;

    private String eventType;

    private String paymentId;

    @Column(nullable = false)
    private LocalDateTime processadoEm;
}
