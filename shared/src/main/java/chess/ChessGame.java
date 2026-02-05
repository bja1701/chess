package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> goodMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves){
//            current state
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            ChessPiece oppPiece = board.getPiece(move.getStartPosition());
            // move piece to test
            board.addPiece(move.getEndPosition(), oppPiece);
            board.addPiece(move.getStartPosition(), null);
            // check if king is safe
            if (!isInCheck(piece.getTeamColor())){
                goodMoves.add(move);
            }
            // put piece back?
            board.addPiece(move.getStartPosition(), oppPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);

        }
        return goodMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition beginning = move.getStartPosition();
        ChessPiece piece = board.getPiece(beginning);

        if (piece == null) {
            throw new InvalidMoveException("No piece");
        }

        Collection<ChessMove> safeMoves = validMoves(beginning);

        if (piece.getTeamColor() != getTeamTurn() || !safeMoves.contains(move)){
            throw new InvalidMoveException("Can't move there");
        } else {
            board.addPiece(beginning, null);

        }
        // What do I need to check? promotion move, or just a regular move
        if (move.getPromotionPiece() != null){
            ChessPiece promoPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promoPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingLocation = findKing(teamColor);
        // check moves to see if king is in check... not sure how to best do that
        // check all opponent pieces to see if they have moves that hit the kingLocation
        // iterate through every opponent piece.
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col ++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null){
                    ChessGame.TeamColor pieceColor = board.getPiece(pos).getTeamColor();
                    if (pieceColor != teamColor){
                        Collection<ChessMove> enemyMoves = piece.pieceMoves(board, pos);
                        for (ChessMove move : enemyMoves){
                            if (move.getEndPosition().equals(kingLocation)){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        ChessPosition kingLocation = findKing(teamColor);
        if (!isInCheck(teamColor)){
            return false;
        }

        // we have valid moves, we have is in check
        // can any pieces make a valid move
        // if there are valid moves then that means that move will escape check and be good

        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col ++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null) {
                    ChessGame.TeamColor pieceColor = board.getPiece(pos).getTeamColor();
                    if (pieceColor == teamColor) {
                        Collection<ChessMove> moves = validMoves(pos);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // stalemate occurs when not in check but no valid moves...

        if (isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor){
                    if (!validMoves(pos).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param newBoard the new board to use
     */
    public void setBoard(ChessBoard newBoard) {
        board = newBoard;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor){
        for (int row = 1; row <=8 ; row++){
            for (int col = 1; col<=8 ; col++){
                ChessPosition space = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(space);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return space;
                }

            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
