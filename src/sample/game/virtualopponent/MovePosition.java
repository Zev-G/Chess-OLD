package sample.game.virtualopponent;

import javafx.geometry.Point2D;
import sample.chess.ChessPiece;

public class MovePosition {

    ChessPiece piece;
    Point2D point2D;

    public MovePosition(ChessPiece piece, Point2D point2D) {
        this.piece = piece;
        this.point2D = point2D;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }
    public Point2D getPoint2D() {
        return this.point2D;
    }
    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }
    public void setPoint2D(Point2D point2D) {
        this.point2D = point2D;
    }
}
