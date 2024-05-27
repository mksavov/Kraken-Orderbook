package com.kraken.orderbook.exception;

public class OrderbookProcessingException extends RuntimeException {
    public OrderbookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
