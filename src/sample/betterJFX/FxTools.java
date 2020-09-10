package sample.betterJFX;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public final class FxTools {

    public static void anchorAll(Node node, double offset) {
        AnchorPane.setLeftAnchor(node, offset); AnchorPane.setRightAnchor(node, offset);
        AnchorPane.setBottomAnchor(node, offset); AnchorPane.setTopAnchor(node, offset);
    }

}
