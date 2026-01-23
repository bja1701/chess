package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int color;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            color = 1; //White
        } else {
            color = 0; //Black
        }

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction;
        int promotionRow;
        int startRow;

        if (color == 1){
            direction = 1;
            promotionRow = 8;
            startRow = 2;
        } else {
            direction = -1;
            promotionRow = 1;
            startRow = 7;
        }

        // Rule 1 Single step forward
        int nextRow = row + direction;
        ChessPosition forwardPos = new ChessPosition(nextRow, col);
        if (forwardPos.isOnBoard() && board.getPiece(forwardPos) == null && forwardPos.getRow() != promotionRow){
            moves.add(new ChessMove(myPosition, forwardPos, null));
            if (row == startRow){
                int doubleRow = row + (direction * 2);
                ChessPosition doubleMovePos = new ChessPosition(doubleRow, col);
                if (board.getPiece(doubleMovePos) == null){
                    moves.add(new ChessMove(myPosition, doubleMovePos, null));
                }
            }
        } else if (forwardPos.getRow() == promotionRow && board.getPiece(forwardPos) == null){
            moves.add(new ChessMove(myPosition, forwardPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, forwardPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, forwardPos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, forwardPos, ChessPiece.PieceType.KNIGHT));
        }

        int [] captureCol = {col - 1, col + 1};
        for (int nextCol : captureCol){
            if (nextCol >= 1 && nextCol <= 8){
                ChessPosition enemyPos = new ChessPosition(nextRow, nextCol);
                ChessPiece enemyPiece = board.getPiece(enemyPos);
                if (enemyPiece != null && enemyPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                    if (enemyPos.getRow() == promotionRow){
                        moves.add(new ChessMove(myPosition, enemyPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, enemyPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, enemyPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, enemyPos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, enemyPos, null));
                    }
                }
            }
        }
        return moves;
    }
}
