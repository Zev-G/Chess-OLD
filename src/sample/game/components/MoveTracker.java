package sample.game.components;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.util.Duration;
import sample.Main;
import sample.betterJFX.prettystage.PrettyStage;
import sample.game.Game;
import sample.game.Move;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MoveTracker extends AnchorPane {

    Game game;
    VBox vBox;
    ScrollPane scrollPane;
    ImageView boardView;
    ArrayList<ImageView> boardImages = new ArrayList<>();
    HashMap<Image, String> formattedInfo = new HashMap<>();
    int moves = 0;

    public MoveTracker(Game inputGame) {
        this.scrollPane = new ScrollPane();
        this.boardView = new ImageView();
        this.boardView.setFitWidth(200); this.boardView.setFitHeight(200); boardView.setOpacity(0.8); boardView.setMouseTransparent(true);
        this.game = inputGame;
        this.setMaxSize(400, 800);
        this.setPrefSize(350, 600);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setPannable(true);
        this.vBox = new VBox();
        this.scrollPane.setContent(this.vBox);
        this.scrollPane.setFitToWidth(true); this.scrollPane.setFitToHeight(true);
        this.scrollPane.setStyle("-fx-border-color: #383838; -fx-background-color: #383838;");
        this.vBox.setStyle("-fx-background-color: #383838;");
        this.getChildren().add(this.scrollPane);
        AnchorPane.setRightAnchor(this.scrollPane, 0.0); AnchorPane.setLeftAnchor(this.scrollPane, 0.0);
        AnchorPane.setBottomAnchor(this.scrollPane, 0.0); AnchorPane.setTopAnchor(this.scrollPane, 0.0);
        this.getChildren().add(this.boardView);
        double rightAndLeft = 75;
        AnchorPane.setRightAnchor(this.boardView, rightAndLeft); AnchorPane.setLeftAnchor(this.boardView, rightAndLeft);
        AnchorPane.setBottomAnchor(this.boardView, 30.0);
    }

    public int getMoves() {
        return this.moves;
    }
    public void addMove(Move input) throws IOException {
        AnchorPane addPane = FXMLLoader.load(MoveTracker.class.getResource("assets/move.fxml"));
        Image mainImage = new Image(Main.class.getResource(("Images/" + input.getPiece().getTeam().toString() + "/" +input.getPiece().getTeam().toString().toLowerCase() + "_" + input.getPiece().getPieceType().toString().toLowerCase() + ".png")).toExternalForm());
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage boardImg = this.game.getChessBoard().snapshot(sp, null);
        ImageView addBoardImg = new ImageView(boardImg);
        this.boardImages.add(addBoardImg);
        ImageView firstChild = (ImageView) addPane.getChildren().get(0);
        addPane.accessibleHelpProperty().addListener((observableValue, s, t1) -> {
            if (addPane.getAccessibleHelp().equals("REMOVE-BOARD_IMG")) {
                this.boardImages.remove(addBoardImg);
            }
        });
        if (input.getTakePiece() == null) {
            firstChild.setImage(mainImage);
        } else {
            ImageView firstImg = new ImageView(mainImage);
            AnchorPane imgMaker = new AnchorPane(firstImg); imgMaker.setMaxSize(48, 39); imgMaker.setMinSize(48, 39);
            ImageView secondImg = new ImageView( new Image(Main.class.getResource("Images/" + input.getTakePiece().getTeam().toString() + "/" + input.getTakePiece().getTeam().toString().toLowerCase() + "_" + input.getTakePiece().getPieceType().toString().toLowerCase() + ".png").toExternalForm()));
            imgMaker.getChildren().add(secondImg); secondImg.setScaleX(-0.7); secondImg.setScaleY(0.7);
            firstChild.setLayoutX(-50);
            secondImg.setLayoutX(360); secondImg.setLayoutY(160);

            firstImg.toFront();
            WritableImage writableImage = imgMaker.snapshot(sp, null);
            firstChild.setImage(writableImage);
        }
        firstChild.setOnMouseEntered(mouseEvent -> {
            this.boardView.setImage(boardImg);
            this.boardView.setVisible(true);
        });
        firstChild.setOnMouseExited(mouseEvent -> {
            this.boardView.setVisible(false);
        });
        firstChild.setOnMousePressed(mouseEvent -> {
            ImageView popUpImageView = new ImageView(boardImg); popUpImageView.setFitHeight(500); popUpImageView.setFitWidth(500);
            AnchorPane popUpPane = new AnchorPane(popUpImageView);
            final Integer[] index = {this.boardImages.indexOf(addBoardImg)};
            PrettyStage popUp = new PrettyStage(Paint.valueOf("#863eab"), popUpPane); popUp.initOwner(this.getScene().getWindow()); popUp.initModality(Modality.WINDOW_MODAL);
            popUp.getScene().setOnKeyReleased(keyEvent -> {

//                index[0] = this.boardImages.indexOf(addBoardImg);
//                System.out.println(index[0]);

                if (keyEvent.getCode() == KeyCode.LEFT) {
                    if (index[0] - 1 >= 0) {
                        index[0]--;
                        popUpImageView.setImage(this.boardImages.get(index[0]).getImage());
                    }
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    if (this.boardImages.size() > index[0] + 1) {
                        index[0]++;
                        popUpImageView.setImage(this.boardImages.get(index[0]).getImage());
                    }
                }
                popUp.setText("Snapshot Window (← →) " + (index[0] + 1));

            });
            AnchorPane.setLeftAnchor(popUpImageView, 0.0); AnchorPane.setRightAnchor(popUpImageView, 0.0);
            AnchorPane.setTopAnchor(popUpImageView, 0.0); AnchorPane.setBottomAnchor(popUpImageView, 0.0);
            popUp.setText("Snapshot Window (← →) " + (index[0] + 1));
            popUp.show();
        });

        String str = input.getPiece().getPieceType().toString();
        ((Label) addPane.getChildren().get(1)).setText("(" + (this.moves + 1) + ") " + str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() + " from " + stringFromMove(input)[0] + " to " + stringFromMove(input)[1]);
        this.vBox.getChildren().add(addPane);
        addPane.toBack();
        addPane.setLayoutY(52);
        moves++;
        this.scrollPane.setVvalue(0);
        addPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(new Duration(450), addPane);
        fadeIn.setToValue(1);
        fadeIn.play();
        fadeIn.setOnFinished(actionEvent -> addPane.setOpacity(1));
    }
    public String[] stringFromMove(Move input) {
        String[] strings = new String[2];
        int i = (int) (input.getFrom().getX() + 1);
        int b = (int) (input.getTo().getX() + 1);
        String loc1 = i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
        String loc2 = b > 0 && b < 27 ? String.valueOf((char)(b + 64)) : null;
        strings[0] = loc1 + ((int) (this.game.getChessBoard().getBoardHeight() - input.getFrom().getY()));
        strings[1] = loc2 + ((int) (this.game.getChessBoard().getBoardHeight() - input.getTo().getY()));
        return strings;
    }

    public void safeRemoveLast() { safeRemove(this.getChildren().size() - 1); }
    public void safeRemove(int index) {
        if (index < 0) { return; }
        if (this.getChildren().size() >= index) {
            this.vBox.getChildren().get(index).setAccessibleHelp("REMOVE-BOARD_IMG");
            this.vBox.getChildren().remove(index);
            this.moves--;
        }
    }
    public ArrayList<ImageView> getBoardImages() {
        return this.boardImages;
    }
}
