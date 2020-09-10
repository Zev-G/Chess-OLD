package sample.game.components;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import sample.Main;
import sample.betterJFX.nicebutton.NiceButton;
import sample.chess.ChessPiece;
import sample.chess.PieceType;
import sample.chess.Team;

public class Promotion extends GridPane {

    ChessPiece piece;
    Team team;

    public Promotion(ChessPiece toPromote) {
        this.piece = toPromote;
        this.team = this.piece.getTeam();
        init();
    }

    private void init() {
        this.setMaxSize(250, 1000);
        this.setMinSize(250, 1000);
        String string = "Images/" + (this.piece.getTeam() == Team.WHITE ? "WHITE" : "BLACK") + "/" + (this.piece.getTeam() == Team.WHITE ? "white_" : "black_");
        ImageView queenImgView = new ImageView(new Image(Main.class.getResource(string + "queen.png").toExternalForm())); queenImgView.setFitWidth(100); queenImgView.setFitHeight(100);
        ImageView rookImgView = new ImageView(new Image(Main.class.getResource(string + "rook.png").toExternalForm())); rookImgView.setFitWidth(100); rookImgView.setFitHeight(100);
        ImageView bishopImgView = new ImageView(new Image(Main.class.getResource(string + "bishop.png").toExternalForm())); bishopImgView.setFitWidth(100); bishopImgView.setFitHeight(100);
        ImageView knightImgView = new ImageView(new Image(Main.class.getResource(string + "knight.png").toExternalForm())); knightImgView.setFitWidth(100); knightImgView.setFitHeight(100);
        NiceButton queen = new NiceButton("", queenImgView);
        queen.setOnAction(actionEvent -> {
            if (this.piece.getBoard().getGame() != null) {
                this.piece.getBoard().getGame().promotedPressed(PieceType.QUEEN, this.piece);
            }
        });
        NiceButton rook = new NiceButton("", rookImgView);
        rook.setOnAction(actionEvent -> {
            if (this.piece.getBoard().getGame() != null) {
                this.piece.getBoard().getGame().promotedPressed(PieceType.ROOK, this.piece);
            }
        });
        NiceButton bishop = new NiceButton("", bishopImgView);
        bishop.setOnAction(actionEvent -> {
            if (this.piece.getBoard().getGame() != null) {
                this.piece.getBoard().getGame().promotedPressed(PieceType.BISHOP, this.piece);
            }
        });
        NiceButton knight = new NiceButton("", knightImgView);
        knight.setOnAction(actionEvent -> {
            if (this.piece.getBoard().getGame() != null) {
                this.piece.getBoard().getGame().promotedPressed(PieceType.KNIGHT, this.piece);
            }
        });
        this.add(queen, 0, 0);
        this.add(rook, 0, 1);
        this.add(bishop, 0, 2);
        this.add(knight, 0, 3);
        this.setBackground(new Background(new BackgroundFill(Paint.valueOf("#858585"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public Team getTeam() {
        return this.team;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }
}
