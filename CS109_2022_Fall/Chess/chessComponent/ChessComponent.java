package Chess.chessComponent;

import Chess.Main;
import Chess.controller.ClickController;
import Chess.model.ChessColor;
import Chess.model.ChessboardPoint;
import Chess.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示棋盘上非空棋子的格子，是所有非空棋子的父类
 */
public abstract class ChessComponent extends SquareComponent {
    protected String name;// 棋子

    public ChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size, int blood, int category) {
        super(chessboardPoint, location, chessColor, clickController, size, blood, category);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Main.theme.equals("像素")) {
            if (isReversal && isSelected()) {
                InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("chess-pixel-sel"));
                try {
                    g.drawImage(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()),
                            this.getWidth() - 2 * spacingLength,
                            this.getHeight() - 2 * spacingLength).getImage(), spacingLength, spacingLength, this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else if (!(isCheating() && !isReversal)) {
                InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("chess-pixel"));
                try {
                    g.drawImage(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()),
                            this.getWidth() - 2 * spacingLength,
                            this.getHeight() - 2 * spacingLength).getImage(), spacingLength, spacingLength, this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("chess-pixel-opq"));
                try {
                    g.drawImage(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()),
                            this.getWidth() - 2 * spacingLength,
                            this.getHeight() - 2 * spacingLength).getImage(), spacingLength, spacingLength, this);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (isReversal || isCheating) {
                //绘制棋子文字
                g.setColor(this.getChessColor().getColor());
                g.setFont(CHESS_FONT);
                g.drawString(this.name, (int) (this.getWidth() / 3.45), this.getHeight() * 2 / 3);
            }
        } else {
            if (!(isCheating() && !isReversal)) {
                //绘制棋子填充色
                g.setColor(Color.decode(Main.getThemeResource("chessfill")));
                g.fillOval(spacingLength, spacingLength, this.getWidth() - 2 * spacingLength, this.getHeight() - 2 * spacingLength);
            } else {
                Color color = Color.decode(Main.getThemeResource("chessfillopaque"));
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                g.fillOval(spacingLength, spacingLength, this.getWidth() - 2 * spacingLength, this.getHeight() - 2 * spacingLength);
            }
            //绘制棋子边框
            ((Graphics2D) g).setStroke(new BasicStroke(3)); // border width
            g.setColor(Color.decode(Main.getThemeResource("chessborder")));
            g.drawOval(spacingLength, spacingLength, getWidth() - 2 * spacingLength, getHeight() - 2 * spacingLength);

            if (isReversal || isCheating()) {
                //绘制棋子文字
                g.setColor(this.getChessColor().getColor());
                g.setFont(CHESS_FONT);
                g.drawString(this.name, (int) (this.getWidth() / 3.45), this.getHeight() * 2 / 3);

                //绘制棋子被选中时状态
                if (isSelected()) {
                    g.setColor(ChessColor.RED.getColor());
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(4f));
                    g2.drawOval(spacingLength, spacingLength, getWidth() - 2 * spacingLength, getHeight() - 2 * spacingLength);
                }
            }
        }
    }


    public String getName() {
        return name;
    }


    public abstract boolean isComMoveX(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor);

    public abstract boolean isComMoveY(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor);
}
