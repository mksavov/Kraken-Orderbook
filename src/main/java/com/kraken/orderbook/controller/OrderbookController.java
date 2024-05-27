package com.kraken.orderbook.controller;

import com.kraken.api.OrderbookApi;
import com.kraken.models.OrderBook;
import com.kraken.orderbook.service.OrderbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderbookController implements OrderbookApi {

    private final OrderbookService orderbookService;

    @Override
    public ResponseEntity<OrderBook> orderbookCurrentGet(String currencyPair) {
        return ResponseEntity.ok(orderbookService.getOrderBook(currencyPair));
    }
}
