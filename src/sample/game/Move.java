package sample.game;


import javafx.geometry.Point2D;
import sample.chess.ChessPiece;

public class Move {

    Point2D from;
    Point2D to;
    ChessPiece piece;

    ChessPiece takePiece;

    public Move(Point2D oldLoc, Point2D newLoc, ChessPiece chessPiece) {
        this.from = oldLoc;
        this.to = newLoc;
        this.piece = chessPiece;
        this.takePiece = null;
    }
    public Move(Point2D oldLoc, Point2D newLoc, ChessPiece chessPiece, ChessPiece taken) {
        this.from = oldLoc;
        this.to = newLoc;
        this.piece = chessPiece;
        this.takePiece = taken;
    }

    public Point2D getFrom() {
        return from;
    }
    public Point2D getTo() {
        return to;
    }
    public ChessPiece getPiece() {
        return piece;
    }
    public ChessPiece getTakePiece() {
        return takePiece;
    }

    @Override
    public String toString() {
        return "Move { From (X: "
                + this.from.getX()
                + " Y: "
                + this.from.getY()
                + ") To (X: "
                + this.to.getX()
                + " Y: "
                + this.to.getY()
                + ")";
    }
}
