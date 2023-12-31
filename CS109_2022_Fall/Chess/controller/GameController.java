package Chess.controller;

import Chess.chessComponent.*;
import Chess.model.ChessBackup;
import Chess.model.ChessboardPoint;
import Chess.utils.FileUtils;
import Chess.view.Chessboard;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static Chess.utils.FileUtils.getCorrectSlash;
import static Chess.view.Chessboard.COL_SIZE;
import static Chess.view.Chessboard.ROW_SIZE;
import static Chess.view.StartFrame.checkRecords;

/**
 * 这个类主要完成由窗体上组件触发的动作。
 * 例如点击button等
 * ChessGameFrame中组件调用本类的对象，在本类中的方法里完成逻辑运算，将运算的结果传递至chessboard中绘制
 */
public class GameController {
    private Chessboard chessboard;
    public Gson gson = new Gson();

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<String> loadGameFromFile(String path) {
        try {
            List<String> chessData = Files.readAllLines(Path.of(path));
            //chessboard.loadGame(chessData);
            return chessData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void toGSon() {
        ChessBackup backup = chessboard.backupGame();
        String[][] reversalString = new String[ROW_SIZE][COL_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                reversalString[i][j] = String.valueOf(backup.getReversal()[i][j]);
            }
        }
        Map<String, JsonElement> data = new HashMap<>();
        data.put("nowColor", gson.toJsonTree(backup.getNowColor()));
        data.put("type", gson.toJsonTree(backup.getType()));
        data.put("color", gson.toJsonTree(backup.getColor()));
        data.put("reversal", gson.toJsonTree(reversalString));
        data.put("records", gson.toJsonTree(backup.getRecords()));
        data.put("redEatenList", gson.toJsonTree(backup.getRedEatenList()));
        data.put("blackEatenList", gson.toJsonTree(backup.getBlackEatenList()));
        data.put("redScore", gson.toJsonTree(backup.getRedScore()));
        data.put("blackScore", gson.toJsonTree(backup.getBlackScore()));
        try {
            FileUtils.saveDataToFile("save", Base64.getEncoder().encodeToString(gson.toJson(data).getBytes(StandardCharsets.UTF_8)), "txt");
            JOptionPane.showMessageDialog(null, "保存在 " + System.getProperty("user.dir") + getCorrectSlash() + "save.txt");
        } catch (Exception e) {
            System.out.println("ERR: " + e.getMessage());
        }
    }

    private boolean checkChess(String[][] type, String[][] color) {
        String typeStr = "class Chess.chessComponent.ShuaiComponent class Chess.chessComponent.ShiComponent class Chess.chessComponent.XiangComponent class Chess.chessComponent.JuComponent class Chess.chessComponent.MaComponent class Chess.chessComponent.MaComponent class Chess.chessComponent.ZuComponent class Chess.chessComponent.PaoComponent class Chess.chessComponent.EmptySlotComponent";
        final int[] maxList = {1, 2, 2, 2, 2, 5, 2};
        int[] blackList = {0, 0, 0, 0, 0, 0, 0}, redList = {0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                if (!typeStr.contains(type[i][j])) return false;
                if (!type[i][j].equals("class Chess.chessComponent.EmptySlotComponent")) {
                    if (!color[i][j].equals("b") && !color[i][j].equals("r")) return false;
                    if (color[i][j].equals("b")) {
                        switch (type[i][j]) {
                            case "class Chess.chessComponent.ShuaiComponent" -> blackList[0]++;
                            case "class Chess.chessComponent.ShiComponent" -> blackList[1]++;
                            case "class Chess.chessComponent.XiangComponent" -> blackList[2]++;
                            case "class Chess.chessComponent.JuComponent" -> blackList[3]++;
                            case "class Chess.chessComponent.MaComponent" -> blackList[4]++;
                            case "class Chess.chessComponent.ZuComponent" -> blackList[5]++;
                            case "class Chess.chessComponent.PaoComponent" -> blackList[6]++;
                        }
                    } else {
                        switch (type[i][j]) {
                            case "class Chess.chessComponent.ShuaiComponent" -> redList[0]++;
                            case "class Chess.chessComponent.ShiComponent" -> redList[1]++;
                            case "class Chess.chessComponent.XiangComponent" -> redList[2]++;
                            case "class Chess.chessComponent.JuComponent" -> redList[3]++;
                            case "class Chess.chessComponent.MaComponent" -> redList[4]++;
                            case "class Chess.chessComponent.ZuComponent" -> redList[5]++;
                            case "class Chess.chessComponent.PaoComponent" -> redList[6]++;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 7; i++) {
            if (blackList[i] > maxList[i]) return false;
            if (redList[i] > maxList[i]) return false;
        }

        return true;
    }

    public void loadGSon(String path) {
        if (!path.split("\\.")[path.split("\\.").length - 1].equals("txt")) {
            JOptionPane.showMessageDialog(null, "101 文件格式错误，没有以 .txt 结尾！");
            return;
        }

        try {
            String input = FileUtils.getDataFromFileInAbsolutePath(path);
            String decoded = new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);

            Map<String, JsonElement> data = JsonParser.parseString(decoded).getAsJsonObject().asMap();
            String nowColor = gson.fromJson(data.get("nowColor"), new TypeToken<String>() {
            }.getType());
            String[][] type = gson.fromJson(data.get("type"), new TypeToken<String[][]>() {
            }.getType());
            String[][] color = gson.fromJson(data.get("color"), new TypeToken<String[][]>() {
            }.getType());
            String[][] reversal = gson.fromJson(data.get("reversal"), new TypeToken<String[][]>() {
            }.getType());
            String records = gson.fromJson(data.get("records"), new TypeToken<String>() {
            }.getType());
            String redEatenList = gson.fromJson(data.get("redEatenList"), new TypeToken<String>() {
            }.getType());
            String blackEatenList = gson.fromJson(data.get("blackEatenList"), new TypeToken<String>() {
            }.getType());
            int redScore = (gson.fromJson(data.get("redScore"), new TypeToken<Integer>() {
            }.getType()) == null) ? 0 : gson.fromJson(data.get("redScore"), new TypeToken<Integer>() {
            }.getType());
            int blackScore = (gson.fromJson(data.get("blackScore"), new TypeToken<Integer>() {
            }.getType()) == null) ? 0 : gson.fromJson(data.get("blackScore"), new TypeToken<Integer>() {
            }.getType());

            boolean[][] reversalBoolean = new boolean[ROW_SIZE][COL_SIZE];
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    reversalBoolean[i][j] = reversal[i][j].equals("true");
                }
            }

            if (type.length != ROW_SIZE || type[0].length != COL_SIZE) {
                JOptionPane.showMessageDialog(null, "102 棋盘尺寸错误！");
            } else if (!checkChess(type, color)) {
                JOptionPane.showMessageDialog(null, "103 棋子并非红黑棋子/空格或某种棋子数不对！");
            } else if (nowColor == null) {
                JOptionPane.showMessageDialog(null, "104 缺少行棋方！");
            } else if (!checkRecords(records)) {
                JOptionPane.showMessageDialog(null, "105 行棋步骤不合理！");
            } else {
                ChessBackup backup = new ChessBackup(nowColor, type, color, reversalBoolean, records, redEatenList, blackEatenList, redScore, blackScore);
                System.out.println(backup);
                chessboard.loadGame(backup);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "未知错误！");
            System.out.println("导入时发生未知错误" + e.getMessage());
        }
    }
}
