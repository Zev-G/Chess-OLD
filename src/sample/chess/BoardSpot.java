package sample.chess;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class BoardSpot extends AnchorPane {

    private final Background lightBg = new Background(new BackgroundFill(Paint.valueOf("#add5f7"), CornerRadii.EMPTY, Insets.EMPTY));
    private final Background darkBg = new Background(new BackgroundFill(Paint.valueOf("#99bfe0"), CornerRadii.EMPTY, Insets.EMPTY));
//    private final Background lightBg = new Background(new BackgroundFill(Paint.valueOf("#f5f2f2"), CornerRadii.EMPTY, Insets.EMPTY));
//    private final Background darkBg = new Background(new BackgroundFill(Paint.valueOf("#171717"), CornerRadii.EMPTY, Insets.EMPTY));
    private Background chosenBg;
    private ChessPiece pieceOnSpot;

    private int duration = 1000;

    public void setLight(boolean light) {
        if (light) {
            this.setBackground(lightBg);
        } else {
            this.setBackground(darkBg);
        }
        this.chosenBg = this.getBackground();
    }
    public void restoreBg() {
        this.setBackground(this.chosenBg);
    }

    public void setPiece(ChessPiece chessPiece) {

        this.getChildren().remove(this.pieceOnSpot);
        if (chessPiece != null) {
            this.pieceOnSpot = chessPiece;
            Runnable fire = () -> {
                chessPiece.setOpacity(0);
                if (this.getChildren().size() > 0) {
                    this.getChildren().set(0, chessPiece);
                } else {
                    this.getChildren().add(chessPiece);
                }
                FadeTransition fadeIn = new FadeTransition(new Duration(duration), chessPiece);
                fadeIn.setToValue(1);
                fadeIn.play();
                fadeIn.setOnFinished(actionEvent -> chessPiece.setOpacity(1));
                AnchorPane.setLeftAnchor(chessPiece, 0.0);
                AnchorPane.setRightAnchor(chessPiece, 0.0);
                AnchorPane.setTopAnchor(chessPiece, 0.0);
                AnchorPane.setBottomAnchor(chessPiece, 0.0);
            };
            if (this.getScene() != null) {
                duration = 300;
                fire.run();
            } else {
                this.sceneProperty().addListener(new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene t1) {
                        Platform.runLater(fire);
                        sceneProperty().removeListener(this);
                    }
                });
            }
        } else {
            this.pieceOnSpot = null;
        }
    }
    public void setVirtualPiece(ChessPiece chessPiece) {
        this.pieceOnSpot = chessPiece;
    }
    public ChessPiece getPiece() {
        return this.pieceOnSpot;
    }

    public BoardSpot(boolean light) {
        this.setMinSize(50, 50);
        this.setPrefSize(100, 100);
        this.setMaxSize(200, 200);
        setLight(light);
        setEffect(new DropShadow());

        this.setOnMouseReleased(mouseEvent -> {
            if (this.getPiece() == null) {
                if (this.getChildren().size() == 0) {
                    ((ChessBoard) this.getParent()).resetMoveButtons();
                } else if (this.getChildren().get(0).getClass() == MoveButton.class) {
                    ((MoveButton) this.getChildren().get(0)).fire();
                }
            } else {
                this.getPiece().fire();
            }
        });
    }

    public Background getChosenBg() {
        return chosenBg;
    }
}
