package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.endPosition = endPosition;
        this.startPosition = startPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public static Collection<ChessMove> getSlideMoves(ChessBoard board,
                                                      ChessPosition position,
                                                      int[][] directions,
                                                      Collection<ChessMove> moves){
        int row = position.getRow();
        int col = position.getColumn();
        for (int[] direction : directions) {
            int rayX = col;
            int rayY = row;
            while (true) {
                rayX += direction[0];
                rayY += direction[1];
                // Check board edges
                if (rayX > 8 || rayX < 1 || rayY > 8 || rayY < 1) {
                    break;
                }
                // Check what is on space, add it to moves if good, otherwise break
                ChessPosition currentSpace = new ChessPosition(rayY, rayX);
                if (board.getPiece(currentSpace) == null) {
                    moves.add(new ChessMove(position, currentSpace, null));
                } else if (board.getPiece(currentSpace).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(position, currentSpace, null));
                    break;
                } else if (board.getPiece(currentSpace).getTeamColor() == board.getPiece(position).getTeamColor()){
                    break;
                }

            }
        }
        return moves;
    }

    public static Collection<ChessMove> getMoves(ChessBoard board,
                                                      ChessPosition position,
                                                      int[][] pieceMoves,
                                                 Collection<ChessMove> moves){
        int row = position.getRow();
        int col = position.getColumn();

        for (int[] move : pieceMoves) {
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
                moves.add(new ChessMove(position, nextPos, null));
            } else if (pieceAtNextPos.getTeamColor()
                    != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, nextPos, null));
            }
        }
        return moves;
    }


    @Override
    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition)
                && Objects.equals(endPosition, chessMove.endPosition)
                && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
