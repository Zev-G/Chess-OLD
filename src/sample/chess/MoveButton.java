package sample.chess;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;


public class MoveButton extends Circle {

    Point2D loc;
    Point2D pieceLoc;
    ChessPiece move;
    ChessBoard board;
    ComplexMoveSequence complexMoveSequence = null;
    Point2D enPassant = null;

    public MoveButton(ChessPiece moveFor, Point2D loc, ChessBoard chessBoard) {
        this.pieceLoc = moveFor.getLocation();
        this.move = moveFor;
        this.loc = loc;
        this.board = chessBoard;
        init();
    }
    public MoveButton(ChessPiece moveFor, Point2D loc, ChessBoard chessBoard, Point2D enPassant) {
        this.pieceLoc = moveFor.getLocation();
        this.move = moveFor;
        this.loc = loc;
        this.board = chessBoard;
        this.enPassant = enPassant;
        init();
    }
    public MoveButton(ChessPiece moveFor, ComplexMoveSequence moveSequence, Point2D to) {
        this.pieceLoc = moveFor.getLocation();
        this.move = moveFor;
        this.loc = to;
        this.board = moveFor.getBoard();
        this.complexMoveSequence = moveSequence;
        init();
    }
    public MoveButton(ChessPiece moveFor, Point2D loc) {
        this.pieceLoc = moveFor.getLocation();
        this.move = moveFor;
        this.loc = loc;
        this.board = moveFor.getBoard();
        init();
    }

    public void addToAnchorPane() {
        this.board.getSpotFromLoc(this.loc).getChildren().add(this);
        AnchorPane.setTopAnchor(this, 22.0);
        AnchorPane.setBottomAnchor(this, 22.0);
        AnchorPane.setRightAnchor(this, 22.0);
        AnchorPane.setLeftAnchor(this, 22.0);
    }

    private void init() {
        this.setCursor(Cursor.HAND);
        this.setFill(Paint.valueOf("#a8a8a8"));
        this.setRadius(28);
        this.setOpacity(0.7);
        this.setOnMousePressed(mouseEvent -> fire());
    }

    public void fire() {
        if (this.complexMoveSequence == null) {
            this.board.moveChessPiece(this.move, this.pieceLoc, this.loc);
            if (this.move.getPieceType() == PieceType.PAWN) {
                if (this.enPassant != null) {
                    this.board.moveChessPiece(this.board.getSpotFromLoc(this.enPassant).getPiece(), this.enPassant, null, false, true);
                }

//                double x = this.loc.getX() - this.pieceLoc.getX();
//                double y = this.loc.getY() - this.pieceLoc.getY();
//                System.out.println("X: " + x + " Y: " + y);
//                if (x == 0.0 && (y == 2 || y == -2)) {
//                    Point2D left = new Point2D(this.loc.getX() - 1, this.loc.getY());
//                    Point2D right = new Point2D(this.loc.getX() + 1, this.loc.getY());
//                    Point2D newPoint = new Point2D(this.loc.getX(), y == -2 ? this.loc.getY() + 1 : this.loc.getY() - 1);
//                    if (this.board.getSpotFromLoc(newPoint).getPiece() == null) {
//                        if (this.board.getIsValidLoc(left) && this.board.getSpotFromLoc(left).getPiece() != null && this.board.getSpotFromLoc(left).getPiece().getPieceType() == PieceType.PAWN) {
//                            this.board.getSpotFromLoc(left).getPiece().setEnPassant(newPoint);
//                            this.board.getSpotFromLoc(left).getPiece().setEnPassantTake(this.loc);
//                        }
//                        if (this.board.getIsValidLoc(right) && this.board.getSpotFromLoc(right).getPiece() != null && this.board.getSpotFromLoc(right).getPiece().getPieceType() == PieceType.PAWN) {
//                            this.board.getSpotFromLoc(right).getPiece().setEnPassant(newPoint);
//                            this.board.getSpotFromLoc(right).getPiece().setEnPassantTake(this.loc);
//                        }
//                    }
//                }
            }
        } else {
            this.board.moveChessPiece(this.move, this.pieceLoc, this.loc, false, false);
            this.board.moveChessPiece(this.complexMoveSequence.getSecondPiece(), this.complexMoveSequence.getSecondPiece().getLocation(), this.complexMoveSequence.getMoveSecondTo());
        }
        this.board.resetMoveButtons();
    }

}
