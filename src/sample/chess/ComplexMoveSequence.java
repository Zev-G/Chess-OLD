package sample.chess;

import javafx.geometry.Point2D;

public class ComplexMoveSequence {

    private final MoveSequence moveSequence;

    private ChessPiece secondPiece = null;
    private final Point2D move2To;
    private final boolean secondPieceRequireNeverMoved;
    private final boolean kingSafety;

    private Point2D moveSecondFrom;
    private ChessBoard board = null;


    public ComplexMoveSequence(MoveSequence sequence1, ChessPiece otherPiece, Point2D moveSecondTwo, boolean requireNeverMoved, boolean kingSafety) {
        this.moveSequence = sequence1;
        this.secondPiece = otherPiece;
        this.move2To = moveSecondTwo;
        this.secondPieceRequireNeverMoved = requireNeverMoved;
        this.kingSafety = kingSafety;
    }
    public ComplexMoveSequence(MoveSequence sequence1, ChessPiece thisPiece, Point2D moveSecondFrom, Point2D moveSecondTwo, boolean requireNeverMoved, boolean kingSafety) {
        this.moveSequence = sequence1;
        this.move2To = moveSecondTwo;
        this.board = thisPiece.getBoard();
        this.moveSecondFrom = moveSecondFrom;
        this.secondPieceRequireNeverMoved = requireNeverMoved;
        this.kingSafety = kingSafety;
    }

    public void initSecondPiece() {
        if (secondPiece == null) {
            this.secondPiece = this.board.getSpotFromLoc(this.moveSecondFrom).getPiece();
        }
    }
    public ChessPiece getSecondPiece() {
        return this.secondPiece;
    }
    public Point2D getMoveSecondTo() {
        return this.move2To;
    }
    public boolean getSecondPieceRequireNeverMoved() {
        return this.secondPieceRequireNeverMoved;
    }
    public MoveSequence getMoveSequence() {
        return this.moveSequence;
    }
    public boolean getKingSafety() {
        return this.kingSafety;
    }
}
