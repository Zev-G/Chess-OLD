package sample.game.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;
import sample.chess.Team;
import sample.game.Game;

import java.io.IOException;
import java.time.Duration;

public class WinnerPopup extends Stage {

    private double xOffset = 0;
    private double yOffset = 0;

    public WinnerPopup(Game game) throws IOException {
        AnchorPane winnerPane = FXMLLoader.load(WinnerPopup.class.getResource("assets/winner.fxml"));
        if (game.getWinner() == Team.WHITE) {
            ((ImageView) winnerPane.getChildren().get(0)).setImage(new Image(Main.class.getResource("Images/WHITE/white_queen.png").toExternalForm()));
            ((Label) winnerPane.getChildren().get(1)).setText("White Won!");
        } else if (game.getWinner() == Team.BLACK) {
            ((ImageView) winnerPane.getChildren().get(0)).setImage(new Image(Main.class.getResource("Images/BLACK/BLACK_queen.png").toExternalForm()));
            ((Label) winnerPane.getChildren().get(1)).setText("Black Won!");
        } else {
            ((ImageView) winnerPane.getChildren().get(0)).setImage(new Image(Main.class.getResource("Images/draw.png").toExternalForm()));
            ((Label) winnerPane.getChildren().get(1)).setText("Stalemate!");
        }
        Duration diff = game.getGameLength();
        String gameLength = String.format("%02d:%02d",
                diff.toMinutesPart(),
                diff.toSecondsPart());
        ((Label) winnerPane.getChildren().get(2)).setText("Game Length: " + gameLength);
        ((Label) winnerPane.getChildren().get(3)).setText("Num of Moves: " + game.getMovesCount());

        Button newGame = (Button) winnerPane.getChildren().get(4);
        newGame.setOnAction(actionEvent -> {
            this.hide();
            game.restart();
        });

        Scene winnerScene = new Scene(winnerPane);
        winnerScene.setFill(null);
        this.setScene(winnerScene);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);

        winnerPane.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        winnerPane.setOnMouseDragged(mouseEvent -> {
            this.setX(mouseEvent.getScreenX() - xOffset);
            this.setY(mouseEvent.getScreenY() - yOffset);
        });


    }

}
