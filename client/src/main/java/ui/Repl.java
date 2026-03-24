package ui;

import client.ServerFacade;
import java.util.Scanner;

public class Repl {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private State state = State.SIGNED_OUT;

    public Repl() {
        ServerFacade facade = new ServerFacade(8080);
        this.preloginUI = new PreloginUI(facade);
        this.postloginUI = new PostloginUI(facade);
    }

    public void run() {
        System.out.println("♕ Type Help to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (state == State.SIGNED_OUT) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
            }
            String line = scanner.nextLine();
            try {
                if (state == State.SIGNED_OUT) {
                    result = preloginUI.eval(line);
                    if (result.contains("logged in")) {
                        state = State.SIGNED_IN;
                    }
                } else {
                    result = postloginUI.eval(line);
                    if (result.equals("logout")) {
                        state = State.SIGNED_OUT;
                        result = "You have been logged out.";
                    }
                }
                System.out.println(result);
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }
}