package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{

    public Collection<ChessMove> pieceMoves(ChessBoard board,
                                            ChessPosition myPosition) {


        // only numbers possible for adding to row and column are -1 to 1 for king.
        int[][] kingMoves = { {1,0}, {1, 1}, {0,1}, {-1,1},
                {-1, 0}, {-1,-1}, {0,-1}, {1,-1}};
        Collection<ChessMove> moves = new ArrayList<>();
        return ChessMove.getMoves(board, myPosition, kingMoves, moves);
    }
}
