package Chess.view;

import Chess.Main;
import Chess.utils.FileUtils;
import Chess.utils.GeneralUtils;
import Chess.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static Chess.Main.*;

public class StartFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;
    private Gson gson = new Gson();

    public StartFrame(int width, int height) {
        setTitle("翻翻棋～");
        this.WIDTH = width;
        this.HEIGHT = height;

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        addTitle();
        addStartButton();
        addRankButton();

        //hasLogon = true;
        if (!hasLogon)
            openUserSelectDialog();
        else
            setCurrentUser(currentUser);
    }

    private void openUserSelectDialog() {
        Object[] options = {"登陆已有账号", "新建账号"}; // 0 1
        int m = JOptionPane.showOptionDialog(null, "请选择登陆已有账号还是新建账号", "翻翻棋", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (m == 0) {
            String n = JOptionPane.showInputDialog("请输入用户名");
            if (n == null) {
                JOptionPane.showMessageDialog(null, "登陆失败，输入用户名为空！");
                openUserSelectDialog();
            } else if (userList.contains(n)) {
                JOptionPane.showMessageDialog(null, "登陆成功");
                currentUserId = userList.indexOf(n);
                setCurrentUser(n);
            } else {
                JOptionPane.showMessageDialog(null, "登陆失败，没有这个用户！");
                openUserSelectDialog();
            }
        } else {
            openNewUserDialog();
        }
    }

    private void openNewUserDialog() {
        String n = JOptionPane.showInputDialog("请输入要新增的用户的用户名");
        if (n == null) {
            JOptionPane.showMessageDialog(null, "新增失败，输入用户名为空");
            openNewUserDialog();
        } else if (userList.contains(n)) {
            JOptionPane.showMessageDialog(null, "新增失败，已经存在这个用户");
            openNewUserDialog();
        } else {
            JOptionPane.showMessageDialog(null, "新增成功！");
            userList.add(n);
            scoresList.add(0);
            currentUserId = userList.size() - 1;
            data.put("userList", gson.toJsonTree(userList, new TypeToken<ArrayList<String>>() {
            }.getType()));
            data.put("userScores", gson.toJsonTree(scoresList, new TypeToken<ArrayList<Integer>>() {
            }.getType()));
            FileUtils.saveDataToFile("data", gson.toJson(data), "json");
            setCurrentUser(n);
        }
    }

    private void setCurrentUser(String n) {
        currentUser = n;
        hasLogon = true;

        JLabel statusLabel = new JLabel("当前用户：" + n + "  胜局数：" + scoresList.get(currentUserId));
        statusLabel.setLocation(WIDTH / 15, (int) (HEIGHT / 13 * 11.25));
        statusLabel.setSize(400, 60);
        statusLabel.setFont(sansFont.deriveFont(Font.BOLD, 17));
        statusLabel.setForeground(Main.getThemeColor("indicatorBlack"));
        add(statusLabel);

        addBg();
        System.out.println(n);
    }

    private void addBg() {
        /*JLabel lbBgBox = new JLabel(ImageUtils.changeToOriginalSize(new ImageIcon("src/Resources/box.png"), (int) (WIDTH / 1.8), HEIGHT / 2));
        lbBgBox.setBounds((int) (WIDTH / 2 - WIDTH / 3.6), HEIGHT / 4, (int) (WIDTH / 1.8), HEIGHT / 2);
        add(lbBgBox);*/
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

    private void addTitle() {
        JLabel label = new JLabel("翻翻棋");
        label.setFont(titleFont.deriveFont(Font.BOLD, 58));
        label.setForeground(Main.getThemeColor("title"));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setLocation(WIDTH / 2 - 150, HEIGHT / 3 - 45);
        label.setSize(300, 90);
        add(label);
    }

    public static boolean checkRecords(String records) {
        return records.equals("--");
    }

    public static void setButtonBg(JButton button, int width, int height) {
        if (Main.theme.equals("像素")) {
            InputStream stream = Main.class.getResourceAsStream(((float) width / (float) height > 1.6) ?
                    Main.getThemeResource("md-btn") : Main.getThemeResource("btn"));
            try {
                button.setIcon(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()), width, height));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.CENTER);
            button.setContentAreaFilled(false);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
        } else {
            //button.setBackground(Color.decode("#dddddd"));
        }
    }

    private void addStartButton() {
        JButton button = new JButton("开始");
        button.setSize(250, 70);
        InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("startbtn"));
        try {
            button.setIcon(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()), 250, 70));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        button.setContentAreaFilled(false);
        button.setFont(titleFont.deriveFont(Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setLocation(WIDTH / 2 - 125, HEIGHT * 2 / 3 - 70);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addActionListener((e) -> {
            int option = JOptionPane.showOptionDialog(this, "选择什么游戏模式呢？", "请选择", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"AI 低难度", "AI 中难度", "本地 1v1"}, "AI 低难度");
            if (option == -1) System.out.println("取消");
            else if (option == 0)
                Main.startGame(1);
            else if (option == 1)
                Main.startGame(2);
            else
                Main.startGame(0);
        });

        add(button);
    }

    private void addRankButton() {
        JButton button = new JButton("排名");
        setButtonBg(button, 80, 60);
        button.setLocation((int) (WIDTH * 11.8 / 15), (int) (HEIGHT * 11.1 / 13));
        button.setSize(80, 60);
        button.setFont(sansFont.deriveFont(Font.BOLD, 18));
        add(button);

        button.addActionListener(e -> {
            Main.playNotifyMusic("click");
            Main.goRank();
        });
    }
}
