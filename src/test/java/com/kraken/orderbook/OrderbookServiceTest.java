package com.kraken.orderbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.models.OrderBook;
import com.kraken.orderbook.entity.OrderBookEntity;
import com.kraken.orderbook.exception.OrderbookNotFoundException;
import com.kraken.orderbook.mapper.OrderbookMapper;
import com.kraken.orderbook.messaging.ReactiveKafkaSender;
import com.kraken.orderbook.repository.OrderBookRepository;
import com.kraken.orderbook.service.OrderbookService;
import com.kraken.orderbook.util.OrderbookDataLogger;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.kraken.orderbook.util.Constants.KRAKEN_EXAMPLE_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderbookServiceTest {

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderbookMapper orderbookMapper;

    @Mock
    private ReactiveKafkaSender reactiveKafkaSender;

    @InjectMocks
    private OrderbookService orderbookService;

    @Mock
    private OrderbookDataLogger orderbookDataLogger;

    @Test
    public void testProcessKrakenMessage_HandleSnapshot() throws Exception {
        when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(new Object[] {"162324", "[data]", "123141", "XBT/USD"});

        when(orderbookMapper.mapOrderBookDtoToEntity(any())).thenReturn(new OrderBookEntity());

        orderbookService.processKrakenMessage(KRAKEN_EXAMPLE_MESSAGE);

        verify(orderbookMapper).mapOrderBookDtoToEntity(any());
        verify(orderBookRepository).save(any());
        verify(reactiveKafkaSender).send(eq(OrderbookService.KAFKA_TOPIC), anyString(), anyString());
    }

    @Test
    public void testGetOrderBook_Found() {
        when(orderBookRepository.findById("XBT/USD")).thenReturn(Optional.of(new OrderBookEntity()));
        when(orderbookMapper.mapOrderBookEntityToDto(any())).thenReturn(new OrderBook());

        OrderBook result = orderbookService.getOrderBook("XBT/USD");

        assertNotNull(result);
        verify(orderbookMapper).mapOrderBookEntityToDto(any());
    }

    @Test
    public void testGetOrderBook_NotFound() {
        when(orderBookRepository.findById("XBT/USD")).thenReturn(Optional.empty());

        assertThrows(OrderbookNotFoundException.class, () -> orderbookService.getOrderBook("XBT/USD"));
    }
}