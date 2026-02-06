package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] knightMoves = { {2,1}, {1,2}, {-2,1}, {1,-2},
                {2,-1}, {-1,2}, {-2,-1}, {-1,-2}};
        Collection<ChessMove> moves = new ArrayList<>();
        return ChessMove.getMoves(board, myPosition, knightMoves, moves);
    }
}

