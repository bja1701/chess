package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, WsMessageContext>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String authToken, WsMessageContext ctx) {
        var gameConnections = connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        gameConnections.put(authToken, ctx);
    }

    public void remove(Integer gameID, String authToken) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.remove(authToken);
        }
    }

    public void broadcast(Integer gameID, String excludeAuthToken, Object message) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            String jsonMessage = new Gson().toJson(message);
            for (var entry : gameConnections.entrySet()) {
                String connectionAuth = entry.getKey();
                WsMessageContext ctx = entry.getValue();
                if (!connectionAuth.equals(excludeAuthToken)) {
                    try {
                        ctx.send(jsonMessage);
                    } catch (Exception e) {
                        System.out.println("Failed to broadcast to connection: " + e.getMessage());
                    }
                }
            }
        }
    }
}