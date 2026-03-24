package ui;

import client.ServerFacade;

public class PostloginUI {
    private final ServerFacade facade;

    public PostloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {
        return "You are in the Postlogin Menu! Type 'logout' to go back.";
    }
}