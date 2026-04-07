package ui;

import chess.ChessGame;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayUI implements ServerMessageObserver {

    private final String serverUrl;
    private final String authToken;
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private ChessGame game;

    public GameplayUI(String serverUrl, String authToken, int gameID, ChessGame.TeamColor playerColor) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        try {
            WebSocketFacade ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, gameID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "Joined game. Type 'help' for options.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Left the game")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        return switch (cmd) {
            case "help" -> help();
            case "redraw" -> redraw();
            default -> "Unknown command\n";
        };
    }

    private String help() {
        return """
                - redraw - redraw the chess board
                - leave - leave the game
                - move <start> <end> - make a move (e.g., move e2 e4)
                - resign - forfeit the game
                - highlight <position> - highlight legal moves (e.g., highlight e2)
                - help - list possible commands
                """;
    }

    private String redraw() {
        if (game != null) {
            boolean whitePerspective = (playerColor != chess.ChessGame.TeamColor.BLACK);
            new BoardDrawer().drawBoard(game.getBoard(), whitePerspective);
        }
        return "";
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.game = loadGameMessage.getGame();
                System.out.println();
                redraw();
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + EscapeSequences.RESET_TEXT_COLOR);
                printPrompt();
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + notificationMessage.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
                printPrompt();
            }
        }
    }

    private void printPrompt() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "\n[GAMEPLAY] >>> " + EscapeSequences.RESET_TEXT_COLOR);
    }
}