package ui;

import client.ServerFacade;
import java.util.Arrays;

public class PostloginUI {
    private final ServerFacade facade;
    private String authToken = null;

    public PostloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String createGame(String... params) throws Exception {
        if (params.length >= 1) {
            String gameName = String.join(" ", params);
            var result = facade.createGame(gameName, authToken);
            return String.format("Successfully created game '%s' with ID: %d", gameName, result.gameID());
        }
        throw new Exception("Expected: <NAME>");
    }

    public String help() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private String logout() throws Exception {
        facade.logout(authToken);
        this.authToken = null;
        return "logout";
    }

    private String listGames() throws Exception {
        var result = facade.listGames(authToken);
        var games = result.games();
        if (games.isEmpty()) {
            return "No active games found. Type 'create <NAME>' to start one!";
        }
        StringBuilder sb = new StringBuilder("Active Games:\n");
        for (var game : games) {
            sb.append(String.format("  [%d] %s\n      White: %s\n      Black: %s\n",
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "Empty",
                    game.blackUsername() != null ? game.blackUsername() : "Empty"));
        }
        return sb.toString();
    }

    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            try {
                int gameID = Integer.parseInt(params[0]);
                String color = params[1].toUpperCase();
                facade.joinGame(color, gameID, authToken);
                var board = new chess.ChessBoard();
                board.resetBoard();
                BoardDrawer drawer = new BoardDrawer();
                boolean isWhite = color.equals("WHITE");
                drawer.drawBoard(board, isWhite);
                return String.format("Successfully joined game %d as %s.", gameID, color);
            } catch (NumberFormatException e) {
                throw new Exception("Game ID must be a number.");
            }
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String observeGame(String... params) throws Exception {
        if (params.length == 1) {
            try {
                int gameID = Integer.parseInt(params[0]);
//                facade.joinGame(null, gameID, authToken);
                var board = new chess.ChessBoard();
                board.resetBoard();
                BoardDrawer drawer = new BoardDrawer();
                drawer.drawBoard(board, true);
                System.out.println();
                drawer.drawBoard(board, false);
                return String.format("Successfully joined game %d as an observer.", gameID);
            } catch (NumberFormatException e) {
                throw new Exception("Game ID must be a number.");
            }
        }
        throw new Exception("Expected: <ID>");
    }
}