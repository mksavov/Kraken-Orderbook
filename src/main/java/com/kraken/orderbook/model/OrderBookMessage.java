package com.kraken.orderbook.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderBookMessage {
    private long timestamp;
    private OrderBookDto orderBook;
    private String pair;
}
