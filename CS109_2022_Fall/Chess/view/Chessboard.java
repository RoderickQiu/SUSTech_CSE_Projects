package Chess.view;


import Chess.Main;
import Chess.chessComponent.*;
import Chess.model.*;
import Chess.controller.ClickController;
import Chess.utils.FileUtils;
import Chess.utils.GeneralUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static Chess.Main.*;
import static Chess.Main.data;
import static Chess.utils.GeneralUtils.addPrefixZero;
import static Chess.utils.ImageUtils.changeToOriginalSize;

/**
 * 这个类表示棋盘组建，其包含：
 * SquareComponent[][]: 4*8个方块格子组件
 */
public class Chessboard extends JComponent {
    public int ModeChoice;//0代表人人对战，1代表人机难度1,2代表人机难度2；
    private final SquareComponent[][] squareComponents = new SquareComponent[ROW_SIZE][COL_SIZE];
    //todo: you can change the initial player
    private ChessColor currentColor = ChessColor.RED;
    private ChessColor playerColor = ChessColor.RED;
    private ChessColor enemyColor = ChessColor.BLACK;
    //all chessComponents in this chessboard are shared only one Chess.model Chess.controller
    public final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;

    private int[] blackList = {0, 0, 0, 0, 0, 0, 0}, redList = {0, 0, 0, 0, 0, 0, 0};
    private final int[] maxList = {1, 2, 2, 2, 2, 5, 2};
    private boolean hasLost = false, isCheating = false;
    private int num = 32;

    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 4;

    private static Stack<ChessboardPoint> stackX1 = new Stack<>();
    private static Stack<ChessboardPoint> stackX2 = new Stack<>();
    private static Stack<SquareComponent> stack1 = new Stack<>();
    private static Stack<SquareComponent> stack2 = new Stack<>();
    public static Stack<SquareComponent> Now = new Stack<>();
    public static boolean isComReverse = false;
    public boolean isPlayerDo = false;
    private Gson gson = new Gson();


    public boolean[] isEat = new boolean[320];

    private int q = 0;

    private Random random = new Random();

    //所有棋子的定义都在这了
    private ChessComponent[][] pieces = new ChessComponent[8][4];


    public Chessboard(int width, int height, int mode) {
        ModeChoice = mode;//0代表人人对战，1代表人机难度1,2代表人机难度2；
        setLayout(null); // Use absolute layout.
        setSize(width + 10, height);
        CHESS_SIZE = (height - 6) / 8;
        SquareComponent.setSpacingLength(CHESS_SIZE / 7);
        System.out.printf("chessboard [%d * %d], chess size = %d\n", width, height, CHESS_SIZE);
        addReverseButton();
        addCheatingButton();
        initAllChessOnBoard();
    }

    private void initAllChessOnBoard() {
        int a = 0;
        for (int i = 0; i < squareComponents.length; i++) {
            for (int j = 0; j < squareComponents[i].length; j++) {
                int type = chooseChessType();
                ChessColor color = type >= 7 ? ChessColor.RED : ChessColor.BLACK;
                SquareComponent squareComponent = switch (type) {
                    case 0, 7 ->
                            new ShuaiComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 1, 8 ->
                            new ShiComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 2, 9 ->
                            new XiangComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 3, 10 ->
                            new JuComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 4, 11 ->
                            new MaComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 5, 12 ->
                            new ZuComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case 6, 13 ->
                            new PaoComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    default -> throw new IllegalStateException("Unexpected value: " + j);
                };
                //id 用颜色换掉
                //camp category=100
                //get isreversal (是否翻开
                squareComponent.setVisible(true);
                putChessOnBoard(squareComponent);
            }
        }
    }

