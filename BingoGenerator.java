/* 各ライブラリのインポート */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BingoCard extends JFrame {
    private static final int GRID_SIZE = 5; // ビンゴカードのサイズ指定
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE]; // ボタンの配列
    private boolean[][] selected = new boolean[GRID_SIZE][GRID_SIZE]; // 選択済みボタン保持用の配列

    /* ウィンドウの作成 */
    public BingoCard() {
        setTitle("ビンゴカード生成システム Ver.1");
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeCard();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400); // ウィンドウサイズ指定
        setVisible(true);
    }

    /* ビンゴカード初期化 */
    private void initializeCard() {
        int[][] cardNumbers = generateCard();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int number = cardNumbers[row][col];
                if (number == 0) {
                    buttons[row][col] = new JButton("FREE");  // FREEを表示
                } else {
                    buttons[row][col] = new JButton(String.valueOf(number));
                    selected[row][col] = false;
                }
                buttons[row][col].setMargin(new Insets(0, 0, 0, 0)); // ボタンの余白を0に設定
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 18)); // ボタンのフォントを設定
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                add(buttons[row][col]);
                selected[row][col] = false;
            }
        }
    }

    /* ビンゴカード生成 */
    private int[][] generateCard() {
        int[][] cardNumbers = new int[GRID_SIZE][GRID_SIZE];
        Random rand = new Random();
        Set<Integer> usedNumbers = new HashSet<>();

        /* 各列ごとに異なる番号範囲（1〜15、16〜30...）でランダムな番号を生成 */
        for (int col = 0; col < GRID_SIZE; col++) {
            int min = col * 15 + 1;
            int max = col * 15 + 15;
            for (int row = 0; row < GRID_SIZE; row++) {
                int num;
                do {
                    num = rand.nextInt(max - min + 1) + min;
                } while (usedNumbers.contains(num));
                cardNumbers[row][col] = num;
                usedNumbers.add(num);
            }
        }

        cardNumbers[2][2] = 0;  // 中央をFREEに
        return cardNumbers;
    }

    /* ボタンクリック時の動作 */
    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selected[row][col] = !selected[row][col]; // 未選択と選択済みの状態を入れ替え
            buttons[row][col].setBackground(selected[row][col] ? Color.CYAN : null); // 選択済みボタンの色を変更
            checkBingo();
        }
    }

    /* リーチ、ビンゴの判定 */
    private void checkBingo() {
        boolean bingo = false;
        int reachCount = 0;

        /* ビンゴ判定 */
        for (int i = 0; i < GRID_SIZE; i++) {
            if (checkLine(selected[i])) bingo = true;
            if (checkColumn(i)) bingo = true;
        }
        if (checkDiagonal()) bingo = true;

        /* リーチ判定 */
        for (int i = 0; i < GRID_SIZE; i++) {
            if (isReach(selected[i])) reachCount++;
            if (isReachColumn(i)) reachCount++;
        }
        if (isReachDiagonal()) reachCount++;

        if (bingo) {
            JOptionPane.showMessageDialog(this, "ビンゴ！"); // ビンゴのメッセージを表示
        } else if (reachCount > 0) {
            JOptionPane.showMessageDialog(this, "リーチ！"); // リーチのメッセージを表示
        }
    }

    /* 行のチェック */
    private boolean checkLine(boolean[] line) {
        for (boolean selected : line) if (!selected) return false;
        return true;
    }

    /* 列のチェック */
    private boolean checkColumn(int col) {
        for (int row = 0; row < GRID_SIZE; row++) if (!selected[row][col]) return false;
        return true;
    }

    /* 斜めのチェック */
    private boolean checkDiagonal() {
        boolean leftToRight = true, rightToLeft = true;
        for (int i = 0; i < GRID_SIZE; i++) {
            leftToRight &= selected[i][i];
            rightToLeft &= selected[i][GRID_SIZE - 1 - i];
        }
        return leftToRight || rightToLeft;
    }

    /* 行のリーチのチェック */
    private boolean isReach(boolean[] line) {
        int count = 0;
        for (boolean selected : line) if (selected) count++;
        return count == GRID_SIZE - 1;
    }

    /* 列のリーチのチェック */
    private boolean isReachColumn(int col) {
        int count = 0;
        for (int row = 0; row < GRID_SIZE; row++) if (selected[row][col]) count++;
        return count == GRID_SIZE - 1;
    }

    /* 斜めのリーチのチェック */
    private boolean isReachDiagonal() {
        int countLeftToRight = 0, countRightToLeft = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (selected[i][i]) countLeftToRight++;
            if (selected[i][GRID_SIZE - 1 - i]) countRightToLeft++;
        }
        return countLeftToRight == GRID_SIZE - 1 || countRightToLeft == GRID_SIZE - 1;
    }

    /* mainメソッド */
    public static void main(String[] args) {
        new BingoCard();
    }
}
