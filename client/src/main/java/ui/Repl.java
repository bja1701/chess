package ui;

import java.util.Scanner;

public class Repl {

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("[LOGGED_OUT] >>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("quit")) {
                result = "quit";
            } else {
                // test
                System.out.println("You typed: " + line);
            }
        }
        System.out.println("Goodbye!");
    }
}