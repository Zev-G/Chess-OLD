package sample.betterJFX.prettystage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;

public class PrettyStage extends Stage {

    AnchorPane topPane;
    Paint color;
    Pane addPane = null;
    ScrollPane addPane2nd = null;
    Label label;
    Button pinWindow;
    Image icon;
    ImageView iconImgView;
    Button closeWindow;

    private double xOffset = 0;
    private double yOffset = 0;

    public PrettyStage(Paint color, Pane add) {
        this.color = color;
        this.addPane = add;
        init();
    }
    public PrettyStage(Paint color, ScrollPane add) {
        this.color = color;
        this.addPane2nd = add;
        init();
    }

    private void init() {
        if (this.addPane != null) {
            this.topPane = new AnchorPane(this.addPane);
        } else if (this.addPane2nd != null) {
            this.topPane = new AnchorPane(this.addPane2nd);
        }


        this.label = new Label();
        AnchorPane.setLeftAnchor(this.topPane.getChildren().get(0), 0.0); AnchorPane.setRightAnchor(this.topPane.getChildren().get(0), 0.0);
        AnchorPane.setTopAnchor(this.topPane.getChildren().get(0), 33.0); AnchorPane.setBottomAnchor(this.topPane.getChildren().get(0), 0.0);

        ImageView closeWindowImgView = new ImageView(new Image(Main.class.getResource("Images/closeButton.png").toExternalForm()));
        this.closeWindow = new Button("", closeWindowImgView); closeWindow.setScaleY(0.135); closeWindow.setScaleX(0.135);
        ImageView pinImgView = new ImageView(new Image(Main.class.getResource("Images/pinButtonOff.png").toExternalForm()));
        ImageView pinImgViewOn = new ImageView(new Image(Main.class.getResource("Images/pinButton.png").toExternalForm()));
        this.pinWindow = new Button("", pinImgView); this.pinWindow.setScaleX(0.04); this.pinWindow.setScaleY(0.04); this.pinWindow.setOpacity(0.6);
        this.pinWindow.setBackground(new Background(new BackgroundFill(null, new CornerRadii(0), Insets.EMPTY)));
        this.closeWindow.setBackground(new Background(new BackgroundFill(null, new CornerRadii(0), Insets.EMPTY)));
        this.closeWindow.setOnMouseEntered(mouseEvent -> closeWindow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ff2121"), new CornerRadii(0), Insets.EMPTY))));
        this.closeWindow.setOnMouseExited(mouseEvent -> closeWindow.setBackground(new Background(new BackgroundFill(null, new CornerRadii(0), Insets.EMPTY))));
        this.pinWindow.setOnMouseEntered(mouseEvent -> this.pinWindow.setOpacity(1));
        this.pinWindow.setOnMouseExited(mouseEvent -> this.pinWindow.setOpacity(0.6));
        this.pinWindow.setOnAction(actionEvent -> {
            if (!this.isAlwaysOnTop()) {
                this.setAlwaysOnTop(true);
                this.pinWindow.setGraphic(pinImgViewOn);
            } else {
                this.setAlwaysOnTop(false);
                this.pinWindow.setGraphic(pinImgView);
            }
        });
        this.closeWindow.setOnAction(actionEvent -> this.close());
        this.topPane.getChildren().addAll(closeWindow, pinWindow, this.label);
        this.label.setStyle("-fx-background-color: transparent;");
        this.label.setMouseTransparent(true); this.label.setFont(new Font(20));
        this.label.setTextFill(Paint.valueOf("#212121"));
        AnchorPane.setRightAnchor(closeWindow, -77.0); AnchorPane.setTopAnchor(closeWindow, -85.0);
        AnchorPane.setRightAnchor(this.pinWindow, -373.0); AnchorPane.setTopAnchor(this.pinWindow, -437.0);

        AnchorPane.setTopAnchor(this.label, 3.0); AnchorPane.setLeftAnchor(this.label, 8.0); AnchorPane.setRightAnchor(this.label, 0.0);


        this.topPane.setBackground(new Background(new BackgroundFill(this.color, new CornerRadii(15), Insets.EMPTY)));
        this.topPane.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        this.topPane.setOnMouseDragged(mouseEvent -> {
            this.setX(mouseEvent.getScreenX() - xOffset);
            this.setY(mouseEvent.getScreenY() - yOffset);
        });

        Scene scene = new Scene(this.topPane);
        scene.setFill(null);
        this.setScene(scene);
//        this.setResizable(false);
        this.initStyle(StageStyle.TRANSPARENT);
    }

    public void setText(String text) {
        this.label.setText(text);
        this.setTitle(text);
    }
    public Label getLabel() {
        return this.label;
    }
    public void setPane(Pane input) {
        this.addPane2nd = null;
        this.addPane = input;
        this.topPane.getChildren().set(0, input);
        AnchorPane.setLeftAnchor(this.topPane.getChildren().get(0), 0.0); AnchorPane.setRightAnchor(this.topPane.getChildren().get(0), 0.0);
        AnchorPane.setTopAnchor(this.topPane.getChildren().get(0), 33.0); AnchorPane.setBottomAnchor(this.topPane.getChildren().get(0), 0.0);
    }
    public void setScrollPane(ScrollPane input) {
        this.addPane2nd = input;
        this.addPane = null;
        this.topPane.getChildren().set(0, input);
        AnchorPane.setLeftAnchor(this.topPane.getChildren().get(0), 0.0); AnchorPane.setRightAnchor(this.topPane.getChildren().get(0), 0.0);
        AnchorPane.setTopAnchor(this.topPane.getChildren().get(0), 33.0); AnchorPane.setBottomAnchor(this.topPane.getChildren().get(0), 0.0);
    }
    public void setIcon(Image img) {

        if (img == null) {
            if (this.icon != null) {
                this.icon = null;
                this.getIcons().clear();
                this.topPane.getChildren().remove(this.iconImgView);
                AnchorPane.setLeftAnchor(this.label, 8.0);
            }
        } else {
            this.icon = img;
            this.getIcons().add(img);
            this.iconImgView = new ImageView(img);
            this.iconImgView.setFitHeight(25);
            this.iconImgView.setFitWidth(25);
            this.topPane.getChildren().add(this.iconImgView);

            AnchorPane.setLeftAnchor(this.iconImgView, 5.0); AnchorPane.setTopAnchor(this.iconImgView, 5.0);
            AnchorPane.setLeftAnchor(this.label, 33.0);
        }
    }

    public Button getPinButton() {
        return this.pinWindow;
    }
    public Button getCloseButton() {
        return this.closeWindow;
    }

    public Pane getAddPane() { return this.addPane; }

}
