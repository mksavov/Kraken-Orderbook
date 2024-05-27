package com.kraken.orderbook.exception;

public class OrderbookNotFoundException extends RuntimeException {
    public OrderbookNotFoundException(String currencyPair) {
        super("Orderbook with currency pair - " + currencyPair + " not found");
    }
}
