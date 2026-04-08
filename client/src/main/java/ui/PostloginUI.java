package ui;

import client.ServerFacade;
import java.util.Arrays;

public class PostloginUI {
    private final ServerFacade facade;
    private String authToken = null;
    private model.GameData[] gamesList;

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
            this.gamesList = new model.GameData[0];
            return "No active games found. Type 'create <NAME>' to start one!";
        }
        this.gamesList = games.toArray(new model.GameData[0]);
        StringBuilder sb = new StringBuilder("Active Games:\n");
        for (int i = 0; i < gamesList.length; i++) {
            var game = gamesList[i];
            sb.append(String.format("  [%d] %s\n      White: %s\n      Black: %s\n",
                    i + 1,
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "Empty",
                    game.blackUsername() != null ? game.blackUsername() : "Empty"));
        }
        return sb.toString();
    }

    // add a map where number of game in list is the game id (list num to game id)
    private String joinGame(String... params) throws Exception {
        if (params.length == 2) {
            try {
                int gameID = getGameID(params[0]);
                String color = params[1].toUpperCase();
                facade.joinGame(color, gameID, authToken);
                chess.ChessGame.TeamColor teamColor = color.equals("WHITE") ?
                        chess.ChessGame.TeamColor.WHITE : chess.ChessGame.TeamColor.BLACK;
                new GameplayUI("http://localhost:8080", authToken, gameID, teamColor).run();
                return "";
            } catch (NumberFormatException e) {
                throw new Exception("Game number must be an integer.");
            }
        }
        throw new Exception("Expected: <NUMBER> [WHITE|BLACK]");
    }

    private String observeGame(String... params) throws Exception {
        if (params.length == 1) {
            try {
                int gameID = getGameID(params[0]);
                new GameplayUI("http://localhost:8080", authToken, gameID, chess.ChessGame.TeamColor.WHITE).run();
                return "";
            } catch (NumberFormatException e) {
                throw new Exception("Game number must be an integer.");
            }
        }
        throw new Exception("Expected: <NUMBER>");
    }

    private int getGameID(String indexStr) throws Exception {
        listGames();
        int index = Integer.parseInt(indexStr);
        if (gamesList == null || index < 1 || index > gamesList.length) {
            throw new Exception("Invalid game number. Type 'list' to see available games.");
        }
        return gamesList[index - 1].gameID();
    }
}