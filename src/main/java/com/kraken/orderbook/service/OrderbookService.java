package com.kraken.orderbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.models.OrderBook;
import com.kraken.orderbook.entity.OrderEntity;
import com.kraken.orderbook.exception.OrderbookNotFoundException;
import com.kraken.orderbook.exception.OrderbookProcessingException;
import com.kraken.orderbook.mapper.OrderbookMapper;
import com.kraken.orderbook.messaging.ReactiveKafkaSender;
import com.kraken.orderbook.model.OrderBookDto;
import com.kraken.orderbook.model.OrderBookMessage;
import com.kraken.orderbook.model.PriceDataDto;
import com.kraken.orderbook.model.enums.OrderType;
import com.kraken.orderbook.repository.OrderBookRepository;
import com.kraken.orderbook.util.OrderbookDataLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kraken.orderbook.util.ErrorMessages.ORDERBOOK_PROCESSING_ERROR_MSG;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderbookService {

    private final OrderBookRepository orderBookRepository;

    private final ObjectMapper objectMapper;

    private final OrderbookMapper orderbookMapper;

    private final OrderbookDataLogger orderbookDataLogger;

    private final ReactiveKafkaSender reactiveKafkaSender;

    private static final String ASK_ORDER_KEY = "a";

    private static final String BID_ORDER_KEY = "b";

    public static final String KAFKA_TOPIC = "orderbook";


    public void processKrakenMessage(String message) {
        try {
            if (message.contains("\"as\"") || message.contains("\"bs\"")) {
                handleSnapshot(message);
            } else if (message.contains("\"a\"") || message.contains("\"b\"")) {
                handleUpdate(message);
            }
        } catch (OrderbookProcessingException e) {
            throw new OrderbookProcessingException(ORDERBOOK_PROCESSING_ERROR_MSG, e);
        }
    }

    public OrderBook getOrderBook(String currencyPair) {
        return orderBookRepository.findById(currencyPair)
                .map(orderbookMapper::mapOrderBookEntityToDto)
                .orElseThrow(() -> new OrderbookNotFoundException(currencyPair));
    }

    private void handleSnapshot(String message) {
        try {
            Object[] rawMessage = objectMapper.readValue(message, Object[].class);

            var orderBookMessage = new OrderBookMessage();

            var timestamp = Long.parseLong(rawMessage[0].toString());
            var currencyPair = rawMessage[3].toString();
            var orderBook = parseOrderBook(rawMessage[1].toString());

            orderBookMessage.setTimestamp(timestamp);
            orderBookMessage.setOrderBook(orderBook);
            orderBookMessage.setPair(currencyPair);

            var orderBookEntity = orderbookMapper.mapOrderBookDtoToEntity(orderBookMessage);
            orderBookRepository.save(orderBookEntity);
            orderbookDataLogger.logOrderBook(orderBookEntity);
            reactiveKafkaSender.send(KAFKA_TOPIC, currencyPair, orderBookEntity.toString());
        } catch (OrderbookProcessingException | JsonProcessingException e) {
            throw new OrderbookProcessingException(ORDERBOOK_PROCESSING_ERROR_MSG, e);
        }
    }

    private void handleUpdate(String message) {
        try {
            var rawMessage = objectMapper.readValue(message, Object[].class);
            var currencyPair = rawMessage[3].toString();
            var orderBook = orderBookRepository.findById(currencyPair)
                    .orElseThrow(() -> new OrderbookNotFoundException(currencyPair));

            if (message.contains(ASK_ORDER_KEY)) {
                updateOrders(message, orderBook.getAsks(), OrderType.ASK);
            }
            if (message.contains(BID_ORDER_KEY)) {
                updateOrders(message, orderBook.getBids(), OrderType.BID);
            }

            orderBookRepository.save(orderBook);
            orderbookDataLogger.logOrderBook(orderBook);
            reactiveKafkaSender.send(KAFKA_TOPIC, currencyPair, orderBook.toString());
        } catch (OrderbookProcessingException | JsonProcessingException e) {
            throw new OrderbookProcessingException(ORDERBOOK_PROCESSING_ERROR_MSG, e);
        }
    }

    private void updateOrders(String message, TreeMap<BigDecimal, OrderEntity> orderMap, OrderType orderType) {
        var updateEntities = parseOrdersFromMessage(message, orderType);
        for (OrderEntity updateEntity : updateEntities) {
            var price = updateEntity.getPrice();
            var volume = updateEntity.getVolume();

            if (volume.compareTo(BigDecimal.ZERO) == 0) {
                orderMap.remove(price);
            } else {
                orderMap.put(price, updateEntity);
            }
        }
    }

    private List<OrderEntity> parseOrdersFromMessage(String message, OrderType orderType) {
        List<OrderEntity> orders = new ArrayList<>();
        var pattern = String.format("\"%s\":\\[\\[(.*?)\\]\\]", orderType.equals(OrderType.ASK) ? ASK_ORDER_KEY : BID_ORDER_KEY);
        var matcher = Pattern.compile(pattern).matcher(message);

        while (matcher.find()) {
            var ordersString = matcher.group(1);
            var orderParts = ordersString.split("],\\s*\\[");

            for (String orderPart : orderParts) {
                String[] parts = orderPart.replace("[", "").replace("]", "").split(",");

                var price = new BigDecimal(parts[0].trim().replace("\"", ""));
                var volume = new BigDecimal(parts[1].trim().replace("\"", ""));
                var dateTime = orderbookMapper.mapEpochToLocalDateTime(Double.parseDouble(parts[2].trim().replace("\"", "")));
                orders.add(new OrderEntity(price, volume, dateTime));
            }
        }
        return orders;
    }

    private OrderBookDto parseOrderBook(String data) {
        List<PriceDataDto> asList = new ArrayList<>();
        List<PriceDataDto> bsList = new ArrayList<>();

        Pattern pattern = Pattern.compile("as=\\[\\[(.*?)\\]\\], bs=\\[\\[(.*?)\\]\\]");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            String asData = "[" + matcher.group(1) + "]";
            String bsData = "[" + matcher.group(2) + "]";

            asList = parsePriceData(asData);
            bsList = parsePriceData(bsData);
        }

        return new OrderBookDto(asList, bsList);
    }

    private List<PriceDataDto> parsePriceData(String rawData) {
        List<PriceDataDto> list = new ArrayList<>();
        Pattern itemPattern = Pattern.compile("(?<=\\[|\\,\\[)\\s*([^,\\[\\]]+),\\s*([^,\\[\\]]+),\\s*([^,\\[\\]]+)(?=\\])");
        Matcher itemMatcher = itemPattern.matcher(rawData);

        while (itemMatcher.find()) {
            System.out.println("Found: " + itemMatcher.group(0));
            double price = Double.parseDouble(itemMatcher.group(1).trim());
            double quantity = Double.parseDouble(itemMatcher.group(2).trim());
            double timestamp = Double.parseDouble(itemMatcher.group(3).trim());

            list.add(new PriceDataDto(price, quantity, timestamp));
        }
        return list;
    }
}
