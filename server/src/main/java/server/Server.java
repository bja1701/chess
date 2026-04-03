package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import model.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import io.javalin.http.Context;
import server.websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to start database", e);
        }
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        this.webSocketHandler = new WebSocketHandler(dataAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.get("/game", this::listGamesHandler);
        javalin.post("/game", this::createGameHandler);
        javalin.put("/game", this::joinGameHandler);
        javalin.delete("/db", this::clearHandler);
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler::onConnect);
            ws.onMessage(webSocketHandler::onMessage);
            ws.onClose(webSocketHandler::onClose);
            ws.onError(webSocketHandler::onError);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerHandler(Context ctx) {
        try {
            Gson gson = new Gson();
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void loginHandler(Context ctx) {
        try {
            Gson gson = new Gson();
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void logoutHandler(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void listGamesHandler(Context ctx) {
        try {
            Gson gson = new Gson();
            String authToken = ctx.header("authorization");
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = gameService.listGames(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void createGameHandler(Context ctx) {
        try {
            Gson gson = new Gson();
            String authToken = ctx.header("authorization");
            CreateGameRequest jsonRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameRequest finalRequest = new CreateGameRequest(authToken, jsonRequest.gameName());
            CreateGameResult result = gameService.createGame(finalRequest);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void joinGameHandler(Context ctx) {
        try {
            Gson gson = new Gson();
            String authToken = ctx.header("authorization");
            JoinGameRequest jsonRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameRequest finalRequest = new JoinGameRequest(authToken, jsonRequest.playerColor(), jsonRequest.gameID());
            gameService.joinGame(finalRequest);
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void clearHandler(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException error) {
            handleError(error, ctx);
        }
    }

    private void handleError(DataAccessException error, Context ctx) {
        switch (error.getMessage()) {
            case "Error: bad request" -> ctx.status(400);
            case "Error: unauthorized" -> ctx.status(401);
            case "Error: already taken" -> ctx.status(403);
            default -> ctx.status(500);
        }
        String message = error.getMessage();
        if (!message.startsWith("Error:")) {
            message = "Error: " + message;
        }
        ctx.result("{ \"message\": \"" + message + "\" }");
    }
}