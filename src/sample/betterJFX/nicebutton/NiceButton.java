package sample.betterJFX.nicebutton;

import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class NiceButton extends Button {

    public NiceButton() {
        init();
    }
    public NiceButton(String text, Node graphic) {
        this.setText(text);
        this.setGraphic(graphic);
        init();
    }

    private void init() {
        this.setCursor(Cursor.HAND);
        this.setStyle("-fx-background-color: transparent;");
    }

    public void opacityEffect() { opacityEffect(0.6, 1, new Duration(200)); }
    public void opacityEffect(double before, double after, Duration duration) {
        FadeTransition fadeIn = new FadeTransition(duration, this);
        fadeIn.setToValue(after);
        fadeIn.setFromValue(before);
        FadeTransition fadeOut = new FadeTransition(duration, this);
        fadeOut.setToValue(before);
        fadeOut.setFromValue(after);
        this.setOnMouseEntered(mouseEvent -> {
            fadeOut.stop();
            fadeIn.play();
        });
        this.setOnMouseExited(mouseEvent -> {
            fadeIn.stop();
            fadeOut.play();
        });
    }

}
