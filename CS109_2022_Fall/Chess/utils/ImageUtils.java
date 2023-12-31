package Chess.utils;

import javax.swing.*;
import java.awt.*;

public class ImageUtils {
    public static ImageIcon changeImageSize(ImageIcon image, float i) {
        int width = (int) (image.getIconWidth() * i);
        int height = (int) (image.getIconHeight() * i);
        Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon changeToOriginalSize(ImageIcon image, int width, int height) {
        Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
