package com.kraken.orderbook.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RedisHash("Order")
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class OrderEntity implements Serializable {
    private BigDecimal price;

    private BigDecimal volume;

    private LocalDateTime timestamp;

}
