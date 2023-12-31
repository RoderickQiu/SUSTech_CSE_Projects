package Chess.chessComponent;

import Chess.controller.ClickController;
import Chess.model.ChessColor;
import Chess.model.ChessboardPoint;

import java.awt.*;

/**
 * 这个类表示棋盘上的空棋子的格子
 */
public class EmptySlotComponent extends SquareComponent {

    public EmptySlotComponent(ChessboardPoint chessboardPoint, Point location, ClickController clickController, int chess_size) {
        super(chessboardPoint, location, ChessColor.NONE, clickController, chess_size, 0, 100);
        this.isReversal = true;
    }

    /**
     * @param chessboard  棋盘
     * @param destination 目标位置，如(0, 0), (0, 1)等等
     * @param ONE
     * @param playerColor
     * @return this棋子对象的移动规则和当前位置(chessboardPoint)能否到达目标位置
     * <br>
     * 这个方法主要是检查移动的合法性，如果合法就返回true，反之是false。
     */
    @Override
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination, ChessboardPoint ONE, ChessColor playerColor) {
        return false;
    }

    @Override
    public boolean isComMoveX(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {
        return false;
    }

    @Override
    public boolean isComMoveY(SquareComponent[][] chessBoard, int x1, int y1, int x, int y, ChessColor playerColor) {
        return false;
    }


}
