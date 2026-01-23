package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] direction : directions) {
            int ray_x = col;
            int ray_y = row;
            while (true) {
                ray_x += direction[0];
                ray_y += direction[1];
                // Check board edges
                if (ray_x > 8 || ray_x < 1 || ray_y > 8 || ray_y < 1) {
                    break;
                }
                // Check what is on space, add it to moves if good, otherwise break
                ChessPosition currentSpace = new ChessPosition(ray_y, ray_x);
                if (board.getPiece(currentSpace) == null) {
                    moves.add(new ChessMove(myPosition, currentSpace, null));
                } else if (board.getPiece(currentSpace).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, currentSpace, null));
                    break;
                } else if (board.getPiece(currentSpace).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }

            }
        }
        return moves;
    }
}
