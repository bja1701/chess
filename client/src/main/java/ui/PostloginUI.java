package ui;

import client.ServerFacade;
import java.util.Arrays;

public class PostloginUI {
    private final ServerFacade facade;

    public PostloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
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

    private String logout() {
        return "logout";
    }
}