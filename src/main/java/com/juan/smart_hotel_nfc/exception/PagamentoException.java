package com.juan.smart_hotel_nfc.exception;

public class PagamentoException extends RuntimeException{

    public PagamentoException(String message, Throwable causa){
        super(message,causa);
    }
}
