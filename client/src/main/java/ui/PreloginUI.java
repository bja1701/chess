package ui;

import client.ServerFacade;
import java.util.Arrays;

public class PreloginUI {

    private final ServerFacade facade;

    public PreloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private String register(String... params) throws Exception {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            facade.register(username, password, email);
            return String.format("Successfully registered and logged in as %s.", username);
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) {
        return "Login method hit! (We will wire this next)";
    }
}