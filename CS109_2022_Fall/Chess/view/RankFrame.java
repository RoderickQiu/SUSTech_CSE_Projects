package Chess.view;

import Chess.Main;
import Chess.utils.ImageUtils;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static Chess.Main.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class RankFrame extends JFrame {
    private final int WIDTH;
    private final int HEIGHT;
    private Gson gson = new Gson();

    public RankFrame(int width, int height) {
        setTitle("本地玩家排行");
        this.WIDTH = width;
        this.HEIGHT = height;

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        addBackButton();
        addList();

        addBg();
    }

    private void addList() {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < scoresList.size(); i++) {
            arr.add(" " + userList.get(i) + " " + scoresList.get(i));
        }
        arr.sort((o1, o2) -> Integer.compare(Integer.parseInt(o2.split(" ")[2]),
                Integer.parseInt(o1.split(" ")[2])));
        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, "   " + arr.get(i));
        }
        JList<String> list = new JList<>(arr.toArray(new String[0]));
        list.setBackground(new Color(0, 0, 0, 0));
        list.setOpaque(false);//workaround keep opaque after repainted
        list.setFont(sansFont.deriveFont(Font.PLAIN, 18));
        list.setBounds(HEIGHT / 13, HEIGHT / 13 + 12, HEIGHT * 2 / 5, HEIGHT * 4 / 5);
        add(list);

        // chess board as bg workaround
        InputStream stream = Main.class.getResourceAsStream(Main.getThemeResource("board"));
        JLabel lbBg = null;
        try {
            lbBg = new JLabel(ImageUtils.changeToOriginalSize(new ImageIcon(stream.readAllBytes()), HEIGHT * 2 / 5, HEIGHT * 4 / 5));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        lbBg.setBounds(HEIGHT / 13, HEIGHT / 13, HEIGHT * 2 / 5, HEIGHT * 4 / 5);
        add(lbBg);
    }


    private void addBackButton() {
        JButton button = new JButton("返回");
        StartFrame.setButtonBg(button, 80, 60);
        button.setFont(sansFont.deriveFont(Font.BOLD, 20));
        button.addActionListener((e) -> {
            Main.backStart();
            Main.playNotifyMusic("click");
        });
        button.setLocation(WIDTH * 11 / 15, HEIGHT / 13);
        button.setSize(80, 60);
        add(button);
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
}
