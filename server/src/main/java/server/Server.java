package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import model.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.
        javalin.post("/user", ctx -> {
            try {
                Gson gson = new Gson();
                String jsonBody = ctx.body();
                RegisterRequest request = gson.fromJson(jsonBody, RegisterRequest.class);
                RegisterResult result = userService.register(request);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (DataAccessException error) {
                handleError(error, ctx);
            }
        });

        javalin.post("/session", ctx -> {
            try {
                Gson gson = new Gson();
                String jsonBody = ctx.body();
                LoginRequest request = gson.fromJson(jsonBody, LoginRequest.class);
                LoginResult result = userService.login(request);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            }  catch (DataAccessException error) {
                handleError(error, ctx);
            }
        });

        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                LogoutRequest request = new LogoutRequest(authToken);
                userService.logout(request);
                ctx.status(200);
                ctx.result("{}");
            } catch (DataAccessException error) {
                handleError(error, ctx);
            }
        });

        javalin.get("/game", ctx -> {
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
        });

        javalin.post("/game", ctx -> {
            try {
                Gson gson = new Gson();
                String authToken = ctx.header("authorization");
                String jsonBody = ctx.body();
                CreateGameRequest jsonRequest = gson.fromJson(jsonBody, CreateGameRequest.class);
                CreateGameRequest finalRequest = new CreateGameRequest(authToken, jsonRequest.gameName());
                CreateGameResult result = gameService.createGame(finalRequest);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (DataAccessException error) {
                handleError(error, ctx);
            }
        });

        javalin.put("/game", ctx -> {
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
        });

        javalin.delete("/db", ctx -> {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void handleError(DataAccessException error, Context ctx) {
        switch (error.getMessage()) {
            case "Error: bad request" -> ctx.status(400);
            case "Error: unauthorized" -> ctx.status(401);
            case "Error: already taken" -> ctx.status(403);
            default -> ctx.status(500);
        }
        ctx.result("{ \"message\": \"" + error.getMessage() + "\" }");
    }
}
