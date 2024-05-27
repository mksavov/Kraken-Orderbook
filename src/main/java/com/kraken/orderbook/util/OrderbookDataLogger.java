package com.kraken.orderbook.util;

import com.kraken.orderbook.entity.OrderBookEntity;
import com.kraken.orderbook.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

@Component
public class OrderbookDataLogger {
    public void logOrderBook(OrderBookEntity orderBook) {
        System.out.println("--------------------------------------------------");
        System.out.println("OrderBook Updated for " + orderBook.getCurrencyPair() + " at " + LocalDateTime.now());
        System.out.println("--------------------------------------------------");

        System.out.println("All Asks:");
        printEntries(orderBook.getAsks());

        System.out.println("All Bids:");
        printEntries(orderBook.getBids());

        highlightBestOrders(orderBook);
        System.out.println("--------------------------------------------------\n");
    }

    private void printEntries(TreeMap<BigDecimal, OrderEntity> orderMap) {
        if (orderMap != null && !orderMap.isEmpty()) {
            orderMap.forEach((price, order) -> {
                System.out.println(formatOrderEntry(price, order));
            });
        } else {
            System.out.println("None");
        }
    }

    private void highlightBestOrders(OrderBookEntity orderBook) {
        if (orderBook.getAsks() != null && !orderBook.getAsks().isEmpty()) {
            var bestAskEntry = orderBook.getAsks().firstEntry();
            System.out.println("Best Ask: " + formatOrderEntry(bestAskEntry.getKey(), bestAskEntry.getValue()));
        } else {
            System.out.println("Best Ask: None");
        }

        if (orderBook.getBids() != null && !orderBook.getBids().isEmpty()) {
            var bestBidEntry = orderBook.getBids().firstEntry();
            System.out.println("Best Bid: " + formatOrderEntry(bestBidEntry.getKey(), bestBidEntry.getValue()));
        } else {
            System.out.println("Best Bid: None");
        }
    }

    private String formatOrderEntry(BigDecimal price, OrderEntity order) {
        return String.format("Price: %,.2f, Volume: %,.2f, Timestamp: %s",
                price, order.getVolume(), order.getTimestamp());
    }
}
