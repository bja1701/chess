package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = { {1,1}, {-1,-1}, {-1,1}, {1,-1}};
        Collection<ChessMove> moves = new ArrayList<>();
        return ChessMove.getSlideMoves(board, myPosition, directions, moves);
    }
}