    /**
     * ***把被吃掉的棋子放进栈里存储，用来悔棋，存储状态；
     */
    public SquareComponent[][] getChessComponents() {
        return squareComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(ChessColor currentColor) {
        this.currentColor = currentColor;
        if (ModeChoice == 0) {
            this.playerColor = this.playerColor.equals(ChessColor.BLACK) ? ChessColor.RED : ChessColor.BLACK;
            this.enemyColor = this.enemyColor.equals(ChessColor.BLACK) ? ChessColor.RED : ChessColor.BLACK;
        }
    }

    public ChessColor getEnemyColor() {
        return enemyColor;
    }

    public void setEnemyColor(ChessColor enemyColor) {
        this.enemyColor = enemyColor;
    }

    public ChessColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * 将SquareComponent 放置在 ChessBoard上。里面包含移除原有的component及放置新的component
     *
     * @param squareComponent
     */
    public void putChessOnBoard(SquareComponent squareComponent) {
        int row = squareComponent.getChessboardPoint().getX(), col = squareComponent.getChessboardPoint().getY();
        if (squareComponents[row][col] != null) {
            remove(squareComponents[row][col]);
        }
        add(squareComponents[row][col] = squareComponent);
    }

    /**
     * 交换chess1 chess2的位置
     *
     * @param chess1
     * @param chess2
     */
    private int player_Score = 0;//玩家血量
    private int com_Score = 0;//电脑血量

    public void swapChessComponents(SquareComponent chess1, SquareComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (chess1.canMoveTo(squareComponents, chess2.getChessboardPoint(), chess1.getChessboardPoint(), playerColor)) {
            isEat[++q] = true;
            System.out.print(chess2.getBlood());
            if (chess2.getChessColor() == enemyColor) {
                if (enemyColor.equals(ChessColor.BLACK)) {
                    player_Score += chess2.getBlood();
                    ChessGameFrame.appendBlackEatenLabel(chess2.getName());
                } else {
                    com_Score += chess2.getBlood();
                    ChessGameFrame.appendRedEatenLabel(chess2.getName());
                }
            }
            if (chess2.getChessColor() == playerColor) {
                if (playerColor.equals(ChessColor.BLACK)) {
                    player_Score += chess2.getBlood();
                    ChessGameFrame.appendBlackEatenLabel(chess2.getName());
                } else {
                    com_Score += chess2.getBlood();
                    ChessGameFrame.appendRedEatenLabel(chess2.getName());
                }
            }
            ChessGameFrame.getScoreLabel().setText(addPrefixZero(player_Score) + " - " + addPrefixZero(com_Score));
            System.out.println("你用 " + "\033[31m" + chess1.getName() + "\33[0m" + " 吃了 " + chess2.getName());
            System.out.println("你的血量为:" + player_Score + ", 电脑血量为" + com_Score);
            stack1.push(chess2);
            stack2.push(chess1);
            stackX1.push(chess1.getChessboardPoint());
            stackX2.push(chess2.getChessboardPoint());
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));
            chess1.swapLocation(chess2);
            int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
            squareComponents[row1][col1] = chess1;
            int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
            squareComponents[row2][col2] = chess2;
            Main.playNotifyMusic("action");
            chess1.repaint();
            chess2.repaint();
        } else {
            System.out.printf("不能移动到这里\n");
            JOptionPane.showMessageDialog(this, "不能移动到这里");
        }
        if (player_Score >= 60 || com_Score >= 60) {
            if (!hasLost) {
                String text;
                if ((com_Score >= 60 && enemyColor.equals(ChessColor.BLACK)) ||
                        ((player_Score >= 60 && enemyColor.equals(ChessColor.RED)))) {
                    text = "你输了！!";
                    playNotifyMusic("fail");
                } else {
                    text = "你赢了!!";
                    playNotifyMusic("success");
                    scoresList.set(currentUserId, scoresList.get(currentUserId) + 1);
                    data.put("userScores", gson.toJsonTree(scoresList, new TypeToken<ArrayList<Integer>>() {
                    }.getType()));
                    FileUtils.saveDataToFile("data", gson.toJson(data), "json");
                }
                hasLost = true;
                JOptionPane.showMessageDialog(this, text);
            }
            Main.backStart();
        }
        //玩家吃棋或移动棋
    }

    public void Comround() {
        //isComReverse = false;
        if (!hasLost) {
            System.out.println("电脑回合:>");
            if (ModeChoice == 1) moveChess1();
            else moveChess2();
        }
    }

    private void moveChess1() {//简单遍历
        if (hasLost) return;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                SquareComponent test = squareComponents[i][j];
                //是电脑阵营， 并且已翻开
                if (test.getChessColor() == enemyColor && test.isReversal()) {
                    //炮单独考虑
                    if (test.getCategory() != 2) {
                        //正常情况
                        if (test.isComMoveX(squareComponents, i + 1, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i + 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveX(squareComponents, i - 1, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i - 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveY(squareComponents, i, j + 1, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j + 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveY(squareComponents, i, j - 1, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j - 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        }
                    } else {
                        //炮
                        if (test.isComMoveX(squareComponents, i + 2, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i + 2][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveX(squareComponents, i - 2, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i - 2][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveY(squareComponents, i, j + 2, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j + 2];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (test.isComMoveY(squareComponents, i, j - 2, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j - 2];
                            ComputerEat(i, j, test, tmp);
                            return;
                        }
                    }
                }
            }
            if (player_Score >= 60 || com_Score >= 60) {
                if (!hasLost) {
                    String text;
                    if ((com_Score >= 60 && enemyColor.equals(ChessColor.BLACK)) ||
                            ((player_Score >= 60 && enemyColor.equals(ChessColor.RED)))) {
                        text = "你输了！!";
                        playNotifyMusic("fail");
                    } else {
                        text = "你赢了!!";
                        playNotifyMusic("success");
                        scoresList.set(currentUserId, scoresList.get(currentUserId) + 1);
                        data.put("userScores", gson.toJsonTree(scoresList, new TypeToken<ArrayList<Integer>>() {
                        }.getType()));
                        FileUtils.saveDataToFile("data", gson.toJson(data), "json");
                    }
                    hasLost = true;
                    JOptionPane.showMessageDialog(this, text);
                }
                Main.backStart();
                break;
            }
        }
        if (num != 0) chessReverse();
        else {
            while (true) { // 随机移动一颗棋子
                Random r = new Random();
                int i = r.nextInt(8), j = r.nextInt(4);
                SquareComponent test = squareComponents[i][j];
                //是电脑阵营， 并且已翻开
                if (test.getChessColor() == enemyColor && test.isReversal()) {
                    //炮单独考虑
                    if (test.getCategory() != 2) {
                        if (isComMoveXForEmptyField(squareComponents, i + 1, j, i, j)) {
                            SquareComponent tmp = squareComponents[i + 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveXForEmptyField(squareComponents, i - 1, j, i, j)) {
                            SquareComponent tmp = squareComponents[i - 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveYForEmptyField(squareComponents, i, j + 2, i, j)) {
                            SquareComponent tmp = squareComponents[i][j + 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveYForEmptyField(squareComponents, i, j - 2, i, j)) {
                            SquareComponent tmp = squareComponents[i][j - 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void moveChess2() {//一层判断的贪心
        if (hasLost) return;
        int[][][] count = new int[32][8][4];
        int ii = 0, jj = 0, kk = 0, bblood = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                SquareComponent test = squareComponents[i][j];
                //是电脑阵营， 并且已翻开
                if (test.getChessColor() == enemyColor && test.isReversal()) {
                    //炮单独考虑
                    if (test.getCategory() != 2) {
                        //正常情况
                        if (test.isComMoveX(squareComponents, i + 1, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i + 1][j];
                            count[0][i][j] += tmp.getBlood();
                        } else if (test.isComMoveX(squareComponents, i - 1, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i - 1][j];
                            count[1][i][j] += tmp.getBlood();
                        } else if (test.isComMoveY(squareComponents, i, j + 1, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j + 1];
                            count[2][i][j] += tmp.getBlood();
                        } else if (test.isComMoveY(squareComponents, i, j - 1, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j - 1];
                            count[3][i][j] += tmp.getBlood();
                        }
                    } else {
                        //炮
                        if (test.isComMoveX(squareComponents, i + 2, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i + 2][j];
                            count[4][i][j] += tmp.getBlood();
                        } else if (test.isComMoveX(squareComponents, i - 2, j, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i - 2][j];
                            count[5][i][j] += tmp.getBlood();
                        } else if (test.isComMoveY(squareComponents, i, j + 2, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j + 2];
                            count[6][i][j] += tmp.getBlood();
                        } else if (test.isComMoveY(squareComponents, i, j - 2, i, j, playerColor)) {
                            SquareComponent tmp = squareComponents[i][j - 2];
                            count[7][i][j] += tmp.getBlood();
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 4; k++) {
                    if (count[i][j][k] >= bblood) {
                        bblood = count[i][j][k];
                        ii = j;
                        jj = k;
                        kk = i;
                    }
                }
            }
        }
        if (bblood != 0) {
            switch (kk) {
                case 0:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii + 1][jj]);
                    return;
                case 1:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii - 1][jj]);
                    return;
                case 2:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii][jj + 1]);
                    return;
                case 3:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii][jj - 1]);
                    return;
                case 4:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii + 2][jj]);
                    return;
                case 5:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii - 2][jj]);
                    return;
                case 6:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii][jj + 2]);
                    return;
                case 7:
                    ComputerEat(ii, jj, squareComponents[ii][jj], squareComponents[ii][jj - 2]);
                    return;
            }
        }
        if (player_Score >= 60 || com_Score >= 60) {
            if (!hasLost) {
                String text;
                if ((com_Score >= 60 && enemyColor.equals(ChessColor.BLACK)) || player_Score >= 60 && enemyColor.equals(ChessColor.RED)) {
                    text = "你输了！!";
                    playNotifyMusic("fail");
                } else {
                    text = "你赢了!!";
                    playNotifyMusic("success");
                    scoresList.set(currentUserId, scoresList.get(currentUserId) + 1);
                    data.put("userScores", gson.toJsonTree(scoresList, new TypeToken<ArrayList<Integer>>() {
                    }.getType()));
                    FileUtils.saveDataToFile("data", gson.toJson(data), "json");
                }
                hasLost = true;
                JOptionPane.showMessageDialog(this, text);
            }
            Main.backStart();
        }
        if (num != 0) chessReverse();
        else {
            while (true) { // 随机移动一颗棋子
                Random r = new Random();
                int i = r.nextInt(8), j = r.nextInt(4);
                SquareComponent test = squareComponents[i][j];
                //是电脑阵营， 并且已翻开
                if (test.getChessColor() == enemyColor && test.isReversal()) {
                    //炮单独考虑
                    if (test.getCategory() != 2) {
                        if (isComMoveXForEmptyField(squareComponents, i + 1, j, i, j)) {
                            SquareComponent tmp = squareComponents[i + 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveXForEmptyField(squareComponents, i - 1, j, i, j)) {
                            SquareComponent tmp = squareComponents[i - 1][j];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveYForEmptyField(squareComponents, i, j + 2, i, j)) {
                            SquareComponent tmp = squareComponents[i][j + 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        } else if (isComMoveYForEmptyField(squareComponents, i, j - 2, i, j)) {
                            SquareComponent tmp = squareComponents[i][j - 1];
                            ComputerEat(i, j, test, tmp);
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean isComMoveXForEmptyField(SquareComponent[][] chessBoard, int x1, int y1, int x, int y) {
        if (x1 + 1 < 0 || x1 + 1 > 8 || y1 < 0 || y1 > 4 || x1 - 1 < 0) {//i + 1
            return false;
        }
        //移动位置为空格子
        return chessBoard[x1][y1].getCategory() == 100;
    }

    public boolean isComMoveYForEmptyField(SquareComponent[][] chessBoard, int x1, int y1, int x, int y) {
        if (x1 < 0 || x1 > 8 || y1 + 1 > 4 || y1 - 1 < 0 || y1 - 1 > 4) {//j + 1
            return false;
        }
        //移动位置为空格子
        return chessBoard[x1][y1].getCategory() == 100;
    }

    private void ComputerEat(int i, int j, SquareComponent test, SquareComponent tmp) {
        /*try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        isEat[++q] = true;
        System.out.println("电脑用 " + test.getName() + " 吃了 " + "\033[31m" + tmp.getName() + "\33[0m");
        System.out.print(tmp.getBlood() + "\n");
        if (tmp.getChessColor() == playerColor) {
            if (playerColor.equals(ChessColor.BLACK)) {
                player_Score += tmp.getBlood();
                ChessGameFrame.appendBlackEatenLabel(tmp.getName());
            } else {
                com_Score += tmp.getBlood();
                ChessGameFrame.appendRedEatenLabel(tmp.getName());
            }
        } else if (tmp.getChessColor() == enemyColor) {
            if (enemyColor.equals(ChessColor.BLACK)) {
                player_Score += tmp.getBlood();
                ChessGameFrame.appendBlackEatenLabel(tmp.getName());
            } else {
                com_Score += tmp.getBlood();
                ChessGameFrame.appendRedEatenLabel(tmp.getName());
            }
        }
        ChessGameFrame.getScoreLabel().setText(addPrefixZero(player_Score) + " - " + addPrefixZero(com_Score));
        System.out.println("你的血量为:" + player_Score + ", 电脑血量为" + com_Score);
        stack1.push(tmp);
        stack2.push(test);
        stackX1.push(test.getChessboardPoint());
        stackX2.push(tmp.getChessboardPoint());
        /**
         * 把吃过的棋子打印到旁边，还未实现
         */
        remove(tmp);
        add(tmp = new EmptySlotComponent(tmp.getChessboardPoint(), tmp.getLocation(), clickController, CHESS_SIZE));
        test.swapLocation(tmp);
        int row1 = test.getChessboardPoint().getX(), col1 = test.getChessboardPoint().getY();
        squareComponents[row1][col1] = test;
        int row2 = tmp.getChessboardPoint().getX(), col2 = tmp.getChessboardPoint().getY();
        squareComponents[row2][col2] = tmp;
        Main.playNotifyMusic("action");

        //只重新绘制chess1 chess2，其他不变
        tmp.repaint();
        test.repaint();

        if (player_Score >= 60 || com_Score >= 60) {
            if (!hasLost) {
                String text;
                if ((com_Score >= 60 && enemyColor.equals(ChessColor.BLACK)) || player_Score >= 60 && enemyColor.equals(ChessColor.RED)) {
                    text = "你输了！!";
                    playNotifyMusic("fail");
                } else {
                    text = "你赢了!!";
                    playNotifyMusic("success");
                    scoresList.set(currentUserId, scoresList.get(currentUserId) + 1);
                    data.put("userScores", gson.toJsonTree(scoresList, new TypeToken<ArrayList<Integer>>() {
                    }.getType()));
                    FileUtils.saveDataToFile("data", gson.toJson(data), "json");
                }
                hasLost = true;
                JOptionPane.showMessageDialog(this, text);
            }
            Main.backStart();
        }
    }

    private void chessReverse() {
        if (hasLost) return;

        //每次电脑翻棋前先算有多少翻开了
        num = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                if (!squareComponents[i][j].isReversal()) {
                    num++;
                }
            }
        }

        if (num == 0) { // 应是0
            if (ModeChoice == 1) moveChess1();
            if (ModeChoice == 2) moveChess2();
        } else {
            System.out.println("NUM" + num);
            boolean flag = true;
            while (flag) {
                int x = random.nextInt(8);
                int y = random.nextInt(4);
                SquareComponent tmp = squareComponents[x][y];
                if (!tmp.isReversal()) {
                    tmp.setReversal(true);
                    tmp.setCheating(false);
                    tmp.repaint();
                    isComReverse = true;
                    Now.push(tmp);
                    System.out.printf("COMPUTER onClick to reverse a chess [%d,%d]\n", tmp.getChessboardPoint().getX(), tmp.getChessboardPoint().getY());
                    tmp.repaint();
                    flag = false;
                }
            }
        }
    }

    /**
     * Stack1记录被吃的棋子
     * Stack2记录吃棋的棋子
     * 可以用来做悔棋
     */
    private void addReverseButton() {
        JButton button = new JButton("悔棋");
        button.setLocation(298, 180);
        button.setSize(80, 40);
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        StartFrame.setButtonBg(button, 80, 40);
        add(button);
        button.addActionListener(e -> {
            if (isEat[q]) {
                System.out.println("reverse");
                Main.playNotifyMusic("click");
                if (ModeChoice == 0 || isComReverse)
                    HuiQI();
                else {
                    HuiQI();
                    HuiQI();
                }
            } else JOptionPane.showMessageDialog(null, "没有吃棋，不能悔棋！");
        });
    }

    private void addCheatingButton() {
        JButton button = new JButton("偷看");
        button.setLocation(298, 225);
        button.setSize(80, 40);
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        StartFrame.setButtonBg(button, 80, 40);
        add(button);
        button.addActionListener(e -> {
            Cheatingmode();
        });
    }

    public void HuiQI() {//重新绘制不太会
        if (!stack1.isEmpty() && !stack2.isEmpty()) {
            if (ModeChoice == 0) //如果人机，重新换回上一方
                setCurrentColor(currentColor.equals(ChessColor.RED) ? ChessColor.BLACK : ChessColor.RED);
            ChessGameFrame.getStatusLabel().setText(String.format("%s走棋", (getCurrentColor().getName().equals("BLACK")) ? "黑" : "红"));
            ChessGameFrame.getStatusLabel().setForeground((getCurrentColor() == ChessColor.BLACK) ? Main.getThemeColor("indicatorBlack") : ChessColor.RED.getColor());
            SquareComponent s = stack1.pop();//被吃
            ChessboardPoint p1 = stackX2.pop();
            SquareComponent o = stack2.pop();//吃
            ChessboardPoint p2 = stackX1.pop();
            o.setChessboardPoint(p2);
            System.out.println(p1.getX() + " " + p1.getY() + "" + p2 + " ");
            if (s.getChessColor() == ChessColor.BLACK) {
                player_Score -= s.getBlood();
                ChessGameFrame.getScoreLabel().setText(addPrefixZero(player_Score) + " - " + addPrefixZero(com_Score));
                ChessGameFrame.popBlackEatenLabel();
            } else if (s.getChessColor() == ChessColor.RED) {
                com_Score -= s.getBlood();
                ChessGameFrame.getScoreLabel().setText(addPrefixZero(player_Score) + " - " + addPrefixZero(com_Score));
                ChessGameFrame.popRedEatenLabel();
            }
            System.out.println("你的血量为:" + player_Score + ", 电脑血量为" + com_Score);
            remove(squareComponents[p1.getX()][p1.getY()]);
            remove(squareComponents[p2.getX()][p2.getY()]);
            add(squareComponents[p1.getX()][p1.getY()] = s);
            add(squareComponents[p2.getX()][p2.getY()] = o);
            squareComponents[p1.getX()][p1.getY()].setLocation(calculatePoint(p1.getX(), p1.getY()));
            squareComponents[p2.getX()][p2.getY()].setLocation(calculatePoint(p2.getX(), p2.getY()));
            System.out.println(squareComponents[p1.getX()][p1.getY()].toString());
            System.out.println(squareComponents[p2.getX()][p2.getY()].toString());
            /*if (isPlayerDo || isComReverse) {
                squareComponents[p1.getX()][p1.getY()].setReversal(false);
                isPlayerDo = false;
                isComReverse = false;
            }
            if (squareComponents[p2.getX()][p2.getY()].getCategory() == 2
                    && (!squareComponents[p1.getX()][p1.getY()].isReversal())) {
                squareComponents[p1.getX()][p1.getY()].setReversal(false);
            }*/
            // if just reversed, reverse back
            System.out.println(isComReverse);
            if (isComReverse) {
                try {
                    SquareComponent now = Now.pop();
                    now.setReversal(false);
                    now.repaint();
                    System.out.println("Rev back: " + now);
                } catch (Exception e) {
                    System.out.println("ERR: " + e.getMessage());
                }
                isComReverse = false;
            }

            if (squareComponents[p2.getX()][p2.getY()].getChessColor() == enemyColor
                    && squareComponents[p1.getX()][p1.getY()].getChessColor() == playerColor)
                squareComponents[p1.getX()][p1.getY()].setVisible(true);
            squareComponents[p2.getX()][p2.getY()].setVisible(true);
            squareComponents[p1.getX()][p1.getY()].repaint();
            squareComponents[p2.getX()][p2.getY()].repaint();
            isEat[q] = false;
            q--;
        }
    }

    /**
     * 作弊模式（或许应该在clickcontroller里面？）
     * 将选择的棋子设置为 category = 10（cheating棋子的特点）
     * 应该增加蒙版和闪烁效果
     */
    public void Cheatingmode() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你选择的坐标:>");
        String res = JOptionPane.showInputDialog("请输入你选择的坐标（两个数字，用空格隔开，从 1 开始）：");
        int x = Integer.parseInt(res.split(" ")[0]), y = Integer.parseInt(res.split(" ")[1]);

        //坐标不对
        if (x < 1 || x > 8 || y < 1 || y > 4) {
            System.out.println("坐标非法, 请重新输入!!!");
            JOptionPane.showMessageDialog(null, "坐标非法, 请重新输入!!!");
            return;
        }

        //当前位置没棋子了
        if (squareComponents[x - 1][y - 1] instanceof EmptySlotComponent) {
            System.out.println("目标位置棋子为空, 请重新输入!");
            JOptionPane.showMessageDialog(null, "目标位置棋子为空, 请重新输入!");
        } else if (squareComponents[x - 1][y - 1].isReversal()) {
            System.out.println("你选择的棋子已经翻开");
            JOptionPane.showMessageDialog(null, "你选择的棋子已经翻开");
        } else {
            squareComponents[x - 1][y - 1].setCheating(true);
            //不翻开的前提下用ischeating把他以不同的形式显示出来，不太懂怎么弄
            squareComponents[x - 1][y - 1].repaint();
            if (squareComponents[x - 1][y - 1].getChessColor() == playerColor) {
                System.out.println("你选择的位置是 (" + (x) + "," + (y) + ") 的 " + "\033[31m" + squareComponents[x - 1][y - 1].getName() + "\33[0m");
                JOptionPane.showMessageDialog(null, "你选择的位置是 (" + (x) + "," + (y) + ") 的 " + squareComponents[x - 1][y - 1].getName());
            } else {
                System.out.println("你选择的位置是 (" + (x) + "," + (y) + ") 的" + squareComponents[x - 1][y - 1].getName());
                JOptionPane.showMessageDialog(null, "你选择的位置是 (" + (x) + "," + (y) + ") 的" + squareComponents[x - 1][y - 1].getName());
            }
        }//"\033[31m" + chess1.getName() + "\33[0m" + "
    }

    private int chooseChessType() {
        Random random1 = new Random(), random2 = new Random();
        while (true) {
            int newInt = random1.nextInt(7), newColor = random2.nextInt(2);
            if (newColor == 0) {
                if (blackList[newInt] < maxList[newInt]) {
                    blackList[newInt]++;
                    System.out.print(newInt);
                    return newInt;
                }
            } else {
                if (redList[newInt] < maxList[newInt]) {
                    redList[newInt]++;
                    System.out.print(newInt);
                    return newInt + 7;
                }
            }
        }
    }

    /**
     * 绘制棋盘格子
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("board"));
        Image image = new ImageIcon().getImage();
        try {
            image = changeToOriginalSize(new ImageIcon(stream.readAllBytes()), 288, 576).getImage();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        g.drawImage(image, 0, 0, this);
        //g.fillRect(0, 0, this.getWidth(), this.getHeight());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * 将棋盘上行列坐标映射成Swing组件的Point
     *
     * @param row 棋盘上的行
     * @param col 棋盘上的列
     * @return
     */
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + 3, row * CHESS_SIZE + 3);
    }

    public ChessBackup backupGame() {
        String nowColor = currentColor.equals(ChessColor.BLACK) ? "b" : "r";
        String[][] type = new String[ROW_SIZE][COL_SIZE];
        String[][] color = new String[ROW_SIZE][COL_SIZE];
        boolean[][] reversal = new boolean[ROW_SIZE][COL_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                type[i][j] = String.valueOf(squareComponents[i][j].getClass());
                color[i][j] = squareComponents[i][j].getChessColor().equals(ChessColor.BLACK) ? "b" : "r";
                reversal[i][j] = squareComponents[i][j].isReversal();
            }
        }
        String records = "--";
        String redEatenList = ChessGameFrame.redEatenList;
        String blackEatenList = ChessGameFrame.blackEatenList;
        int redScore = Integer.parseInt(ChessGameFrame.getScoreLabel().getText().split(" - ")[0]);
        int blackScore = Integer.parseInt(ChessGameFrame.getScoreLabel().getText().split(" - ")[1]);
        System.out.println(new ChessBackup(nowColor, type, color, reversal, records, redEatenList, blackEatenList, redScore, blackScore));
        return new ChessBackup(nowColor, type, color, reversal, records, redEatenList, blackEatenList, redScore, blackScore);
    }

    /**
     * 通过GameController调用该方法
     *
     * @param chessData
     */
    public void loadGame(ChessBackup chessData) {
        String[][] colors = chessData.getColor();
        String[][] types = chessData.getType();
        boolean[][] reversals = chessData.getReversal();

        ChessGameFrame.setRedEatenLabel(chessData.getRedEatenList());
        ChessGameFrame.setBlackEatenLabel(chessData.getBlackEatenList());

        com_Score = chessData.getBlackScore();
        player_Score = chessData.getRedScore();
        ChessGameFrame.getScoreLabel().setText(addPrefixZero(player_Score) + " - " + addPrefixZero(com_Score));

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                ChessColor color = colors[i][j].equals("b") ?
                        ChessColor.BLACK : ChessColor.RED;
                String type = types[i][j];
                SquareComponent squareComponent = switch (type) {
                    case "class Chess.chessComponent.ShuaiComponent" ->
                            new ShuaiComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.ShiComponent" ->
                            new ShiComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.XiangComponent" ->
                            new XiangComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.JuComponent" ->
                            new JuComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.MaComponent" ->
                            new MaComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.ZuComponent" ->
                            new ZuComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.PaoComponent" ->
                            new PaoComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color, clickController, CHESS_SIZE);
                    case "class Chess.chessComponent.EmptySlotComponent" ->
                            new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE);
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };

                GeneralUtils.log(type + " try repaint");

                squareComponent.setVisible(true);
                putChessOnBoard(squareComponent);

                squareComponent.setReversal(reversals[i][j]);
                squareComponent.repaint();
            }
        }
    }
}
