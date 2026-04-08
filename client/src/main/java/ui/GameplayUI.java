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
    private WebSocketFacade ws;
    private ChessGame game;

    public GameplayUI(String serverUrl, String authToken, int gameID, ChessGame.TeamColor playerColor) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        try {
            ws = new WebSocketFacade(serverUrl, this);
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
            case "leave" -> leave();
            case "resign" -> resign();
            case "move" -> makeMove(tokens);
            default -> "Unknown command\n";
        };
    }

    private String leave() {
        try {
            ws.leave(authToken, gameID);
            return "Left the game";
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    private String resign() {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            try {
                ws.resign(authToken, gameID);
                return "Resignation sent.\n";
            } catch (Exception e) {
                return e.getMessage() + "\n";
            }
        }
        return "Resignation cancelled.\n";
    }

    private String makeMove(String[] tokens) {
        if (tokens.length < 3) {
            return "Usage: move <start> <end> [promotion]\n";
        }
        try {
            String startStr = tokens[1].toLowerCase();
            String endStr = tokens[2].toLowerCase();
            if (startStr.length() != 2 || endStr.length() != 2) {
                return "Invalid position format. Use format like 'e2'.\n";
            }
            int startCol = startStr.charAt(0) - 'a' + 1;
            int startRow = startStr.charAt(1) - '0';
            int endCol = endStr.charAt(0) - 'a' + 1;
            int endRow = endStr.charAt(1) - '0';
            chess.ChessPosition start = new chess.ChessPosition(startRow, startCol);
            chess.ChessPosition end = new chess.ChessPosition(endRow, endCol);
            chess.ChessPiece.PieceType promotion = null;
            if (tokens.length > 3) {
                switch (tokens[3].toLowerCase()) {
                    case "q" -> promotion = chess.ChessPiece.PieceType.QUEEN;
                    case "r" -> promotion = chess.ChessPiece.PieceType.ROOK;
                    case "b" -> promotion = chess.ChessPiece.PieceType.BISHOP;
                    case "n" -> promotion = chess.ChessPiece.PieceType.KNIGHT;
                    default -> {
                        return "Invalid promotion piece. Use q, r, b, or n.\n";
                    }
                }
            }
            chess.ChessMove move = new chess.ChessMove(start, end, promotion);
            ws.makeMove(authToken, gameID, move);
            return "Move sent.\n";
        } catch (Exception e) {
            return "Error sending move: " + e.getMessage() + "\n";
        }
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