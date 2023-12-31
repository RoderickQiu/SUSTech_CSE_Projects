package Chess.utils;

import Chess.view.ChessGameFrame;
import Chess.view.Chessboard;

import javax.swing.*;

public class GeneralUtils {
    public static void log(Object log) {
        System.out.println(log);
    }

    public static String addPrefixZero(int num) {
        if (num >= 10 || num < 0) return String.valueOf(num);
        else return "0" + num;
    }
}
