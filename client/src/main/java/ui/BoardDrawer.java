package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public void drawBoard(ChessBoard board) {
        System.out.println();
        drawHeaders();
        drawCheckerboard(board);
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

    private void drawCheckerboard(ChessBoard board) {
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
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                printPiece(piece);
            }
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_BLACK);
            System.out.print(" " + row + " ");
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            System.out.println();
        }
    }

    private void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print(EMPTY);
            return;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.print(switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            });
        } else {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.print(switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            });
        }
    }
}