package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void onConnect(WsConnectContext ctx) {
        System.out.println("WebSocket Connected: " + ctx.sessionId());
    }

    public void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket Disconnected: " + ctx.sessionId());
    }

    public void onError(WsErrorContext ctx) {
        System.out.println("WebSocket Error: " + ctx.error());
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();
            AuthData auth = dataAccess.getAuthToken(authToken);
            if (auth == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
                return;
            }
            GameData game = dataAccess.getGame(gameID);
            if (game == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: bad game ID")));
                return;
            }
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx, authToken, gameID, auth, game);
                case MAKE_MOVE -> makeMove(ctx, message, authToken, gameID, auth, game);
                case LEAVE -> leave(ctx, authToken, gameID, auth, game);
                case RESIGN -> resign(ctx, message, authToken, gameID, auth, game);
            }
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void connect(WsMessageContext ctx, String authToken, Integer gameID, AuthData auth, GameData game) {
        try {
            connections.add(gameID, authToken, ctx);
            LoadGameMessage loadMessage = new LoadGameMessage(game.game());
            ctx.send(new Gson().toJson(loadMessage));
            NotificationMessage notification = new NotificationMessage(auth.username() + " joined the game.");
            connections.broadcast(gameID, authToken, notification);
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void makeMove(WsMessageContext ctx, String message, String authToken, Integer gameID, AuthData auth, GameData game) {
        try {
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            chess.ChessMove move = command.getMove();
            String username = auth.username();
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            chess.ChessGame.TeamColor playerColor = null;
            if (username.equals(whiteUser)) {
                playerColor = chess.ChessGame.TeamColor.WHITE;
            } else if (username.equals(blackUser)) {
                playerColor = chess.ChessGame.TeamColor.BLACK;
            }
            if (playerColor == null) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: observers cannot make moves")));
                return;
            }
            if (game.game().isGameOver()) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: game is already over")));
                return;
            }
            if (game.game().getTeamTurn() != playerColor) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: not your turn")));
                return;
            }
            try {
                game.game().makeMove(move);
            } catch (chess.InvalidMoveException e) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: invalid move")));
                return;
            }
            chess.ChessGame.TeamColor opponentColor = (playerColor == chess.ChessGame.TeamColor.WHITE) ?
                    chess.ChessGame.TeamColor.BLACK : chess.ChessGame.TeamColor.WHITE;
            String opponentName = (opponentColor == chess.ChessGame.TeamColor.WHITE) ? whiteUser : blackUser;
            boolean checkmate = game.game().isInCheckmate(opponentColor);
            boolean stalemate = game.game().isInStalemate(opponentColor);
            if (checkmate || stalemate) {
                game.game().setGameOver(true);
            }
            GameData updatedGame = new GameData(game.gameID(), whiteUser, blackUser, game.gameName(), game.game());
            dataAccess.updateGame(updatedGame);
            LoadGameMessage loadMessage = new LoadGameMessage(game.game());
            connections.broadcast(gameID, "", loadMessage);
            NotificationMessage moveNotification = new NotificationMessage(username + " made a move.");
            connections.broadcast(gameID, authToken, moveNotification);
            if (checkmate) {
                connections.broadcast(gameID, "", new NotificationMessage(opponentName + " is in checkmate."));
            } else if (stalemate) {
                connections.broadcast(gameID, "", new NotificationMessage(opponentName + " is in stalemate."));
            } else if (game.game().isInCheck(opponentColor)) {
                connections.broadcast(gameID, "", new NotificationMessage(opponentName + " is in check."));
            }
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void leave(WsMessageContext ctx, String authToken, Integer gameID, AuthData auth, GameData game) {
        try {
            String username = auth.username();
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            if (username.equals(whiteUser)) {
                dataAccess.updateGame(new GameData(game.gameID(), null, blackUser, game.gameName(), game.game()));
            } else if (username.equals(blackUser)) {
                dataAccess.updateGame(new GameData(game.gameID(), whiteUser, null, game.gameName(), game.game()));
            }
            connections.remove(gameID, authToken);
            NotificationMessage notification = new NotificationMessage(username + " has left the game.");
            connections.broadcast(gameID, authToken, notification);
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void resign(WsMessageContext ctx, String message, String authToken, Integer gameID, AuthData auth, GameData game) {
        try {
            String username = auth.username();
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            if (!username.equals(whiteUser) && !username.equals(blackUser)) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: observers cannot resign")));
                return;
            }
            if (game.game().isGameOver()) {
                ctx.send(new Gson().toJson(new ErrorMessage("Error: game is already over")));
                return;
            }
            game.game().setGameOver(true);
            dataAccess.updateGame(new GameData(game.gameID(), whiteUser, blackUser, game.gameName(), game.game()));
            NotificationMessage notification = new NotificationMessage(username + " has resigned.");
            connections.broadcast(gameID, "", notification);
        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

}