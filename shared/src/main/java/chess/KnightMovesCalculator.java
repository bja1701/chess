package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // only numbers possible for adding to row and column are -1 to 1 for king.
        int[][] knightMoves = { {2,1}, {1,2}, {-2,1}, {1,-2}, {2,-1}, {-1,2}, {-2,-1}, {-1,-2}};
        // loop through moves
        for (int[] move : knightMoves) {
            int nextRow = row + move[0];
            int nextCol = col + move[1];
            // Check board edge, skip if not on board
            if (nextRow < 1 || nextRow > 8 || nextCol < 1 || nextCol > 8) {
                continue;
            }
            // Check square going to
            ChessPosition nextPos = new ChessPosition(nextRow, nextCol);
            ChessPiece pieceAtNextPos = board.getPiece(nextPos);
            // Check valid move
            if (pieceAtNextPos == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (pieceAtNextPos.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }
        return moves;
    }
}

