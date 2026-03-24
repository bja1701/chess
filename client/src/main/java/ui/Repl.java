package ui;

import java.util.Scanner;

public class Repl {

    private final PreloginUI preloginUI;

    public Repl() {
        this.preloginUI = new PreloginUI();
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
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }
}