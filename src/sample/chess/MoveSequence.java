package sample.chess;

import javafx.geometry.Point2D;


public class MoveSequence {

    private final boolean repeat;
    private final boolean requirePiece;
    private final boolean requireNeverMove;
    private final Point2D currentLoc;
    private final boolean attack;

    public MoveSequence(boolean repeatable, Point2D location) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = false;
        this.requireNeverMove = false;
        this.attack = true;
    }
    public MoveSequence(boolean repeatable, boolean needPiece,  Point2D location) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = needPiece;
        this.requireNeverMove = false;
        this.attack = true;
    }
    public MoveSequence(boolean repeatable, Point2D location, boolean neverMoved) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = false;
        this.requireNeverMove = neverMoved;
        this.attack = true;
    }
    public MoveSequence(boolean repeatable, boolean neverMoved, boolean needPiece, Point2D location) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = needPiece;
        this.requireNeverMove = neverMoved;
        this.attack = true;
    }
    public MoveSequence(boolean repeatable, boolean neverMoved, boolean needPiece, Point2D location, boolean attack) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = needPiece;
        this.requireNeverMove = neverMoved;
        this.attack = attack;
    }
    public MoveSequence(boolean repeatable, boolean needPiece,  Point2D location, boolean attack) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = needPiece;
        this.requireNeverMove = false;
        this.attack = attack;
    }
    public MoveSequence(boolean repeatable, Point2D location, boolean neverMoved, boolean attack) {
        this.repeat = repeatable;
        this.currentLoc = location;
        this.requirePiece = false;
        this.requireNeverMove = neverMoved;
        this.attack = attack;
    }



    public boolean getRequireNeverMove() {return this.requireNeverMove;}
    public boolean getRequirePiece() {return this.requirePiece;}
    public boolean getRepeat() {return this.repeat;}
    public Point2D getPoint() {return this.currentLoc;}
    public boolean getAttack() {return this.attack;}

}
