
package com.juan.smart_hotel_nfc.dto;

public record CheckoutReservaResponseDTO(
        Long reservaId,
        String paymentId,
        String invoiceUrl
) {
}
