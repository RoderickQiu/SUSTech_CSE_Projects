package Chess;

import Chess.model.ChessColor;
import Chess.utils.FileUtils;
import Chess.view.ChessGameFrame;
import Chess.view.RankFrame;
import Chess.view.StartFrame;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static Chess.utils.GeneralUtils.log;

public class Main {
    private static ChessGameFrame gameFrame = null;
    private static StartFrame startFrame = null;
    public static Font titleFont, sansFont, serifFont;
    public static String theme = "像素", currentUser = "";
    public static final String[] themeList = {"典雅", "激情", "像素"};

    private static RankFrame rankFrame = null;
    public static boolean hasLogon = false;
    public static int currentUserId = 0, mode = 0;
    public static Map<String, JsonElement> data;
    public static ArrayList<String> userList = new ArrayList<>();
    public static ArrayList<Integer> scoresList = new ArrayList<>();

    public static void main(String[] args) {
        Gson gson = new Gson();

        FlatLightLaf.setup();

        String raw = FileUtils.getDataFromFile("data");
        if (raw == null) raw = "{}";
        try {
            data = JsonParser.parseString(raw).getAsJsonObject().asMap();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        if (gson.fromJson(data.get("userList"), new TypeToken<ArrayList<String>>() {
        }.getType()) == null)
            userList.add("test");
        else
            userList = gson.fromJson(data.get("userList"), new TypeToken<ArrayList<String>>() {
            }.getType());
        if (gson.fromJson(data.get("userScores"), new TypeToken<ArrayList<Integer>>() {
        }.getType()) == null)
            scoresList.add(0);
        else
            scoresList = gson.fromJson(data.get("userScores"), new TypeToken<ArrayList<Integer>>() {
            }.getType());
        theme = gson.fromJson(data.get("theme"), new TypeToken<String>() {
        }.getType()) == null ? "像素" : gson.fromJson(data.get("theme"), new TypeToken<String>() {
        }.getType());

        try {
            InputStream stream = Main.class.getResourceAsStream("Resources/FZShiGKSJW.TTF");
            titleFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (Exception e) {
            titleFont = Font.getFont(Font.SERIF);
        }
        try {
            InputStream stream = Main.class.getResourceAsStream("Resources/SweiSpringCJKtc-Medium.ttf");
            serifFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (Exception e) {
            serifFont = Font.getFont(Font.SERIF);
        }
        try {
            InputStream stream = Main.class.getResourceAsStream("Resources/SourceHanSansCN-Regular.otf");
            sansFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (Exception e) {
            sansFont = Font.getFont(Font.SANS_SERIF);
        }

        SwingUtilities.invokeLater(Main::backStart);

        playBGMusic();
    }

    public static void startGame(int mode) {
        Main.mode = mode;
        gameFrame = new ChessGameFrame(480, 720, mode);
        gameFrame.setVisible(true);
        if (startFrame != null) startFrame.dispose();
        if (rankFrame != null) rankFrame.dispose();
        playNotifyMusic("gamestart");
    }

    public static void goRank() {
        rankFrame = new RankFrame(480, 720);
        rankFrame.setVisible(true);
        if (gameFrame != null) gameFrame.dispose();
        if (startFrame != null) startFrame.dispose();
    }

    public static void refreshGame() {
        if (gameFrame != null) gameFrame.dispose();
        if (rankFrame != null) rankFrame.dispose();
        gameFrame = new ChessGameFrame(480, 720, mode);
        gameFrame.setVisible(true);
    }

    public static void backStart() {
        startFrame = new StartFrame(480, 720);
        startFrame.setVisible(true);
        if (rankFrame != null) rankFrame.dispose();
        if (gameFrame != null) gameFrame.dispose();
    }


    public static String getThemeResource(String type) {
        switch (type) {
            case "bg":
                if (theme.equals(themeList[0])) return "Resources/background1.jpg";
                if (theme.equals(themeList[1])) return "Resources/background2.jpg";
                else return "Resources/background3.jpg";
            case "startbtn":
                if (theme.equals(themeList[0])) return "Resources/button2.png";
                if (theme.equals(themeList[1])) return "Resources/button1.png";
                else return "Resources/button3.png";
            case "btn":
                if (theme.equals(themeList[2])) return "Resources/pixel-btn.png";
                else return "Resources/normal-btn.png";
            case "md-btn":
                if (theme.equals(themeList[2])) return "Resources/pixel-btn-md.png";
                else return "Resources/normal-btn.png";
            case "board":
                if (theme.equals(themeList[0])) return "Resources/board1.png";
                if (theme.equals(themeList[1])) return "Resources/board2.png";
                else return "Resources/board3.png";
            case "chess-pixel":
                return "Resources/pixel-chess.png";
            case "chess-pixel-sel":
                return "Resources/pixel-chess-sel.png";
            case "chess-pixel-opq":
                return "Resources/pixel-chess-opq.png";
            case "chessfill":
                if (theme.equals(themeList[0])) return "#f6b731";
                else return "#E28B24";
            case "chessfillopaque":
                if (theme.equals(themeList[0])) return "#f2deb2";
                else return "#dca35f";
            case "chessborder":
                if (theme.equals(themeList[0])) return "#e3914a";
                else return "#B3391F";
        }
        return "";
    }

    public static Color getThemeColor(String type) {
        switch (type) {
            case "indicatorBlack":
                if (theme.equals(themeList[0]) || theme.equals(themeList[2]))
                    return ChessColor.BLACK.getColor();
                else return Color.WHITE;
            case "indicatorRed":
                return ChessColor.RED.getColor();
            case "title":
                if (theme.equals(themeList[0])) return Color.WHITE;
                else return Color.BLACK;
            case "black":
                return Color.BLACK;
        }
        return Color.BLACK;
    }

    public static void playBGMusic() {
        playMusic("Resources/bgm1.wav", 0f, true);
    }

    public static void playNotifyMusic(String id) {
        playMusic("Resources/" + id + ".wav", (id.equals("click") ? 5f : 0f), false);
    }

    private static void playMusic(String filePath, float volume, boolean shouldLoop) {
        try {
            if (Main.class.getResourceAsStream(filePath) != null) {
                BufferedInputStream musicPath =
                        new BufferedInputStream(Objects.requireNonNull(Main.class.getResourceAsStream(filePath)));
                Clip clip1;
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip1 = AudioSystem.getClip();
                clip1.open(audioInput);
                FloatControl gainControl = (FloatControl) clip1.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume); //设置音量，范围为 -60.0f 到 6.0f
                clip1.start();
                clip1.loop(shouldLoop ? Clip.LOOP_CONTINUOUSLY : 0);
            } else {
                log("Cannot find music");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
