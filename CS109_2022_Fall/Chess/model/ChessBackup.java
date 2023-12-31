package Chess.model;

public class ChessBackup {
    String nowColor;
    String[][] type, color;
    boolean[][] reversal;
    String records = "--", redEatenList, blackEatenList;
    int redScore, blackScore;

    public ChessBackup(String nowColor, String[][] type, String[][] color, boolean[][] reversal, String records, String redEatenList, String blackEatenList, int redScore, int blackScore) {
        this.nowColor = nowColor;
        this.type = type;
        this.color = color;
        this.reversal = reversal;
        this.records = records;
        this.redEatenList = redEatenList;
        this.blackEatenList = blackEatenList;
        this.redScore = redScore;
        this.blackScore = blackScore;
    }

    public String twoDimenToString(String[][] myArray) {
        StringBuilder res = new StringBuilder();
        for (String[] strings : myArray) {
            for (int j = 0; j < myArray[0].length; j++)
                res.append(String.format("%s ", strings[j]));
            res.append(" | ");
        }
        return String.valueOf(res);
    }

    public String twoDimenToString(boolean[][] myArray) {
        StringBuilder res = new StringBuilder();
        for (boolean[] strings : myArray) {
            for (int j = 0; j < myArray[0].length; j++)
                res.append(String.format("%s ", strings[j]));
            res.append(" | ");
        }
        return String.valueOf(res);
    }

    @Override
    public String toString() {
        return "ChessBackup{" +
                "nowColor='" + nowColor + '\'' +
                ", type=" + twoDimenToString(type) +
                ", color=" + twoDimenToString(color) +
                ", reversal=" + twoDimenToString(reversal) +
                ", records='" + records + '\'' +
                ", redEatenList='" + redEatenList + '\'' +
                ", blackEatenList='" + blackEatenList + '\'' +
                ", redScore=" + redScore +
                ", blackScore=" + blackScore +
                '}';
    }

    public String getNowColor() {
        return nowColor;
    }

    public String[][] getType() {
        return type;
    }

    public String[][] getColor() {
        return color;
    }

    public boolean[][] getReversal() {
        return reversal;
    }

    public String getRecords() {
        return records;
    }

    public String getRedEatenList() {
        return redEatenList;
    }

    public String getBlackEatenList() {
        return blackEatenList;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getBlackScore() {
        return blackScore;
    }
}
