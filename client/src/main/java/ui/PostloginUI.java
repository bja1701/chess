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
}