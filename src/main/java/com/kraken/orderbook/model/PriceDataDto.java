package com.kraken.orderbook.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PriceDataDto {
    private double price;
    private double quantity;
    private double timestamp;
}
