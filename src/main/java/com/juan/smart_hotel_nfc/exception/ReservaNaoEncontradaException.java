package com.juan.smart_hotel_nfc.exception;

public class ReservaNaoEncontradaException extends RuntimeException{

    public ReservaNaoEncontradaException(Long reservaId){
        super("Reserva não encontrada com o ID: " + reservaId);

    }

}
