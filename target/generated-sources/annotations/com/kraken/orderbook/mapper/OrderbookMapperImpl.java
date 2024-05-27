package com.kraken.orderbook.mapper;

import com.kraken.models.Order;
import com.kraken.models.OrderBook;
import com.kraken.orderbook.entity.OrderBookEntity;
import com.kraken.orderbook.entity.OrderEntity;
import com.kraken.orderbook.model.OrderBookDto;
import com.kraken.orderbook.model.OrderBookMessage;
import com.kraken.orderbook.model.PriceDataDto;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-22T15:56:19+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Homebrew)"
)
@Component
public class OrderbookMapperImpl implements OrderbookMapper {

    @Override
    public OrderBook mapOrderBookEntityToDto(OrderBookEntity orderBookEntity) {
        if ( orderBookEntity == null ) {
            return null;
        }

        OrderBook orderBook = new OrderBook();

        orderBook.setBids( mapTreeMapToOrderList( orderBookEntity.getBids() ) );
        orderBook.setAsks( mapTreeMapToOrderList( orderBookEntity.getAsks() ) );

        return orderBook;
    }

    @Override
    public OrderBookEntity mapOrderBookDtoToEntity(OrderBookMessage orderBookMessage) {
        if ( orderBookMessage == null ) {
            return null;
        }

        OrderBookEntity orderBookEntity = new OrderBookEntity();

        orderBookEntity.setCurrencyPair( orderBookMessage.getPair() );
        List<PriceDataDto> as = orderBookMessageOrderBookAs( orderBookMessage );
        orderBookEntity.setAsks( listToTreeMapAsks( as ) );
        List<PriceDataDto> bs = orderBookMessageOrderBookBs( orderBookMessage );
        orderBookEntity.setBids( listToTreeMapBids( bs ) );

        return orderBookEntity;
    }

    @Override
    public OrderEntity priceDataDtoToOrderEntity(PriceDataDto dto) {
        if ( dto == null ) {
            return null;
        }

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setPrice( BigDecimal.valueOf( dto.getPrice() ) );
        orderEntity.setVolume( BigDecimal.valueOf( dto.getQuantity() ) );
        orderEntity.setTimestamp( mapEpochToLocalDateTime( dto.getTimestamp() ) );

        return orderEntity;
    }

    @Override
    public Order mapOrderEntityToOrder(OrderEntity value) {
        if ( value == null ) {
            return null;
        }

        Order order = new Order();

        if ( value.getPrice() != null ) {
            order.setPrice( value.getPrice().doubleValue() );
        }
        if ( value.getVolume() != null ) {
            order.setVolume( value.getVolume().doubleValue() );
        }

        return order;
    }

    private List<PriceDataDto> orderBookMessageOrderBookAs(OrderBookMessage orderBookMessage) {
        if ( orderBookMessage == null ) {
            return null;
        }
        OrderBookDto orderBook = orderBookMessage.getOrderBook();
        if ( orderBook == null ) {
            return null;
        }
        List<PriceDataDto> as = orderBook.getAs();
        if ( as == null ) {
            return null;
        }
        return as;
    }

    private List<PriceDataDto> orderBookMessageOrderBookBs(OrderBookMessage orderBookMessage) {
        if ( orderBookMessage == null ) {
            return null;
        }
        OrderBookDto orderBook = orderBookMessage.getOrderBook();
        if ( orderBook == null ) {
            return null;
        }
        List<PriceDataDto> bs = orderBook.getBs();
        if ( bs == null ) {
            return null;
        }
        return bs;
    }
}
