package sample.game.components;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Main;
import sample.chess.Chess;
import sample.chess.ChessBoard;
import sample.chess.PieceType;
import sample.chess.Team;
import sample.game.Game;
import sample.game.virtualopponent.SimpleAI;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameManager extends AnchorPane {

    Game game;
    AnchorPane anchorPane;

    ImageView toggleChessBoard;
    ImageView toggleMoveTracker;

    ChoiceBox<String> boxWhite;
    ChoiceBox<String> boxBlack;

    ImageView whiteImgView;
    ImageView blackImgView;

    ScheduledExecutorService everySecond;

    public GameManager(Game game) throws IOException {
        this.game = game;
        init();
    }


    private void init() throws IOException {
        this.setOpacity(0);
//        str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
        this.setMaxWidth(421);
        this.anchorPane = FXMLLoader.load(GameManager.class.getResource("assets/gameManager.fxml"));
        this.getChildren().add(this.anchorPane);
        AnchorPane.setLeftAnchor(this.anchorPane, 0.0); AnchorPane.setRightAnchor(this.anchorPane, 0.0);
        AnchorPane.setTopAnchor(this.anchorPane, 0.0); AnchorPane.setBottomAnchor(this.anchorPane, 0.0);
        ImageView topImageView = (ImageView) this.anchorPane.getChildren().get(0); topImageView.setImage(getImageFromPieceInfo(PieceType.QUEEN, this.game.getCurrentTurn()));
        Label gameTime = (Label) this.anchorPane.getChildren().get(1);
        Label turnCounter = (Label) this.anchorPane.getChildren().get(2);
        ImageView back = (ImageView) this.anchorPane.getChildren().get(3);
        Label currentTurn = (Label) this.anchorPane.getChildren().get(4); currentTurn.setText(firstLetterCapital(this.game.getCurrentTurn().toString() + "'s Turn"));
        Label turnLength = (Label) this.anchorPane.getChildren().get(5);
        ImageView concede = (ImageView) this.anchorPane.getChildren().get(6);
        Label blackLength = (Label) this.anchorPane.getChildren().get(7);
        Label whiteLength = (Label) this.anchorPane.getChildren().get(8);
        Label whitePoints = (Label) this.anchorPane.getChildren().get(12);
        Label blackPoints = (Label) this.anchorPane.getChildren().get(11);
        ImageView flipButton = (ImageView) this.anchorPane.getChildren().get(13);
        ImageView soundToggle = (ImageView) this.anchorPane.getChildren().get(15);
        Button showPane = (Button) this.anchorPane.getChildren().get(16);
        AnchorPane pane = (AnchorPane) this.anchorPane.getChildren().get(17);
        ImageView save = (ImageView) this.anchorPane.getChildren().get(18);
        ImageView open = (ImageView) this.anchorPane.getChildren().get(19);
        save.setOnMouseReleased(mouseEvent -> this.game.showSaveDialog());
        open.setOnMouseReleased(mouseEvent -> this.game.showOpenDialog());
        showPane.setOnAction(actionEvent -> {
            pane.setOpacity(0);
            pane.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(new javafx.util.Duration(400), pane);
            fadeIn.setToValue(1);
            fadeIn.play();
            fadeIn.setOnFinished(actionEvent1 -> pane.setOpacity(1));
            pane.setMouseTransparent(false);
        });
        pane.setOnMouseReleased(mouseEvent -> {
            pane.setMouseTransparent(true);
            FadeTransition fadeOut = new FadeTransition(new javafx.util.Duration(275), pane);
            fadeOut.setToValue(0);
            fadeOut.play();
            fadeOut.setOnFinished(actionEvent -> {
                pane.setOpacity(0);
                pane.setVisible(false);
            });
        });
        this.whiteImgView = (ImageView) pane.getChildren().get(0);
        this.blackImgView = (ImageView) pane.getChildren().get(1);
        this.boxWhite = (ChoiceBox<String>) pane.getChildren().get(2);
        this.boxBlack = (ChoiceBox<String>) pane.getChildren().get(3);
        this.boxWhite.getItems().addAll("Human", "Computer");
        if (this.game.getOpponent(Team.WHITE) == null) {
            this.boxWhite.getSelectionModel().select("Human");
        } else {
            this.boxWhite.getSelectionModel().select("Computer");
        }
        this.boxBlack.getItems().addAll("Human", "Computer");
        if (this.game.getOpponent(Team.BLACK) == null) {
            this.boxBlack.getSelectionModel().select("Human");
        } else {
            this.boxBlack.getSelectionModel().select("Computer");
        }
        this.boxBlack.setOnAction(actionEvent -> {
            this.selectBlackOpp(this.boxBlack.getSelectionModel().getSelectedItem());
            if (this.boxBlack.getSelectionModel().getSelectedItem().equals("Computer") && this.game.getOpponent(Team.BLACK) == null) {
                this.game.setOpponent(new SimpleAI(this.game, Team.BLACK, true));
            } else {
                this.game.clearOpponent(Team.BLACK);
            }
        });
        this.boxWhite.setOnAction(actionEvent -> {
            this.selectWhiteOpp(this.boxWhite.getSelectionModel().getSelectedItem());
            if (this.boxWhite.getSelectionModel().getSelectedItem().equals("Computer") && this.game.getOpponent(Team.WHITE) == null) {
                this.game.setOpponent(new SimpleAI(this.game, Team.WHITE, true));
            } else {
                this.game.clearOpponent(Team.WHITE);
            }
        });
        soundToggle.setOnMouseReleased(mouseEvent -> {
            if (this.game.getUseSound()) {
                this.game.setUseSound(false);
                soundToggle.setImage(new Image(Main.class.getResource("Images/sound_off.png").toExternalForm()));
            } else {
                this.game.setUseSound(true);
                soundToggle.setImage(new Image(Main.class.getResource("Images/sound_on.png").toExternalForm()));
            }
        });


        flipButton.setOnMouseReleased(actionEvent -> {
            this.game.getChessBoard().setOrientationWithTransition(Chess.flipTeam(this.game.getChessBoard().getOrientation()), new javafx.util.Duration(650));
            RotateTransition rightFirst = new RotateTransition(new javafx.util.Duration(150), flipButton);
            rightFirst.setToAngle(43);
            RotateTransition thenLeft = new RotateTransition(new javafx.util.Duration(500), flipButton);
            thenLeft.setToAngle(-360);
            SequentialTransition transition = new SequentialTransition(rightFirst, thenLeft);
            transition.play();
            transition.setOnFinished(actionEvent1 -> flipButton.setRotate(0));
        });
        CheckBox flipTurnCheckBox = (CheckBox) this.anchorPane.getChildren().get(14);
        flipTurnCheckBox.setOnAction(actionEvent -> {
            this.game.setRotateGame(flipTurnCheckBox.isSelected());
        });
        whitePoints.setText("Points: " + Chess.getTotalValueForTeam(Team.WHITE, this.game.getChessBoard()));
        blackPoints.setText("Points: " + Chess.getTotalValueForTeam(Team.BLACK, this.game.getChessBoard()));
        this.toggleChessBoard = (ImageView) this.anchorPane.getChildren().get(9);
        this.toggleMoveTracker = (ImageView) this.anchorPane.getChildren().get(10);
        this.toggleChessBoard.setOnMouseReleased(mouseEvent -> {
            this.toggleChessBoard();
        });
        this.toggleMoveTracker.setOnMouseReleased(mouseEvent -> {
            this.toggleMoveTracker();
        });
        Runnable runnable = () -> {
            Platform.runLater(() -> {
                if (this.game.getWinner() == null) {
                    gameTime.setText(formattedDuration(this.game.getGameLength()));
                    turnLength.setText("Turn Length: " + formattedDuration(this.game.getTurnDuration()));
                    if (this.game.getBlackTime() != null) {
                        blackLength.setText("Black Length: " + formattedDuration(this.game.getBlackTime()));
                    }
                    if (this.game.getWhiteTime() != null) {
                        whiteLength.setText("White Length: " + formattedDuration(this.game.getWhiteTime()));
                    }
                }
            });
        };
        this.everySecond = Executors.newScheduledThreadPool(1);
        this.everySecond.scheduleAtFixedRate(runnable, 500, 100, TimeUnit.MILLISECONDS);


        this.game.getNextTurnEventListeners().add((game1, newMove) -> {
            turnCounter.setText("Turns: " + this.game.getMovesCount());
            if (this.game.getCurrentTurn() != null) {
                currentTurn.setText(firstLetterCapital(this.game.getCurrentTurn().toString() + "'s Turn"));
                topImageView.setImage(getImageFromPieceInfo(PieceType.QUEEN, this.game.getCurrentTurn()));
                whitePoints.setText("Points: " + Chess.getTotalValueForTeam(Team.WHITE, this.game.getChessBoard()));
                blackPoints.setText("Points: " + Chess.getTotalValueForTeam(Team.BLACK, this.game.getChessBoard()));
            }
        });

        concede.setOnMouseReleased(mouseEvent -> this.game.endGame(this.game.getCurrentTurn() == Team.WHITE ? Team.BLACK : Team.WHITE));

        back.setOnMouseReleased(mouseEvent -> {
            if (this.game.getMovesCount() > 0) {
                this.game.backwardOneChessBoard();
                this.game.safeRemoveMove(this.game.getMovesCount() - 1);
            }
        });

        Runnable fade = () -> {
            FadeTransition fadeIn = new FadeTransition(new javafx.util.Duration(1000), this);
            fadeIn.setToValue(1);
            fadeIn.play();
            fadeIn.setOnFinished(actionEvent -> this.setOpacity(1));
        };
        if (this.getScene() != null) {
            fade.run();
        } else {
            sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene t1) {
                    Platform.runLater(fade);
                    sceneProperty().removeListener(this);
                }
            });
        }

    }

    private String formattedDuration(Duration duration) {
        return String.format("%02d:%02d.%d",
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                ((int) Math.floor((double) duration.toMillisPart() / 100)));
    }

    private String firstLetterCapital(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return this.everySecond;
    }

    private Image getImageFromPieceInfo(PieceType pieceType, Team team) {
        return new Image(Main.class.getResource("Images/" + team.toString() + "/" + team.toString().toLowerCase() + "_" + pieceType.toString().toLowerCase() + ".png").toExternalForm());
    }

    public void toggleChessBoard() {
        if (this.toggleChessBoard.getOpacity() == 1) {
            this.toggleChessBoard.setOpacity(0.5);
            this.game.getChessBoard().getScene().getWindow().hide();
        } else {
            this.toggleChessBoard.setOpacity(1);
            ((Stage) this.game.getChessBoard().getScene().getWindow()).show();
        }
    }
    public void toggleChessBoard(boolean input) {
        if (input) {
            this.toggleChessBoard.setOpacity(1);
        } else {
            this.toggleChessBoard.setOpacity(0.5);
        }
    }

    public void toggleMoveTracker() {
        if (this.toggleMoveTracker.getOpacity() == 1) {
            this.toggleMoveTracker.setOpacity(0.5);
            this.game.getMoveTracker().getScene().getWindow().hide();
        } else {
            this.toggleMoveTracker.setOpacity(1);
            ((Stage) this.game.getMoveTracker().getScene().getWindow()).show();
        }
    }
    public void toggleMoveTracker(boolean input) {
        if (input) {
            this.toggleMoveTracker.setOpacity(1);
        } else {
            this.toggleMoveTracker.setOpacity(0.5);
        }
    }

    public void selectWhiteOpp(String name) {
        this.boxWhite.getSelectionModel().select(name);
        if (name.equals("Human")) {
            this.whiteImgView.setImage(new Image(Main.class.getResource("Images/person_white.png").toExternalForm()));
        } else if (name.equals("Computer")) {
            this.whiteImgView.setImage(new Image(Main.class.getResource("Images/computer_white.png").toExternalForm()));
        }
    }

    public void selectBlackOpp(String name) {
        this.boxBlack.getSelectionModel().select(name);
        if (name.equals("Human")) {
            this.blackImgView.setImage(new Image(Main.class.getResource("Images/person_black.png").toExternalForm()));
        } else if (name.equals("Computer")) {
            this.blackImgView.setImage(new Image(Main.class.getResource("Images/computer_black.png").toExternalForm()));
        }
    }

}
