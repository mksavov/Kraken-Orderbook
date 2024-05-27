package com.kraken.orderbook.websocket;

import com.kraken.orderbook.service.OrderbookService;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.kraken.orderbook.util.ErrorMessages.WEBSOCKET_CONNECTION_ERROR_MSG;

@ClientEndpoint
@Component
@RequiredArgsConstructor
public class KrakenWebSocketClient {

    private Session userSession = null;
    private final OrderbookService orderBookService;
    private static final String KRAKEN_SUBSCRIBE_REQUEST = "{\"event\":\"subscribe\", \"pair\":[\"XBT/USD\", \"ETH/USD\"], \"subscription\":{\"name\":\"book\"}}";

    private static final String KRAKEN_WS_URI = "wss://ws.kraken.com";

    @PostConstruct
    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(KRAKEN_WS_URI));
        } catch (Exception e) {
            throw new RuntimeException(WEBSOCKET_CONNECTION_ERROR_MSG, e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.userSession = session;
        System.out.println("Connected to server");
        subscribeToChannel(KRAKEN_SUBSCRIBE_REQUEST);
    }

    @OnMessage
    public void onMessage(String message) {
        orderBookService.processKrakenMessage(message);
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        System.out.println("Disconnected: " + closeReason);
        this.userSession = null;
    }

    @OnError
    public void onError(Throwable throwable) {
        System.out.println("WebSocket Error: " + throwable.getMessage());
    }

    private void subscribeToChannel(String message) {
        if (this.userSession != null) {
            this.userSession.getAsyncRemote().sendText(message);
        } else {
            System.err.println("Session is not connected. Cannot subscribe to channel.");
        }
    }
}
