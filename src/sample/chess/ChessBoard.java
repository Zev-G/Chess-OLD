package sample.chess;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import sample.game.Game;
import sample.game.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessBoard extends GridPane {

    Integer width;
    Integer height;
    HashMap<Point2D, BoardSpot> boardSpotMap = new HashMap<>();
    HashMap<Point2D, ChessPiece> chessPieceMap = new HashMap<>();
    Game game = null;

    private ChessPiece highlightedPiece;

    public ChessBoard(int width, int height) {
        this.width = width;
        this.height = height;
        init();
        setAllBoardItems(initBoardItems());
    }
    public ChessBoard(int width, int height, HashMap<Point2D, ChessPiece> map) {
        this.width = width;
        this.height = height;
        init();
        setAllBoardItems(map);
    }
    public ChessBoard(Game inputGame, int width, int height) {
        this.game = inputGame;
        this.width = width;
        this.height = height;
        init();
        setAllBoardItems(initBoardItems());
    }
    public ChessBoard() {
        this.width = 8;
        this.height = 8;
        init();
        setAllBoardItems(initBoardItems());
    }
    public ChessBoard(boolean empty) {
        if (!empty) {
            this.width = 8;
            this.height = 8;
            init();
            setAllBoardItems(initBoardItems());
        }
    }

    public void setBoardWidth(int width) {
        this.width = width;
    }
    public void setBoardHeight(int height) {
        this.height = height;
    }
    public void setBoardSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Game getGame() {return this.game;}
    public BoardSpot getSpotFromLoc(Point2D point2D) {
        return this.boardSpotMap.get(point2D);
    }
    public ChessPiece getPieceFromLoc(Point2D point2D) {
        return this.chessPieceMap.get(point2D);
    }
    public Point2D getLocFromPiece(ChessPiece getFrom) {
        for (Map.Entry<Point2D, ChessPiece> entry : this.chessPieceMap.entrySet()) {
            if (entry.getValue() == getFrom) {
                return entry.getKey();
            }
        }
        return null;
    }
    public Point2D getLocFromSpot(BoardSpot getFrom) {
        for (Map.Entry<Point2D, BoardSpot> entry : this.boardSpotMap.entrySet()) {
            if (entry.getValue() == getFrom) {
                return entry.getKey();
            }
        }
        return null;
    }
    public boolean getIsValidLoc(Point2D point2D) {
        if (point2D.getX() >= this.width || point2D.getY() >= this.height) {return false;}
        return !(point2D.getX() < 0) && !(point2D.getY() < 0);
    }
    public HashMap<Point2D, BoardSpot> getBoardSpotMap() {
        return this.boardSpotMap;
    }
    public HashMap<Point2D, ChessPiece> getChessPieceMap() {
        return this.chessPieceMap;
    }
    public boolean getCheckMateStatus(Team checkFor) {
        HashMap<Point2D, ChessPiece> clonedHashMap = (HashMap<Point2D, ChessPiece>) this.chessPieceMap.clone();
        Point2D kingLoc = null;
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() == checkFor && entry.getValue().getPieceType() == PieceType.KING) { kingLoc = entry.getKey(); }
        }
        boolean inCheck = false;
        for (ChessPiece entry : clonedHashMap.values()) {
            ArrayList<Point2D> loopMoves = entry.getMoveBuilder().getMoves();
            if (entry.getTeam() != checkFor) {
                if (!inCheck && loopMoves.contains(kingLoc)) { inCheck = true; }
            } else {
                if (!loopMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return inCheck;
    }

    public boolean getCheckStatus(Team checkFor) {
        HashMap<Point2D, ChessPiece> clonedHashMap = (HashMap<Point2D, ChessPiece>) this.chessPieceMap.clone();
        Point2D kingLoc = null;
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() == checkFor && entry.getValue().getPieceType() == PieceType.KING) { kingLoc = entry.getKey(); }
        }
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() != checkFor) {
                if (entry.getValue().getMoveBuilder().getMoves().contains(kingLoc)) { return true; }
            }
        }
        return false;
    }

    public Point2D getKingLoc(Team checkFor) {
        HashMap<Point2D, ChessPiece> clonedHashMap = (HashMap<Point2D, ChessPiece>) this.chessPieceMap.clone();
        Point2D kingLoc = null;
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() == checkFor && entry.getValue().getPieceType() == PieceType.KING) { kingLoc = entry.getKey(); }
        }
        return kingLoc;
    }

    public boolean getStalemateStatus(Team checkFor) {
        HashMap<Point2D, ChessPiece> clonedHashMap = (HashMap<Point2D, ChessPiece>) this.chessPieceMap.clone();
        Point2D kingLoc = null;
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() == checkFor && entry.getValue().getPieceType() == PieceType.KING) { kingLoc = entry.getKey(); }
        }
        boolean inCheck = false;
        ArrayList<Point2D> moves = new ArrayList<>();
        for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
            if (entry.getValue().getTeam() != checkFor) {
                if (!inCheck && entry.getValue().getMoveBuilder().getMoves().contains(kingLoc)) { inCheck = true; }
            } else {
                ArrayList<Point2D> loopMoves = entry.getValue().getMoveBuilder().getMoves();
                moves.addAll(loopMoves);
            }
        }
        return !inCheck && moves.size() == 0;
    }
    public Point2D getComplexSpot(int index) {
        if (index == 1) { return new Point2D(0, 0); }
        if (index == 2) { return new Point2D(this.width - 1, 0); }
        if (index == 3) { return new Point2D(0, this.height - 1); }
        if (index == 4) { return new Point2D(this.width - 1, this.height - 1); }
        return null;
    }

    public void setHighlightedPiece(ChessPiece piece) {
        this.highlightedPiece = piece;
    }
    public ChessPiece getHighlightedPiece() {
        return highlightedPiece;
    }

    public void setGame(Game input) {
        this.game = input;
    }
    public void setOrientation(Team turn) {
        if (turn == Team.WHITE) {
            for (Map.Entry<Point2D, ChessPiece> entry : chessPieceMap.entrySet()) {
                entry.getValue().setRotate(0);
            }
            this.setRotate(0);
        } else {
            for (Map.Entry<Point2D, ChessPiece> entry : chessPieceMap.entrySet()) {
                entry.getValue().setRotate(180);
            }
            this.setRotate(180);
        }
    }
    public void setOrientationWithTransition(Team turn, Duration duration) {
        this.setDisable(true);
        ParallelTransition parallelTransition = new ParallelTransition();
        if (turn == Team.WHITE) {
            for (Map.Entry<Point2D, ChessPiece> entry : chessPieceMap.entrySet()) {
                RotateTransition rotateTransition = new RotateTransition(duration, entry.getValue());
                rotateTransition.setToAngle(0);
                parallelTransition.getChildren().add(rotateTransition);
            }
            RotateTransition rotateTransition = new RotateTransition(duration, this);
            rotateTransition.setToAngle(0);
            parallelTransition.getChildren().add(rotateTransition);
        } else {
            for (Map.Entry<Point2D, ChessPiece> entry : chessPieceMap.entrySet()) {
                RotateTransition rotateTransition = new RotateTransition(duration, entry.getValue());
                rotateTransition.setToAngle(180);
                parallelTransition.getChildren().add(rotateTransition);
            }
            RotateTransition rotateTransition = new RotateTransition(duration, this);
            rotateTransition.setToAngle(180);
            parallelTransition.getChildren().add(rotateTransition);
        }
        parallelTransition.play();
        parallelTransition.setOnFinished(actionEvent -> this.setDisable(false));
    }
    public Team getOrientation() {
        if (this.getRotate() == 180) {
            return Team.BLACK;
        } else {
            return Team.WHITE;
        }
    }

    public void moveChessPiece(ChessPiece move, Point2D oldPoint, Point2D newPoint) { moveChessPiece(move, oldPoint, newPoint, true, false); }
    public void moveChessPiece(ChessPiece move, Point2D oldPoint, Point2D newPoint, boolean nextTurn) { moveChessPiece(move, oldPoint, newPoint, nextTurn, true); }
    public void moveChessPiece(ChessPiece move, Point2D oldPoint, Point2D newPoint, boolean nextTurn, boolean blockActions) {
        if (this.chessPieceMap.get(oldPoint) == move || oldPoint == null) {

            if (nextTurn && !blockActions) { move.setNeverMoved(false); }
            if (this.game != null && nextTurn) { this.game.getPastChessBoards().add((HashMap<Point2D, ChessPiece>) this.chessPieceMap.clone()); }

            this.chessPieceMap.remove(oldPoint, move);

            if (oldPoint != null) {
                BoardSpot oldSpot = getSpotFromLoc(oldPoint);
                oldSpot.setPiece(null);
            }
            ChessPiece taken = null;
            if (newPoint != null) {
                BoardSpot newSpot = getSpotFromLoc(newPoint);
                taken = newSpot.getPiece();
                newSpot.setPiece(move);
                this.chessPieceMap.put(newPoint, move);
            }
            if (!blockActions && move.getPieceType() == PieceType.PAWN && newPoint != null) {
                if (move.getTeam() == Team.BLACK && newPoint.getY() == this.height - 1) {
                    this.game.showPromotion(newPoint);
                } else if (move.getTeam() == Team.WHITE && newPoint.getY() == 0) {
                    this.game.showPromotion(newPoint);
                }
            }


            if (this.game != null && nextTurn) {
                if (taken != null) {
                    this.game.addMove(new Move(oldPoint, newPoint, move, taken));
                } else {
                    this.game.addMove(new Move(oldPoint, newPoint, move));
                }
            }
        }
    }
    public void virtualMoveChessPiece(ChessPiece move, Point2D newPoint) {
        this.virtualMoveChessPiece(move, move.getLocation(), newPoint);
    }
    public void virtualMoveChessPiece(ChessPiece move, Point2D oldPoint, Point2D newPoint) {
        if (this.chessPieceMap.get(oldPoint) == move || oldPoint == null) {

            this.chessPieceMap.remove(oldPoint, move);

            if (oldPoint != null) {
                BoardSpot oldSpot = getSpotFromLoc(oldPoint);
                oldSpot.setVirtualPiece(null);
            }
            if (newPoint != null) {
                BoardSpot newSpot = getSpotFromLoc(newPoint);
                newSpot.setVirtualPiece(move);
                this.chessPieceMap.put(newPoint, move);
            }

        }
    }
    public void resetMoveButtons() {
        for (Map.Entry<Point2D, BoardSpot> entry : this.boardSpotMap.entrySet()) {
            entry.getValue().getChildren().removeIf(node -> node.getClass() == MoveButton.class);
            entry.getValue().restoreBg();
            if (this.highlightedPiece == entry.getValue().getPiece()) {
                ChessPiece piece = entry.getValue().getPiece();
                ScaleTransition scaleTransition = new ScaleTransition(new Duration(175), piece);
                scaleTransition.setToX(0.1);
                scaleTransition.setToY(0.1);
                scaleTransition.play();
            }
        }
        this.highlightedPiece = null;
    }
    public Team kingIsAlive() {
        boolean white = false;
        boolean black = false;
        for (Map.Entry<Point2D, ChessPiece> entry : this.chessPieceMap.entrySet()) {
            if (entry.getValue().getPieceType() == PieceType.KING) {
                if (entry.getValue().getTeam() == Team.WHITE) { white = true; }
                else { black = true; }
                if (black && white) { return null; }
            }
        }
        if (black) { return Team.BLACK; }
        if (white) { return Team.WHITE; }
        return null;
    }



    public void setAllBoardItems(HashMap<Point2D, ChessPiece> chessPieceHashMap) {
        for (Map.Entry<Point2D, ChessPiece> entry : chessPieceHashMap.entrySet()) {
            this.boardSpotMap.get(entry.getKey()).setPiece(entry.getValue());
            this.chessPieceMap.put(entry.getKey(), entry.getValue());
        }
    }
    public void setAndResetAllBoardItems(HashMap<Point2D, ChessPiece> chessPieceHashMap) {
        for (Map.Entry<Point2D, BoardSpot> entry : this.boardSpotMap.entrySet()) {
            if (chessPieceHashMap.get(entry.getKey()) != null) {
                entry.getValue().setPiece(chessPieceHashMap.get(entry.getKey()));
                this.chessPieceMap.put(entry.getKey(), chessPieceHashMap.get(entry.getKey()));
            } else {
                entry.getValue().setPiece(null);
                this.chessPieceMap.remove(entry.getKey());
            }
        }
    }
    public void setAndResetAllBoardItemsPlus(HashMap<Point2D, ChessPiece> chessPieceHashMap, int restoreIf) {
        for (Map.Entry<Point2D, BoardSpot> entry : this.boardSpotMap.entrySet()) {
            if (chessPieceHashMap.get(entry.getKey()) != null) {
                if (chessPieceHashMap.get(entry.getKey()).getTimeWhenFirstMoved() == restoreIf) { chessPieceHashMap.get(entry.getKey()).setNeverMoved(true); }
                entry.getValue().setPiece(chessPieceHashMap.get(entry.getKey()));
                this.chessPieceMap.put(entry.getKey(), chessPieceHashMap.get(entry.getKey()));
            } else {
                entry.getValue().setPiece(null);
                this.chessPieceMap.remove(entry.getKey());
            }
        }
    }

    private void init() {

        this.setOnContextMenuRequested(contextMenuEvent -> {
            for (Node currentChild : this.getChildren()) {
                if (currentChild.getClass() == BoardSpot.class) {
                    ((BoardSpot) currentChild).restoreBg();
                }
            }
        });

        this.getChildren().clear();
        for (int loopOne = 0; loopOne < width; loopOne++) {
            for (int loopTwo = 0; loopTwo < height; loopTwo++) {
                int together = loopOne + loopTwo;
                BoardSpot newBoardSpot = new BoardSpot(together % 2 == 0);
                this.add(newBoardSpot, loopOne, loopTwo);
                this.boardSpotMap.put(new Point2D(loopOne, loopTwo), newBoardSpot);
            }
        }
    }
    private HashMap<Point2D, ChessPiece> initBoardItems() {

        HashMap<Point2D, ChessPiece> returnMap = new HashMap<>();

        for (int i = 0; i < 8; i++) {
            returnMap.put(new Point2D(i, 1), new ChessPiece(Team.BLACK, PieceType.PAWN, this));
            returnMap.put(new Point2D(i, 6), new ChessPiece(Team.WHITE, PieceType.PAWN, this));
        }

        returnMap.put(new Point2D(0, 0), new ChessPiece(Team.BLACK, PieceType.ROOK, this));
        returnMap.put(new Point2D(7, 0), new ChessPiece(Team.BLACK, PieceType.ROOK, this));
        returnMap.put(new Point2D(1, 0), new ChessPiece(Team.BLACK, PieceType.KNIGHT, this));
        returnMap.put(new Point2D(6, 0), new ChessPiece(Team.BLACK, PieceType.KNIGHT, this));
        returnMap.put(new Point2D(2, 0), new ChessPiece(Team.BLACK, PieceType.BISHOP, this));
        returnMap.put(new Point2D(5, 0), new ChessPiece(Team.BLACK, PieceType.BISHOP, this));
        returnMap.put(new Point2D(3, 0), new ChessPiece(Team.BLACK, PieceType.QUEEN, this));
        returnMap.put(new Point2D(4, 0), new ChessPiece(Team.BLACK, PieceType.KING, this));

        returnMap.put(new Point2D(0, 7), new ChessPiece(Team.WHITE, PieceType.ROOK, this));
        returnMap.put(new Point2D(7, 7), new ChessPiece(Team.WHITE, PieceType.ROOK, this));
        returnMap.put(new Point2D(1, 7), new ChessPiece(Team.WHITE, PieceType.KNIGHT, this));
        returnMap.put(new Point2D(6, 7), new ChessPiece(Team.WHITE, PieceType.KNIGHT, this));
        returnMap.put(new Point2D(2, 7), new ChessPiece(Team.WHITE, PieceType.BISHOP, this));
        returnMap.put(new Point2D(5, 7), new ChessPiece(Team.WHITE, PieceType.BISHOP, this));
        returnMap.put(new Point2D(3, 7), new ChessPiece(Team.WHITE, PieceType.QUEEN, this));
        returnMap.put(new Point2D(4, 7), new ChessPiece(Team.WHITE, PieceType.KING, this));

        return returnMap;
    }

    public Integer getBoardHeight() {return this.height;}
    public Integer getBoardWidth() {return this.width;}

    public String getFormatted() {
        String header = "Chessboard:\n" +
                "\tSize: " + this.width + "," + this.height + ";\n" +
                "\tPieces:";
        StringBuilder middle = new StringBuilder();
        for (Map.Entry<Point2D, BoardSpot> entry : this.boardSpotMap.entrySet()) {
            middle.append("\n\t\t").append(entry.getKey().getX()).append("-").append(entry.getKey().getY()).append(": ");
            if (entry.getValue().getPiece() == null) {
                middle.append("null;");
            } else {
                ChessPiece piece = entry.getValue().getPiece();
                middle.append("*").append(piece.getTeam()).append(" *").append(piece.getPieceType()).append(" *");
                if (piece.getNeverMoved()) {
                    middle.append("Never Moved");
                } else {
                    middle.append("Moved at (").append(piece.getTimeWhenFirstMoved()).append(")");
                }

                middle.append(";");
            }
        }
        return header + middle;
    }

    public static ChessBoard getFormattedBoard(String formattedString) {
        String[] lines = formattedString.split("\n");
        int width;
        int height;
        HashMap<Point2D, ChessPiece> pieceHashMap = new HashMap<>();
        boolean passedBoard = false;
        ChessBoard board = new ChessBoard(true);
        for (String line : lines) {
            if (line.startsWith("\tSize: ")) {
                String[] separated = line.split(",");
                width = Integer.parseInt(separated[0].substring(separated[0].length() - 1));
                height = Integer.parseInt(String.valueOf(separated[1].charAt(0)));
                board.setBoardSize(width, height);
                board.init();
                continue;
            } else if (line.endsWith(";") && passedBoard) {


                if (line.contains("null")) {
                    continue;
                }
                String[] coordinateSep = line.replaceAll("\t", "").split(":");
                String[] valueAndSuffixSplit = coordinateSep[1].split(";");
                String[] valueSplit = valueAndSuffixSplit[0].split("\\*");
                Team team = Team.valueOf(valueSplit[1].replaceAll(" ", ""));
                PieceType type = PieceType.valueOf(valueSplit[2].replaceAll(" ", ""));
                Point2D location = new Point2D(
                        Double.parseDouble(coordinateSep[0].split("-")[0]), Double.parseDouble(coordinateSep[0].split("-")[1]));


                boolean neverMoved = true;
                int moveTime = -1;
                if (!valueSplit[3].equals("Never Moved")) {
                    neverMoved = false;
                    moveTime = Integer.parseInt(valueSplit[3].split("\\(")[1].split("\\)")[0]);
                }


                ChessPiece newPiece = new ChessPiece(team, type, board);
                newPiece.setNeverMoved(neverMoved);
                if (!neverMoved) {
                    newPiece.setTimeWhenFirstMoved(moveTime);
                }
                pieceHashMap.put(location, newPiece);

            }
            if (!passedBoard && line.startsWith("\tPieces:")) {
                passedBoard = true;
            }
        }

        board.setAllBoardItems(pieceHashMap);

        return board;


    }

}
