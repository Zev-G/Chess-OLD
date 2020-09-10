package sample.chess;

import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;
import javafx.util.Duration;
import sample.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ChessPiece extends Button {

    private PieceType pieceType = PieceType.PAWN;
    private Image pieceImage;
    private Team team = Team.WHITE;
    private ArrayList<MoveSequence> moveSequences;
    private ArrayList<ComplexMoveSequence> complexMoveSequences = null;
    private MoveBuilder moveBuilder;
    private ChessBoard chessBoard;
    private int timeWhenFirstMoved;

    private Point2D enPassant = null;
    private Point2D enPassantTake = null;

    private final Popup popup = new Popup();

    private boolean neverMoved = true;

    private double startX;
    private double startY;

    private static final EventHandler<MouseEvent> mouseEntered = mouseEvent -> {
        ChessPiece piece = (ChessPiece) mouseEvent.getSource();
        if (piece.getTeam() == piece.getBoard().getGame().getCurrentTurn()) {
            ScaleTransition scaleTransition = new ScaleTransition(new Duration(175), piece);
            scaleTransition.setToX(0.115);
            scaleTransition.setToY(0.115);
            scaleTransition.play();
        }
    };
    private final EventHandler<MouseEvent> mouseExited = mouseEvent -> {
        if (this.chessBoard.getHighlightedPiece() != this) {
            ChessPiece piece = (ChessPiece) mouseEvent.getSource();
            ScaleTransition scaleTransition = new ScaleTransition(new Duration(175), piece);
            scaleTransition.setToX(0.1);
            scaleTransition.setToY(0.1);
            scaleTransition.play();
        }
    };

    public ChessPiece (Team teamInput, PieceType pieceTypeImput, ChessBoard inputBoard) {
        this.pieceType = pieceTypeImput;
        this.team = teamInput;
        this.chessBoard = inputBoard;
        init();
        setImage();
    }
    public ChessPiece(PieceType input, ChessBoard inputBoard) {
        this.pieceType = input;
        this.chessBoard = inputBoard;
        init();
        setImage();
    }
    public ChessPiece(Team input, ChessBoard inputBoard) {
        this.team = input;
        this.chessBoard = inputBoard;
        init();
        setImage();
    }
    public ChessPiece(ChessBoard inputBoard) {
        this.chessBoard = inputBoard;
        init();
        setImage();
    }
    public Point2D getLocation() {
        return this.chessBoard.getLocFromPiece(this);
    }
    public Team getTeam() { return this.team; }
    public PieceType getPieceType() {return this.pieceType;}
    public ChessBoard getBoard() {return this.chessBoard;}
    public boolean getNeverMoved() {return this.neverMoved;}
    public void setNeverMoved(boolean input) {
        if (this.neverMoved && !input && this.chessBoard.getGame() != null) {
            this.timeWhenFirstMoved = this.chessBoard.getGame().getMovesCount();
        }
        this.neverMoved = input;
    }
    public void setTimeWhenFirstMoved(int timeWhenFirstMoved) {
        this.timeWhenFirstMoved = timeWhenFirstMoved;
    }

    public MoveBuilder getMoveBuilder() {return this.moveBuilder;}
    public int getTimeWhenFirstMoved() {
        return this.timeWhenFirstMoved;
    }

    private void init() {
        this.moveSequences = new ArrayList<>();
        this.setStyle("-fx-background-color: transparent");
        this.setScaleX(0.1);
        this.setScaleY(0.1);
        this.setOnMouseEntered(mouseEntered);
        this.setOnMouseExited(mouseExited);
        this.setCursor(Cursor.HAND);
        if (this.pieceType == PieceType.PAWN) {
            if (this.team == Team.WHITE) {
                this.moveSequences.add(new MoveSequence(false, new Point2D(0, -1), false, false));
                this.moveSequences.add(new MoveSequence(false, true, new Point2D(1, -1)));
                this.moveSequences.add(new MoveSequence(false, true, new Point2D(-1, -1)));
                this.moveSequences.add(new MoveSequence(false, new Point2D(0, -2), true, false));
            } else {
                this.moveSequences.add(new MoveSequence(false, new Point2D(0, 1), false, false));
                this.moveSequences.add(new MoveSequence(false, true, new Point2D(1, 1)));
                this.moveSequences.add(new MoveSequence(false, true, new Point2D(-1, 1)));
                this.moveSequences.add(new MoveSequence(false, new Point2D(0, 2), true, false));
            }
        } else if (this.pieceType == PieceType.ROOK) {
            this.moveSequences.add(new MoveSequence(true, new Point2D(1,0)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,0)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(0,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(0,-1)));
        } else if (this.pieceType == PieceType.BISHOP) {
            this.moveSequences.add(new MoveSequence(true, new Point2D(1,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(1,-1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,-1)));
        } else if (this.pieceType == PieceType.KNIGHT) {
            this.moveSequences.add(new MoveSequence(false, new Point2D(2,1)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(1,2)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-1,2)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-2,1)));
            //negative
            this.moveSequences.add(new MoveSequence(false, new Point2D(2,-1)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(1,-2)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-1,-2)));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-2,-1)));
        } else if (this.pieceType == PieceType.QUEEN) {
            this.moveSequences.add(new MoveSequence(true, new Point2D(1,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(1,-1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,-1)));

            this.moveSequences.add(new MoveSequence(true, new Point2D(1,0)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(-1,0)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(0,1)));
            this.moveSequences.add(new MoveSequence(true, new Point2D(0,-1)));
        } else if (this.pieceType == PieceType.KING) {
            this.moveSequences.add(new MoveSequence(false, new Point2D(1,1), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-1,1), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(1,-1), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-1,-1), false, true));

            this.moveSequences.add(new MoveSequence(false, new Point2D(1,0), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(-1,0), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(0,1), false, true));
            this.moveSequences.add(new MoveSequence(false, new Point2D(0,-1), false, true));

            this.complexMoveSequences = new ArrayList<>();
            if (this.team == Team.WHITE) {
                this.complexMoveSequences.add(new ComplexMoveSequence(new MoveSequence(false, new Point2D(-2, 0), true),
                        this,
                        this.chessBoard.getComplexSpot(3),
                        new Point2D(3, 7), true, true));
                this.complexMoveSequences.add(new ComplexMoveSequence(new MoveSequence(false, new Point2D(2, 0), true),
                        this,
                        this.chessBoard.getComplexSpot(4),
                        new Point2D(5, 7), true, true));
            } else {
                this.complexMoveSequences.add(new ComplexMoveSequence(new MoveSequence(false, new Point2D(-2, 0), true),
                        this,
                        this.chessBoard.getComplexSpot(1),
                        new Point2D(3, 0), true, true));
                this.complexMoveSequences.add(new ComplexMoveSequence(new MoveSequence(false, new Point2D(2, 0), true),
                        this,
                        this.chessBoard.getComplexSpot(2),
                        new Point2D(5, 0), true, true));
            }
        }
        this.moveBuilder = new MoveBuilder(this.chessBoard, this, this.moveSequences, this.complexMoveSequences);


        ArrayList<Point2D> moves = new ArrayList<>();
        HashMap<ComplexMoveSequence, Point2D> complexMoveSequence = new HashMap<>();
        AtomicReference<Point2D> enPassant = new AtomicReference<>();
        this.setOnAction(actionEvent -> {
            if (this.chessBoard.getGame() == null || this.chessBoard.getGame().getCurrentTurn() == this.team) {
                moves.clear();
                this.chessBoard.resetMoveButtons();
                this.chessBoard.setHighlightedPiece(this);
                ((BoardSpot) this.getParent()).setBackground(new Background(new BackgroundFill(Paint.valueOf("#863eab"), CornerRadii.EMPTY, Insets.EMPTY)));
                for (Point2D currentMove : this.moveBuilder.getMoves()) {
                    MoveButton newButton = new MoveButton(this, currentMove, this.chessBoard);
                    newButton.addToAnchorPane();
                }
                for (Map.Entry<ComplexMoveSequence, Point2D> entry : this.moveBuilder.getComplexMoves().entrySet()) {
                    if (entry.getValue() != null && entry.getKey() != null) {
                        MoveButton newButton = new MoveButton(this, entry.getKey(), entry.getValue());
                        newButton.addToAnchorPane();
                    }
                }
                if (this.pieceType == PieceType.PAWN) {
                    Point2D enPassantMove = this.moveBuilder.getEnPassant();
                    if (enPassantMove != null) {
                        MoveButton newButton = new MoveButton(this, enPassantMove, this.chessBoard, this.enPassantTake);
                        newButton.addToAnchorPane();
                    }
                }
            }
        });

        this.setOnDragDetected(mouseEvent -> {
            if (this.chessBoard.getGame() != null && this.chessBoard.getGame().getCurrentTurn() == this.team) {
                moves.clear();
                moves.addAll(moveBuilder.getMoves());
                complexMoveSequence.clear();
                complexMoveSequence.putAll(moveBuilder.getComplexMoves());
                enPassant.set(moveBuilder.getEnPassant());
                ArrayList<Point2D> allMoves = new ArrayList<>();
                allMoves.addAll(moves);
                allMoves.addAll(complexMoveSequence.values());
                allMoves.add(enPassant.get());
                for (Point2D highlight : allMoves) {
                    BoardSpot spot = this.chessBoard.getSpotFromLoc(highlight);
                    if (spot != null) {
                        if (spot.getPiece() != null) {
                            spot.setBackground(new Background(new BackgroundFill(Color.valueOf("#db3832"), CornerRadii.EMPTY, Insets.EMPTY)));
                        } else {
                            spot.setBackground(new Background(new BackgroundFill(Color.valueOf("#254385"), CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                }
                Bounds screenBounds = this.localToScreen(this.getBoundsInLocal());
                startX = mouseEvent.getScreenX() - screenBounds.getCenterX();
                startY = mouseEvent.getScreenY() - screenBounds.getCenterY();
                System.out.println("Startx: " + startX + "StartY: " + startY);
                popup.show(this.getScene().getWindow());
                this.setVisible(false);
            }
        });
        this.setOnMouseDragged(mouseEvent -> {
            popup.setX(mouseEvent.getScreenX() - startX - 10);
            popup.setY(mouseEvent.getScreenY() - startY - 20);
        });
        this.setOnMouseReleased(mouseEvent -> {
            popup.hide();
            this.setVisible(true);
            if (!moves.isEmpty()) {
                if (this.chessBoard.getGame().getCurrentTurn() == this.team) {
                    this.chessBoard.resetMoveButtons();
                    System.out.println(mouseEvent.getPickResult().getIntersectedNode());
                    moves.clear();
                    moves.addAll(moveBuilder.getMoves());
                    complexMoveSequence.clear();
                    complexMoveSequence.putAll(moveBuilder.getComplexMoves());
                    enPassant.set(moveBuilder.getEnPassant());
                    if (mouseEvent.getPickResult().getIntersectedNode() instanceof BoardSpot || mouseEvent.getPickResult().getIntersectedNode() instanceof ChessPiece) {
                        BoardSpot boardSpot = null;
                        if (mouseEvent.getPickResult().getIntersectedNode() instanceof BoardSpot) {
                            boardSpot = (BoardSpot) mouseEvent.getPickResult().getIntersectedNode();
                        } else {
                            boardSpot = this.chessBoard.getSpotFromLoc(((ChessPiece) mouseEvent.getPickResult().getIntersectedNode()).getLocation());
                        }
                        Point2D spot = chessBoard.getLocFromSpot(boardSpot);
                        if (moves.contains(spot)) {
                            this.chessBoard.moveChessPiece(this, this.getLocation(), spot);
                        } else if (complexMoveSequence.containsValue(spot)) {
                            AtomicReference<ComplexMoveSequence> moveSequence = new AtomicReference<>();
                            complexMoveSequence.forEach((complexMoveSequence1, point2D) -> {
                                if (point2D == spot) {
                                    moveSequence.set(complexMoveSequence1);
                                }
                            });
                            if (moveSequence.get() != null) {
                                this.chessBoard.moveChessPiece(this, this.getLocation(), moveSequence.get().getMoveSequence().getPoint(), false, false);
                                this.chessBoard.moveChessPiece(moveSequence.get().getSecondPiece(), moveSequence.get().getSecondPiece().getLocation(), moveSequence.get().getMoveSecondTo());
                            }
                        } else if (spot.equals(enPassant.get())) {
//                        this.chessBoard.moveChessPiece(this, this.getLocation(), spot);
//                        if (this.pieceType == PieceType.PAWN) {
//                            Point2D enPassantMove = this.moveBuilder.getEnPassant();
//                            if (enPassantMove != null) {
//                                this.chessBoard.moveChessPiece(this.chessBoard.getSpotFromLoc(this.enPassantTake).getPiece(), this.enPassantTake, null, false, true);
//                            }
//                        }
                        }
                    }
                }
            }
        });
    }

    private void setImage() {
        this.pieceImage = new Image(Main.class.getResource(("Images/" + this.team.toString() + "/" + this.team.toString().toLowerCase() + "_" + this.pieceType.toString().toLowerCase() + ".png")).toExternalForm());
        this.setGraphic(new ImageView(this.pieceImage));
        ImageView imageView = new ImageView(this.pieceImage);
        imageView.setFitWidth(this.pieceImage.getWidth() / 10);
        imageView.setFitHeight(this.pieceImage.getHeight() / 10);
        popup.getContent().add(imageView);
    }

    public void setEnPassant(Point2D enPassant) {this.enPassant = enPassant;}
    public Point2D getEnPassant() {return this.enPassant;}
    public void setEnPassantTake(Point2D enPassantTake) { this.enPassantTake = enPassantTake; }
    public Point2D getEnPassantTake() { return this.enPassantTake; }

    public ArrayList<ComplexMoveSequence> getComplexMoveSequences() {
        return this.complexMoveSequences;
    }
    public ArrayList<MoveSequence> getMoveSequences() {
        return this.moveSequences;
    }
    public Image getPieceImage() {
        return this.pieceImage;
    }
}
