package com.kraken.orderbook.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

@RedisHash("OrderBook")
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class OrderBookEntity implements Serializable {
    @Id
    private String currencyPair;
    private TreeMap<BigDecimal, OrderEntity> asks;
    private TreeMap<BigDecimal, OrderEntity> bids;
}
