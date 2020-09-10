package sample.game.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import sample.Main;
import sample.betterJFX.FxTools;
import sample.betterJFX.ImageTools;
import sample.chess.Chess;
import sample.game.Game;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GameSelector extends AnchorPane {

    AnchorPane topPane;
    VBox contentBox;
    HBox topHBox;
    Label name;
    Game saveGame;
    TextField textField;
    SelectedEvent onClick;
    ArrayList<Node> topDisable = new ArrayList<>();


    public GameSelector(Game saveGame) {
        this.saveGame = saveGame;
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException {
        this.topPane = FXMLLoader.load(GameSelector.class.getResource("assets/gameSelector.fxml"));
        this.getChildren().add(this.topPane);
        FxTools.anchorAll(this.topPane, 0);
        VBox topVBox = (VBox) this.topPane.getChildren().get(0);
        this.topHBox = (HBox) topVBox.getChildren().get(0);
        this.textField = (TextField) this.topHBox.getChildren().get(1);
        this.name = (Label) this.topHBox.getChildren().get(0);
        Button createNew = (Button) this.topHBox.getChildren().get(2);
        this.topDisable.add(createNew); this.topDisable.add(this.textField);
        createNew.setOnAction(actionEvent -> {
            if (this.saveGame != null) {
                if (this.textField.getText().length() > 1) {
                    try {
                        this.saveGame.save(new File(Game.saveFolder().getPath() + "/" + textField.getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.getScene().getWindow().hide();
                }
            } else {
                this.onClick.onFileSelected(new File(Game.saveFolder().getPath() + "/" + textField.getText()));
            }

        });
        Button close = (Button) this.topHBox.getChildren().get(3);
        close.setOnAction(actionEvent -> this.getScene().getWindow().hide());
        ScrollPane scrollPane = (ScrollPane) this.topPane.getChildren().get(1);
        this.contentBox = (VBox) scrollPane.getContent();
        this.populateBox();
    }

    public HBox getTopHBox() {return this.topHBox;}
    public void removeTop() {
        this.topHBox.getChildren().removeAll(this.topDisable);
    }

    public void setName(String input) {
        this.name.setText(input);
    }

    public void setOnClick(SelectedEvent selectedEvent) {
        this.onClick = selectedEvent;
    }

    public void setTextFieldText(String text) {
        this.textField.setText(text);
    }

    private void populateBox() throws IOException {
        File saves = Game.saveFolder();
        if (!saves.exists() || !saves.isDirectory()) {
            return;
        }
        for (File file : Objects.requireNonNull(saves.listFiles())) {
            AnchorPane addPane = FXMLLoader.load(GameSelector.class.getResource("assets/gameSelectorItem.fxml"));
            ((Label) addPane.getChildren().get(0)).setText(file.getName());
            ((Label) addPane.getChildren().get(1)).setText(file.getPath());
            Button openLocation = (Button) addPane.getChildren().get(2);
            openLocation.setOnAction(actionEvent -> {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Button delete = (Button) addPane.getChildren().get(3);
            delete.setOnAction(actionEvent -> {
                if (delete.getGraphic() == null) {
                    this.contentBox.getChildren().remove(addPane);
                    Desktop.getDesktop().moveToTrash(file);
                } else {
                    delete.setAccessibleText("CONFIRMED");
                    delete.setText("Confirm");
                    delete.setGraphic(null);
                }
            });
            if (this.saveGame != null) {
                addPane.setOnMouseReleased(mouseEvent -> {
                    try {
                        this.saveGame.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.getScene().getWindow().hide();
                });
            } else {
                addPane.setOnMouseReleased(mouseEvent -> {
                    this.onClick.onFileSelected(file);
                    this.getScene().getWindow().hide();
                });
            }

            this.contentBox.getChildren().add(addPane);
        }
    }

}
