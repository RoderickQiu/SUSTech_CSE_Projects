package Chess.view;

import Chess.Main;
import Chess.chessComponent.EatenComponent;
import Chess.chessComponent.SquareComponent;
import Chess.controller.GameController;
import Chess.model.ChessColor;
import Chess.model.ChessboardPoint;
import Chess.utils.FileUtils;
import Chess.utils.GeneralUtils;
import Chess.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static Chess.Main.*;
import static Chess.utils.GeneralUtils.log;
import static Chess.utils.ImageUtils.changeImageSize;

/**
 * 这个类表示游戏窗体，窗体上包含：
 * 1 Chessboard: 棋盘
 * 2 JLabel:  标签
 * 3 JButton： 按钮
 */
public class ChessGameFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    private static JLabel statusLabel, scoreLabel, blackEatenLabel, redEatenLabel;
    private static JPanel eatenPanel;
    public static String redEatenList, blackEatenList;
    private Gson gson = new Gson();
    private static EatenComponent[][] eatenComponents = new EatenComponent[2][7];
    private static String[][] nameList = {
            {"將", "士", "象", "車", "馬", "卒", "砲"},
            {"帥", "仕", "相", "俥", "傌", "兵", "炮"}};
    private static int redEaten[] = {0, 0, 0, 0, 0, 0, 0}, blackEaten[] = {0, 0, 0, 0, 0, 0, 0};

    public ChessGameFrame(int width, int height, int mode) {
        setTitle("翻翻棋～");
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;
        
        redEatenList = "";
        blackEatenList = "";

        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        addChessboard(mode);
        addStatusLabel();
        addScoreLabel();
        addBlackEatenLabel();
        addRedEatenLabel();
        addEatenList();
        addBackButton();
        addLoadButton();
        addRefreshButton();
        addThemeButton();
        addBg();
    }


    /**
     * 在游戏窗体中添加棋盘
     */
    private void addChessboard(int mode) {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE / 2 + 100, CHESSBOARD_SIZE, mode);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGHT / 13, HEIGHT / 13);
        add(chessboard);
    }

    private void addEatenList() {
        eatenPanel = new JPanel();
        eatenPanel.setBounds(WIDTH * 11 / 15 + 2, (int) (HEIGHT / 13 * 6.04), 75, 185);
        GridLayout gridLayout = new GridLayout(7, 2);
        eatenPanel.setLayout(gridLayout);
        eatenPanel.setBackground(Color.WHITE);
        eatenPanel.setOpaque(false);//workaround keep opaque after repainted
        add(eatenPanel);

        setEatenList();
    }

    private static void setEatenList() {
        parseEatenLists();

        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 2; i++) {
                if (eatenComponents[i][j] != null) eatenPanel.remove(eatenComponents[i][j]);
                eatenComponents[i][j] = new EatenComponent(nameList[i][j],
                        (i == 0) ? blackEaten[j] : redEaten[j], i, j, (i == 0) ? "b" : "r");
                eatenPanel.add(eatenComponents[i][j]);
            }
        }
    }

    private static void parseEatenLists() {
        Arrays.fill(redEaten, 0);
        Arrays.fill(blackEaten, 0);
        String[] red = redEatenList.split(" "), black = blackEatenList.split(" ");
        for (String s : red) {
            switch (s) {
                case "帥" -> redEaten[0]++;
                case "仕" -> redEaten[1]++;
                case "相" -> redEaten[2]++;
                case "俥" -> redEaten[3]++;
                case "傌" -> redEaten[4]++;
                case "兵" -> redEaten[5]++;
                case "炮" -> redEaten[6]++;
            }
        }
        for (String s : black) {
            switch (s) {
                case "將" -> blackEaten[0]++;
                case "士" -> blackEaten[1]++;
                case "象" -> blackEaten[2]++;
                case "車" -> blackEaten[3]++;
                case "馬" -> blackEaten[4]++;
                case "卒" -> blackEaten[5]++;
                case "砲" -> blackEaten[6]++;
            }
        }
    }

    /**
     * 在游戏窗体中添加标签
     */
    private void addStatusLabel() {
        statusLabel = new JLabel("红走棋");
        statusLabel.setLocation(WIDTH * 11 / 15 + 5, (int) (HEIGHT / 13 * 10.65));
        statusLabel.setSize(200, 60);
        statusLabel.setFont(sansFont.deriveFont(Font.BOLD, 23));
        statusLabel.setForeground(Main.getThemeColor("indicatorRed"));
        add(statusLabel);
    }

    private void addRedEatenLabel() {
        redEatenLabel = new JLabel();
        redEatenLabel.setLocation(WIDTH * 11 / 15 + 5, (int) (HEIGHT / 13 * 5.05));
        redEatenLabel.setSize(100, 200);
        redEatenLabel.setFont(sansFont.deriveFont(Font.BOLD, 14));
        redEatenLabel.setForeground(Main.getThemeColor("indicatorBlack"));
        JlabelSetText(redEatenLabel, "红被吃:----");
        redEatenLabel.setFont(sansFont.deriveFont(Font.BOLD, 14));
        //add(redEatenLabel);
    }

    private void addBlackEatenLabel() {
        blackEatenLabel = new JLabel();
        blackEatenLabel.setLocation(WIDTH * 11 / 15 + 5, (int) (HEIGHT / 13 * 6.65));
        blackEatenLabel.setSize(100, 200);
        blackEatenLabel.setFont(sansFont.deriveFont(Font.BOLD, 14));
        blackEatenLabel.setForeground(Main.getThemeColor("indicatorBlack"));
        JlabelSetText(blackEatenLabel, "黑被吃:----");
        blackEatenLabel.setFont(sansFont.deriveFont(Font.BOLD, 14));
        //add(blackEatenLabel);
    }

    public static void appendBlackEatenLabel(String str) {
        blackEatenList += " " + str;
        setEatenList();
        //JlabelSetText(blackEatenLabel, "黑被吃:<br/>" + blackEatenList);
    }

    public static void appendRedEatenLabel(String str) {
        redEatenList += " " + str;
        setEatenList();
        //JlabelSetText(redEatenLabel, "红被吃:<br/>" + redEatenList);
    }

    public static void popBlackEatenLabel() {
        blackEatenList = blackEatenList.substring(0, blackEatenList.lastIndexOf(" "));
        setEatenList();
        /*if (blackEatenList.strip().length() > 0)
            JlabelSetText(blackEatenLabel, "黑被吃:<br/>" + blackEatenList);
        else JlabelSetText(blackEatenLabel, "黑被吃:----");*/
    }

    public static void popRedEatenLabel() {
        redEatenList = redEatenList.substring(0, redEatenList.lastIndexOf(" "));
        setEatenList();
        /*if (redEatenList.strip().length() > 0)
            JlabelSetText(redEatenLabel, "红被吃:<br/>" + redEatenList);
        else JlabelSetText(redEatenLabel, "红被吃:----");*/
    }

    public static void setBlackEatenLabel(String str) {
        blackEatenList = str;
        setEatenList();
        //JlabelSetText(blackEatenLabel, "黑被吃:<br/>" + blackEatenList);
    }

    public static void setRedEatenLabel(String str) {
        redEatenList = str;
        setEatenList();
        //JlabelSetText(redEatenLabel, "红被吃:<br/>" + redEatenList);
    }


    private static void JlabelSetText(JLabel jLabel, String longString) {
        StringBuilder builder = new StringBuilder("<html>" + longString.substring(0, 7));
        char[] chars = longString.toCharArray();
        FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
        int start = 8;
        int len = 0;
        while (start + len < longString.length()) {
            while (true) {
                len++;
                if (start + len > longString.length()) break;
                if (fontMetrics.charsWidth(chars, start, len)
                        > jLabel.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len - 1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, longString.length() - start);
        builder.append("</html>");
        jLabel.setText(builder.toString());
    }

    private void addScoreLabel() {
        JLabel jLabel = new JLabel("红 - 黑");
        jLabel.setLocation((int) (WIDTH * 11.14 / 15 + 5), (int) (HEIGHT / 13 * 9.3));
        jLabel.setSize(200, 60);
        jLabel.setFont(sansFont.deriveFont(Font.BOLD, 23));
        jLabel.setForeground(ChessColor.BLACK.getColor());
        add(jLabel);

        scoreLabel = new JLabel("00 - 00");
        scoreLabel.setLocation(WIDTH * 11 / 15 + 5, (int) (HEIGHT / 13 * 9.85));
        scoreLabel.setSize(200, 60);
        scoreLabel.setFont(sansFont.deriveFont(Font.BOLD, 23));
        scoreLabel.setForeground(ChessColor.BLACK.getColor());
        add(scoreLabel);
    }

    public static JLabel getStatusLabel() {
        return statusLabel;
    }

    public static JLabel getScoreLabel() {
        return scoreLabel;
    }

    public static JLabel getBlackEatenLabel() {
        return blackEatenLabel;
    }

    public static JLabel getRedEatenLabel() {
        return redEatenLabel;
    }

    /**
     * 在游戏窗体中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addBackButton() {
        JButton button = new JButton("返回");
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        button.addActionListener((e) -> {
            Main.backStart();
            Main.playNotifyMusic("click");
        });
        button.setLocation(WIDTH * 11 / 15, HEIGHT / 13);
        button.setSize(80, 40);
        StartFrame.setButtonBg(button, 80, 40);
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("存档");
        button.setLocation(WIDTH * 11 / 15, HEIGHT / 13 + 45);
        button.setSize(80, 40);
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        StartFrame.setButtonBg(button, 80, 40);
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            Main.playNotifyMusic("click");
            int option = JOptionPane.showOptionDialog(this, "选择是要保存存档还是导入存档？", "请选择", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"保存存档", "导入存档"}, "导入存档");
            if (option == 1) {
                String path = JOptionPane.showInputDialog(this, "在这里输入存档的文件绝对路径");
                gameController.loadGSon(path);
            } else {
                gameController.toGSon();
            }
        });
    }

    private void addRefreshButton() {
        JButton button = new JButton("重置");
        button.setLocation(WIDTH * 11 / 15, HEIGHT / 13 + 90);
        button.setSize(80, 40);
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        StartFrame.setButtonBg(button, 80, 40);
        add(button);

        button.addActionListener(e -> {
            Main.playNotifyMusic("click");
            Main.refreshGame();
        });
    }

    private void addThemeButton() {
        JButton button = new JButton("主题");
        button.setLocation(WIDTH * 11 / 15, HEIGHT / 13 + 135);
        button.setSize(80, 40);
        button.setFont(sansFont.deriveFont(Font.BOLD, 17));
        StartFrame.setButtonBg(button, 80, 40);
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click theme");
            Main.playNotifyMusic("click");
            String path = JOptionPane.showInputDialog(this, "在下方输入主题的名字以选择主题（可选主题有像素、典雅、激情）：");

            if (path == null) {
                JOptionPane.showMessageDialog(this, "不能输入空内容！");
            } else if (path.equals(Main.themeList[0]) ||
                    path.equals(Main.themeList[1]) ||
                    path.equals(Main.themeList[2])) {
                log("SELECT " + path);
                Main.theme = path;
                data.put("theme", gson.toJsonTree(theme, new TypeToken<String>() {
                }.getType()));
                FileUtils.saveDataToFile("data", gson.toJson(data), "json");
                Main.startGame(Main.mode);
            } else {
                JOptionPane.showMessageDialog(this, "没有这个主题，请重新选择！");
            }
        });
    }


    private void addBg() {
        InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("bg"));
        JLabel lbBg = null;
        try {
            lbBg = new JLabel(ImageUtils.changeImageSize(new ImageIcon(stream.readAllBytes()), 0.3f));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        lbBg.setBounds(0, 0, WIDTH, HEIGHT);
        add(lbBg);
    }

}
