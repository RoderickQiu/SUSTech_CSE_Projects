package Chess.controller;


import Chess.Main;
import Chess.chessComponent.SquareComponent;
import Chess.model.ChessColor;
import Chess.view.ChessGameFrame;
import Chess.view.Chessboard;

import javax.swing.*;

public class ClickController {
    private final Chessboard chessboard;
    private SquareComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    private int count = 0;

    public void onClick(SquareComponent squareComponent) {
        Main.playNotifyMusic("click");

        //判断第一次点击
        if (first == null) {
            if (handleFirst(squareComponent)) {
                squareComponent.setSelected(true);
                first = squareComponent;
                first.repaint();//还没有实现显示可以当前棋子可以移动的坐标
            }
        } else {
            if (first == squareComponent) { // 再次点击取消选取
                squareComponent.setSelected(false);
                SquareComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(squareComponent)) {
                //repaint in swap chess method.
                chessboard.swapChessComponents(first, squareComponent);
                chessboard.clickController.swapPlayer();

                first.setSelected(false);
                first = null;
            }
        }
    }


    /**
     * @param squareComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(SquareComponent squareComponent) {
        if (!squareComponent.isReversal()) {
            squareComponent.setReversal(true);
            squareComponent.setCheating(false);
            System.out.printf("onClick to reverse a chess [%d,%d]\n", squareComponent.getChessboardPoint().getX(), squareComponent.getChessboardPoint().getY());
            chessboard.isPlayerDo = true;
            Chessboard.isComReverse = true;
            Chessboard.Now.push(squareComponent);
            squareComponent.repaint();
            if (count == 0) {
                count++;
                chessboard.setCurrentColor(squareComponent.getChessColor());
                chessboard.setPlayerColor(squareComponent.getChessColor());
                if (squareComponent.getChessColor() == ChessColor.BLACK) {
                    JOptionPane.showMessageDialog(null, "你是黑色，开始博弈吧！");
                    chessboard.setEnemyColor(ChessColor.RED);
                } else {
                    chessboard.setEnemyColor(ChessColor.BLACK);
                    JOptionPane.showMessageDialog(null, "你是红色，开始博弈吧！");
                }
            }
            chessboard.clickController.swapPlayer();
            try {
                Thread.currentThread().sleep(1000);
            } catch (Exception e) {
            }
            return false;
        }
        return squareComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param squareComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    /*
     *
     * 对局主逻辑填充在这里
     */
    private boolean handleSecond(SquareComponent squareComponent) {

        return first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint(), first.getChessboardPoint(), chessboard.getPlayerColor());
    }

    public void swapPlayer() {
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        ChessGameFrame.getStatusLabel().setText(String.format("%s走棋", (chessboard.getCurrentColor().getName().equals("BLACK")) ? "黑" : "红"));
        ChessGameFrame.getStatusLabel().setForeground((chessboard.getCurrentColor() == ChessColor.BLACK) ? Main.getThemeColor("indicatorBlack") : ChessColor.RED.getColor());
        if (chessboard.getCurrentColor() == chessboard.getEnemyColor() && (chessboard.ModeChoice == 1 || chessboard.ModeChoice == 2)) {
            chessboard.Comround();
            swapPlayer();
        }
    }
}
