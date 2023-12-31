package Chess.chessComponent;

import Chess.controller.ClickController;
import Chess.model.ChessColor;
import Chess.model.ChessboardPoint;

import javax.swing.*;
import java.awt.*;

/**
 * 表示黑红车
 */
public class PaoComponent extends ChessComponent {

    public PaoComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size, 5, 2);
        if (this.getChessColor() == ChessColor.RED) {
            name = "炮";
        } else {
            name = "砲";
        }
    }

    @Override
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination, ChessboardPoint ONE, ChessColor playerColor) {
        SquareComponent destinationChess = chessboard[destination.getX()][destination.getY()];
        int x = ONE.getX();
        int y = ONE.getY();
        int x1 = destination.getX();
        int y1 = destination.getY();

        int n, m;//上下左右移动格数；
        n = x - x1;
        m = y - y1;

        boolean ismove = false;//判断是否违规
        int count1 = 0;
        if (n > 0 && m == 0) {
            for (int i = x1 + 1; i < x; i++) {
                if (chessboard[i][y].getCategory() <= 7) count1++;
            }
        }
        if (n < 0 && m == 0) {
            for (int i = x + 1; i < x1; i++) {
                if (chessboard[i][y].getCategory() <= 7) count1++;
            }

        }
        if (m > 0 && n == 0) {
            for (int i = y1 + 1; i < y; i++) {
                if (chessboard[x][i].getCategory() <= 7) count1++;
            }
        }
        if (m < 0 && n == 0) {
            for (int i = y + 1; i < y1; i++) {
                if (chessboard[x][i].getCategory() <= 7) count1++;
            }
        }
        if ((n >= 2 || n <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if ((m >= 2 || m <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if (!ismove) {
            JOptionPane.showMessageDialog(null, "炮不能移动");
            return false;
        }
        if (destinationChess instanceof EmptySlotComponent) return false;
        //棋子为敌方或我方
        /*if (destinationChess.getChessColor() == ChessColor.RED && destinationChess.isReversal) {//red为自己的棋子
            JOptionPane.showMessageDialog(null, "不能移动到自己的棋子上！"); //注释了，因为炮可以吃自己的棋子
            return false;
        } else */
        if (!chessboard[x1][y1].isReversal) {//移动位置棋子未翻开
            return true;
        } else {//敌方棋子
            return true;
        }
    }

    @Override
    public boolean isComMoveX(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {
        if (x1 + 2 < 0 || x1 + 2 > 8 || y1 < 0 || y1 > 4 || x1 - 2 < 0 || x1 - 2 > 8) {//i + 2
            return false;
        }
        int n, m;//上下左右移动格数；
        n = x - x1;
        m = y - y1;
        boolean ismove = false;//判断是否违规
        int count1 = 0;
        if (n > 0 && m == 0) {
            for (int i = x1 + 1; i < x; i++) {
                if (chessBoard[i][y].getCategory() <= 7) count1++;
            }
        }
        if (n < 0 && m == 0) {
            for (int i = x + 1; i < x1; i++) {
                if (chessBoard[i][y].getCategory() <= 7) count1++;
            }

        }
        if (m > 0 && n == 0) {
            for (int i = y1 + 1; i < y; i++) {
                if (chessBoard[x][i].getCategory() <= 7) count1++;
            }
        }
        if (m < 0 && n == 0) {
            for (int i = y + 1; i < y1; i++) {
                if (chessBoard[x][i].getCategory() <= 7) count1++;
            }
        }
        if ((n >= 2 || n <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if ((m >= 2 || m <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if (!ismove) {
            return false;
        }
        if (chessBoard[x1][y1] instanceof EmptySlotComponent) {//移动位置为空格子
            return false;
        } else if (chessBoard[x1][y1].isReversal && chessBoard[x1][y1].getChessColor() == playerColor) {//移动位置棋子未翻开
            return true;
        }
        return false;
    }

    @Override
    public boolean isComMoveY(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {
        if (x1 < 0 || x1 > 8 || y1 + 2 < 0 || y1 + 2 > 4 || y1 - 2 < 0 || y1 - 2 > 4) {//j + 2
            return false;
        }
        int n, m;//上下左右移动格数；
        n = x - x1;
        m = y - y1;
        boolean ismove = false;//判断是否违规
        int count1 = 0;
        if (n > 0 && m == 0) {
            for (int i = x1 + 1; i < x; i++) {
                if (chessBoard[i][y].getCategory() <= 7) count1++;
            }
        }
        if (n < 0 && m == 0) {
            for (int i = x + 1; i < x1; i++) {
                if (chessBoard[i][y].getCategory() <= 7) count1++;
            }

        }
        if (m > 0 && n == 0) {
            for (int i = y1 + 1; i < y; i++) {
                if (chessBoard[x][i].getCategory() <= 7) count1++;
            }
        }
        if (m < 0 && n == 0) {
            for (int i = y + 1; i < y1; i++) {
                if (chessBoard[x][i].getCategory() <= 7) count1++;
            }
        }
        if ((n >= 2 || n <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if ((m >= 2 || m <= -2) && (count1 == 1)) {
            ismove = true;
        }
        if (!ismove) {
            return false;
        }
        if (chessBoard[x1][y1] instanceof EmptySlotComponent) {//移动位置为空格子
            return false;
        } else if (chessBoard[x1][y1].isReversal && chessBoard[x1][y1].getChessColor() == playerColor) {//移动位置棋子未翻开
            return true;
        }
        return false;
    }
}
