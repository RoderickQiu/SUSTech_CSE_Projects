package Chess.chessComponent;

import Chess.controller.ClickController;
import Chess.model.ChessColor;
import Chess.model.ChessboardPoint;

import javax.swing.*;
import java.awt.*;

public class ShiComponent extends ChessComponent {

    public ShiComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size, 10, 6);
        if (this.getChessColor() == ChessColor.RED) {
            name = "仕";
        } else {
            name = "士";
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
        if (!destinationChess.isReversal) {
            JOptionPane.showMessageDialog(null, "不能吃还没翻开的棋子！");
            return false;
        }
        int count = 0;//判断是否违规

        if (n == 1 || n == -1) {//左右移动一格
            if (m == 0) {//上下没有移动
                count++;
            }
        }
        if (m == 1 || m == -1) {//上下移动一格
            if (n == 0) {//左右没有移动
                count++;
            }
        }
        if (count != 1) {
            JOptionPane.showMessageDialog(null, "棋子只能上下左右移动一格！");
            return false;
        }
        if (destinationChess instanceof EmptySlotComponent) return true;
        if (destinationChess.getChessColor() == playerColor) {//red 为自己的棋子
            JOptionPane.showMessageDialog(null, "不能移动到自己的棋子上！");
            //continue;
            return false;
        } else {//敌方棋子
            if (destinationChess.getCategory() <= 6) {//扣除敌方血量
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "对方的棋子比你的大");
                return false;
            }
        }
    }

    @Override
    public boolean isComMoveX(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {
        if (x1 + 1 < 0 || x1 + 1 > 8 || y1 < 0 || y1 > 4 || x1 - 1 < 0) {//i + 1
            return false;
        }

        if (chessBoard[x1][y1].getCategory() == 100) {//移动位置为空格子
            return false;
        } else if (!chessBoard[x1][y1].isReversal) {//移动位置棋子未翻开
            return false;
        } else {//棋子为敌方或我方
            if (chessBoard[x1][y1].getChessColor() != playerColor) {
                return false;
            } else {//敌方棋子
                if (chessBoard[x1][y1].getCategory() <= 6) {
                    //扣除敌方血量
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean isComMoveY(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {


        if (x1 < 0 || x1 > 8 || y1 + 1 > 4 || y1 - 1 < 0 || y1 - 1 > 4) {//j + 1
            return false;
        }
        if (chessBoard[x1][y1].getCategory() == 100) {//移动位置为空格子
            return false;
        } else if (!chessBoard[x1][y1].isReversal) {//移动位置棋子未翻开
            return false;
        } else {//棋子为敌方或我方
            if (chessBoard[x1][y1].getChessColor() != playerColor) {
                return false;
            } else {//敌方棋子
                //扣除敌方血量
                return chessBoard[x1][y1].getCategory() <= 6;
            }
        }
    }
}
