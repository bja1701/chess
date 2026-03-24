package ui;

import client.ServerFacade;

import java.util.Scanner;

public class Repl {

    private final PreloginUI preloginUI;

    public Repl() {
        ServerFacade facade = new ServerFacade(8080);
        this.preloginUI = new PreloginUI(facade);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("[LOGGED_OUT] >>> ");
            String line = scanner.nextLine();
            try {
                result = preloginUI.eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }
}