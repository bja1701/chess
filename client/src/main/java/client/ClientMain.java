package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess Client ♕");
        Repl repl = new Repl();
        repl.run();
    }
}