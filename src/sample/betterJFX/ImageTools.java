package sample.betterJFX;

import javafx.scene.image.Image;

import java.io.File;

public final class ImageTools {

    public static Image getImg(File file) {
        String fileName = file.getName();
        String[] imageEndings = { ".png", ".jpeg", ".svg", ".gif" };
        String[] textEndings = { ".txt", ".properties", ".yml", ".yaml", ".css" };
        String[] videoEndings = { ".png", ".jpeg", ".svg", ".gif" };
        if (fileName.endsWith(imageEndings[0]) || fileName.endsWith(imageEndings[1]) || fileName.endsWith(imageEndings[2]) || fileName.endsWith(imageEndings[3])) {
            return new Image(ImageTools.class.getResource("icons/image.png").toExternalForm());
        }
        if (fileName.endsWith(textEndings[0]) || fileName.endsWith(textEndings[1]) || fileName.endsWith(textEndings[2]) || fileName.endsWith(textEndings[3]) || fileName.endsWith(textEndings[4])) {
            return new Image(ImageTools.class.getResource("icons/text.png").toExternalForm());
        }
        if (fileName.endsWith(videoEndings[0]) || fileName.endsWith(videoEndings[1]) || fileName.endsWith(videoEndings[2]) || fileName.endsWith(videoEndings[3])) {
            return new Image(ImageTools.class.getResource("icons/video.png").toExternalForm());
        }
        if (!file.isDirectory()) {
            return new Image(ImageTools.class.getResource("icons/file.png").toExternalForm());
        }
        return new Image(ImageTools.class.getResource("icons/folder.png").toExternalForm());
    }

    public static Image getFolderImg() {
        return new Image(ImageTools.class.getResource("icons/folder.png").toExternalForm());
    }

}
