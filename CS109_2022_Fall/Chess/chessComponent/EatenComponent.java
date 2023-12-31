package Chess.chessComponent;

import Chess.Main;
import Chess.model.ChessColor;
import Chess.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static Chess.chessComponent.SquareComponent.CHESS_FONT;

public class EatenComponent extends JComponent {
    String type, color;
    int number, x, y;
    static final int width = 24, height = 24;

    public EatenComponent(String type, int number, int x, int y, String color) {
        this.type = type;
        this.number = number;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        if (Main.theme.equals("像素")) {
            if (number > 0) {
                InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("chess-pixel"));
                try {
                    g.drawImage(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()),
                            width, height).getImage(), 8, 3, this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("chess-pixel-opq"));
                try {
                    g.drawImage(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()),
                            width, height).getImage(), 8, 3, this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            if (number > 0) {
                g.setColor(Color.decode(Main.getThemeResource("chessfill")));
                g.fillOval(8, 3, width - 1, height - 1);
            } else {
                Color color = Color.decode(Main.getThemeResource("chessfillopaque"));
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                g.fillOval(8, 3, width - 1, height - 1);
            }

            ((Graphics2D) g).setStroke(new BasicStroke(1)); // border width
            g.setColor(Color.decode(Main.getThemeResource("chessborder")));
            g.drawOval(8, 3, width - 1, height - 1);
        }

        if (number > 0) {
            g.setColor(color.equals("b") ? ChessColor.BLACK.getColor() : ChessColor.RED.getColor());
            g.setFont(CHESS_FONT.deriveFont(13.5f));
            g.drawString(this.type, 13, 20);

            if (number > 1) {
                g.setFont(CHESS_FONT.deriveFont(8f));
                g.drawString(String.valueOf(number), 26, 26);
            }
        }

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
