package com.kraken.orderbook.repository;

import com.kraken.orderbook.entity.OrderBookEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderBookRepository extends CrudRepository<OrderBookEntity, String> {
}
