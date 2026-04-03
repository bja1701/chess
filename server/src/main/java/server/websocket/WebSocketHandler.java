package server.websocket;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    public void onConnect(WsConnectContext ctx) {
        System.out.println("WebSocket Connected: " + ctx.sessionId());
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        System.out.println("Received message: " + message);
    }

    public void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket Disconnected: " + ctx.sessionId());
    }

    public void onError(WsErrorContext ctx) {
        System.out.println("WebSocket Error: " + ctx.error());
    }
}
