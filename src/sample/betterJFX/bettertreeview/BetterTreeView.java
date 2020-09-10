package sample.betterJFX.bettertreeview;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import sample.betterJFX.ImageTools;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class BetterTreeView extends TitledPane {

    protected File file;
    private HashMap<String, TitledPane> titledPaneHashMap = new HashMap<>();
    private HashMap<String, Button> buttonHashMap = new HashMap<>();

    public BetterTreeView(File file) {
        this.file = file;
        init();
    }

    private void init() {
        VBox top = new VBox(); top.setFillWidth(true);
        top.setPadding(new Insets(0, 0, 0, 10));
        this.setContent(top);
        this.setText(file.getName());
        addForDir( top, this.file);
    }

    private void addForDir(VBox box, File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                TitledPane pane = titledPane();
                pane.setText(file.getName());
                pane.setExpanded(false);
                pane.setOnMousePressed(mouseEvent -> {
                    if (!pane.isExpanded()) {
                        ((VBox) pane.getContent()).getChildren().clear();
                        addForDir(((VBox) pane.getContent()), file);
                    }
                });
                this.titledPaneHashMap.put(file.getPath(), pane);
                box.getChildren().add(pane);
            } else {
                Button button = getItemButton(file);
                this.buttonHashMap.put(file.getPath(), button);
                button.setText(file.getName());
                box.getChildren().add(button);
            }
        }
    }

    private TitledPane titledPane() {
        TitledPane pane = new TitledPane();
        ImageView folderIcon = new ImageView(ImageTools.getFolderImg()); folderIcon.setFitWidth(20); folderIcon.setFitHeight(20);
        pane.setGraphic(folderIcon);
        pane.getStylesheets().add(BetterTreeView.class.getResource("titledpane.css").toExternalForm());
        VBox box = new VBox(); box.setFillWidth(true);
        box.setPadding(new Insets(0, 0, 0, 20));
        pane.setContent(box);
        return pane;
    }

    private Button getItemButton(File fileName) {
        Button button = new Button();
        button.setTextAlignment(TextAlignment.LEFT);
        button.getStylesheets().add(BetterTreeView.class.getResource("button.css").toExternalForm());
        ImageView icon = new ImageView(ImageTools.getImg(fileName)); icon.setFitWidth(20); icon.setFitHeight(20);
        button.setGraphic(icon);
        button.setPrefWidth(100);
        return button;
    }

    public HashMap<String, Button> getButtonHashMap() {
        return this.buttonHashMap;
    }

    public HashMap<String, TitledPane> getTitledPaneHashMap() {
        return this.titledPaneHashMap;
    }
}
