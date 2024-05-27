package com.kraken.orderbook.mapper;

import com.kraken.models.Order;
import com.kraken.models.OrderBook;
import com.kraken.orderbook.config.MapConfig;
import com.kraken.orderbook.entity.OrderBookEntity;
import com.kraken.orderbook.entity.OrderEntity;
import com.kraken.orderbook.model.OrderBookMessage;
import com.kraken.orderbook.model.PriceDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Mapper(config = MapConfig.class)
public interface OrderbookMapper {

    OrderBook mapOrderBookEntityToDto(OrderBookEntity orderBookEntity);

    @Mappings({
            @Mapping(source = "pair", target = "currencyPair"),
            @Mapping(source = "orderBook.as", target = "asks", qualifiedByName = "listToTreeMapAsks"),
            @Mapping(source = "orderBook.bs", target = "bids", qualifiedByName = "listToTreeMapBids")
    })
    OrderBookEntity mapOrderBookDtoToEntity(OrderBookMessage orderBookMessage);

    @Named("listToTreeMapAsks")
    default TreeMap<BigDecimal, OrderEntity> listToTreeMapAsks(List<PriceDataDto> as) {
        return as.stream()
                .map(this::priceDataDtoToOrderEntity)
                .collect(Collectors.toMap(
                        OrderEntity::getPrice,
                        orderEntity -> orderEntity,
                        (existing, replacement) -> existing,
                        TreeMap::new
                ));
    }

    @Named("listToTreeMapBids")
    default TreeMap<BigDecimal, OrderEntity> listToTreeMapBids(List<PriceDataDto> bs) {
        return bs.stream()
                .map(this::priceDataDtoToOrderEntity)
                .collect(Collectors.toMap(
                        OrderEntity::getPrice,
                        orderEntity -> orderEntity,
                        (existing, replacement) -> existing,
                        () -> new TreeMap<>(Collections.reverseOrder())
                ));
    }

    @Mappings({
            @Mapping(source = "price", target = "price"),
            @Mapping(source = "quantity", target = "volume"),
            @Mapping(source = "timestamp", target = "timestamp")
    })
    OrderEntity priceDataDtoToOrderEntity(PriceDataDto dto);

    Order mapOrderEntityToOrder(OrderEntity value);

    default LocalDateTime mapEpochToLocalDateTime(double value) {
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond((long) value), java.time.ZoneId.systemDefault());
    }

    default List<Order> mapTreeMapToOrderList(TreeMap<BigDecimal, OrderEntity> value) {
        return value.values().stream().map(this::mapOrderEntityToOrder).collect(Collectors.toList());
    }
}
