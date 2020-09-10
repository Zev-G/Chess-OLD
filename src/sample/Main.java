package sample;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import sample.chess.ChessBoard;
import sample.chess.Team;
import sample.game.Game;
import sample.betterJFX.prettystage.PrettyStage;
import sample.game.virtualopponent.MinimaxAI;

import java.io.IOException;

public class Main extends Application {

    private ChessBoard mainChessBoard = new ChessBoard(8, 8);
    private final Game game = new Game(mainChessBoard);

    @Override
    public void start(Stage primaryStage) {

        PrettyStage moveTrackerStage = new PrettyStage(Paint.valueOf("#666666") , game.getMoveTracker());
        moveTrackerStage.getPinButton().fire();
        game.setMoveTrackerStage(moveTrackerStage);
        moveTrackerStage.setIcon(new Image(Main.class.getResource("Images/draw.png").toExternalForm()));
        moveTrackerStage.setWidth(350);
        moveTrackerStage.setText("Move Tracker");


        PrettyStage prettyStage = new PrettyStage(Paint.valueOf("#81b6e3"), mainChessBoard);
        prettyStage.getPinButton().fire();
        game.setChessBoardStage(prettyStage);
        prettyStage.setIcon(new Image(Main.class.getResource("Images/BLACK/black_rook.png").toExternalForm()));
        prettyStage.setText("Chessboard");

        PrettyStage gameManagerStage = new PrettyStage(Paint.valueOf("#9ec5e6"), game.getGameManager());
        gameManagerStage.setIcon(new Image(Main.class.getResource("Images/WHITE/white_queen.png").toExternalForm()));
        gameManagerStage.getPinButton().fire();
        gameManagerStage.setWidth(424);

        gameManagerStage.setText("Game Dashboard");
        gameManagerStage.setOnHidden(windowEvent -> {
            System.exit(0);
        });


        moveTrackerStage.show();
        prettyStage.show();
        gameManagerStage.show();
        moveTrackerStage.setX(moveTrackerStage.getX() + 100);
        prettyStage.setX(prettyStage.getX() - 500);
        gameManagerStage.setX(gameManagerStage.getX() + 500);




//        game.initOpponent(new SimpleAI(game, Team.BLACK, true));
//        game.setOpponent(new SimpleAI(game, Team.WHITE, true));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
