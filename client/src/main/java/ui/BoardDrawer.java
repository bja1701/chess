package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public void drawBoard(ChessBoard board, boolean whitePerspective) {
        System.out.println();
        drawHeaders(whitePerspective);
        drawCheckerboard(board, whitePerspective);
        drawHeaders(whitePerspective);
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void drawHeaders(boolean whitePerspective) {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_WHITE);
        if (whitePerspective) {
            System.out.print("    a   b   c  d   e   f  g   h    ");
        } else {
            System.out.print("    h   g   f  e   d   c  b   a    ");
        }
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void drawCheckerboard(ChessBoard board, boolean whitePerspective) {
        int startRow = whitePerspective ? 8 : 1;
        int endRow = whitePerspective ? 1 : 8;
        int rowDirection = whitePerspective ? -1 : 1;
        int startCol = whitePerspective ? 1 : 8;
        int endCol = whitePerspective ? 8 : 1;
        int colDirection = whitePerspective ? 1 : -1;
        for (int row = startRow; row != endRow + rowDirection; row += rowDirection) {
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");
            for (int col = startCol; col != endCol + colDirection; col += colDirection) {
                if ((row + col) % 2 == 0) {
                    // Dark squares
                    System.out.print(SET_BG_COLOR_DARK_GREY);
                } else {
                    // Light squares
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                printPiece(piece);
            }
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
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
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            });
        } else {
            System.out.print(SET_TEXT_COLOR_BLACK);
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

    public void drawHighlightBoard(ChessBoard board, boolean whitePerspective, ChessPosition selectedPosition, java.util.Collection<chess.ChessMove> validMoves) {
        System.out.println();
        drawHeaders(whitePerspective);
        drawCheckerboardWithHighlights(board, whitePerspective, selectedPosition, validMoves);
        drawHeaders(whitePerspective);
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void drawCheckerboardWithHighlights(ChessBoard board, boolean whitePerspective, ChessPosition selectedPosition, java.util.Collection<chess.ChessMove> validMoves) {
        int startRow = whitePerspective ? 8 : 1;
        int endRow = whitePerspective ? 1 : 8;
        int rowDirection = whitePerspective ? -1 : 1;
        int startCol = whitePerspective ? 1 : 8;
        int endCol = whitePerspective ? 8 : 1;
        int colDirection = whitePerspective ? 1 : -1;

        for (int row = startRow; row != endRow + rowDirection; row += rowDirection) {
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");
            for (int col = startCol; col != endCol + colDirection; col += colDirection) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                boolean isHighlighted = currentPosition.equals(selectedPosition);

                if (!isHighlighted && validMoves != null) {
                    for (chess.ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(currentPosition)) {
                            isHighlighted = true;
                            break;
                        }
                    }
                }

                if (isHighlighted) {
                    if ((row + col) % 2 == 0) {
                        System.out.print(SET_BG_COLOR_DARK_GREEN);
                    } else {
                        System.out.print(SET_BG_COLOR_GREEN);
                    }
                } else {
                    if ((row + col) % 2 == 0) {
                        System.out.print(SET_BG_COLOR_DARK_GREY);
                    } else {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    }
                }
                ChessPiece piece = board.getPiece(currentPosition);
                printPiece(piece);
            }
            System.out.print(SET_BG_COLOR_BLACK);
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.print(" " + row + " ");
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_COLOR);
            System.out.println();
        }
    }
}