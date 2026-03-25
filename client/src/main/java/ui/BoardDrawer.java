package ui;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public void drawBoard() {
        System.out.println();
        drawHeaders();
        drawCheckerboard();
        drawHeaders();
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void drawHeaders() {
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_BLACK);
        System.out.print("    a   b   c  d   e   f  g   h    ");
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void drawCheckerboard() {
        for (int row = 8; row >= 1; row--) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");

            for (int col = 1; col <= 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_BLACK);
                } else {
                    System.out.print(SET_BG_COLOR_WHITE);
                }
                System.out.print(EMPTY);
            }
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            System.out.println();
        }
    }
}