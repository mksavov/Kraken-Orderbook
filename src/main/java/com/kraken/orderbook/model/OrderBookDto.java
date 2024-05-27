package com.kraken.orderbook.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderBookDto {
    private List<PriceDataDto> as;
    private List<PriceDataDto> bs;
}
