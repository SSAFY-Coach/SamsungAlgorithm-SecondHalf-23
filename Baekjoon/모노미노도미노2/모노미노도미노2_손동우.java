import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    private static int N;
    private static int[][] greenMap = new int[6][4];
    private static int[][] blueMap = new int[4][6];
    private static Block[] blocks;

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        int count = countCell();
        System.out.println(answer);
        System.out.println(count);
    }

    private static int countCell() {
        int count = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                if (greenMap[i][j] != 0) {
                    count++;
                }
                if (blueMap[j][i] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        blocks = new Block[N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            int t = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            blocks[i] = new Block(t, y, x);
        }
    }

    private static int solution() {
        int score = 0;
        for (int i = 0; i < blocks.length; i++) {
            putBlockGreen(blocks[i], i + 1);
            putBlockBlue(blocks[i], i + 1);
            score += getScoreGreen() + getScoreBlue();
            removeBlock01Green();
            removeBlock01Blue();
        }
        return score;
    }

    private static void putBlockGreen(Block block, int id) {
        for (int i = 0; i < 6; i++) {
            int y1 = i;
            int x1 = block.x[0];
            int y2 = i + (block.y[1] - block.y[0]);
            int x2 = block.x[1];
            if (isInGreenMap(y1, x1) && isInGreenMap(y2, x2)) {
                if (greenMap[y1][x1] == 0 && greenMap[y2][x2] == 0) {
                    if (isInGreenMap(y1 + 1, x1) && isInGreenMap(y2 + 1, x2)) {
                        if (greenMap[y1 + 1][x1] != 0 || greenMap[y2 + 1][x2] != 0) {
                            greenMap[y1][x1] = id;
                            greenMap[y2][x2] = id;
                            break;
                        }
                    } else {
                        greenMap[y1][x1] = id;
                        greenMap[y2][x2] = id;
                        break;
                    }
                }
            }
        }
    }

    private static void putBlockBlue(Block block, int id) {
        for (int i = 0; i < 6; i++) {
            int y1 = block.y[0];
            int x1 = i;
            int y2 = block.y[1];
            int x2 = i + (block.x[1] - block.x[0]);
            if (isInBlueMap(y1, x1) && isInBlueMap(y2, x2)) {
                if (blueMap[y1][x1] == 0 && blueMap[y2][x2] == 0) {
                    if (isInBlueMap(y1, x1 + 1) && isInBlueMap(y2, x2 + 1)) {
                        if (blueMap[y1][x1 + 1] != 0 || blueMap[y2][x2 + 1] != 0) {
                            blueMap[y1][x1] = id;
                            blueMap[y2][x2] = id;
                            break;
                        }
                    } else {
                        blueMap[y1][x1] = id;
                        blueMap[y2][x2] = id;
                        break;
                    }
                }
            }
        }
    }

    private static void removeBlock01Green() {
        while (hasBlock01Green()) {
            unshiftGreen(5);
        }
    }

    private static boolean hasBlock01Green() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (greenMap[i][j] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeBlock01Blue() {
        while (hasBlock01Blue()) {
            unshiftBlue(5);
        }
    }

    private static boolean hasBlock01Blue() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (blueMap[j][i] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int getScoreGreen() {
        int score = 0;
        for (int i = 5; i >= 2; i--) {
            if (isFullGreen(i)) {
                score++;
                unshiftGreen(i);
                ++i;
            }
        }
        return score;
    }

    private static boolean isFullGreen(int i) {
        for (int j = 0; j < 4; j++) {
            if (greenMap[i][j] == 0) {
                return false;
            }
        }
        return true;
    }

    private static int getScoreBlue() {
        int score = 0;
        for (int i = 5; i >= 2; i--) {
            if (isFullBlue(i)) {
                score++;
                unshiftBlue(i);
                ++i;
            }
        }
        return score;
    }

    private static boolean isFullBlue(int i) {
        for (int j = 0; j < 4; j++) {
            if (blueMap[j][i] == 0) {
                return false;
            }
        }
        return true;
    }

    private static void unshiftGreen(int from) {
        for (int i = from; i > 0; i--) {
            for (int j = 0; j < 4; j++) {
                greenMap[i][j] = greenMap[i - 1][j];
            }
        }
        Arrays.fill(greenMap[0], 0);
    }

    private static void unshiftBlue(int from) {
        for (int i = from; i > 0; i--) {
            for (int j = 0; j < 4; j++) {
                blueMap[j][i] = blueMap[j][i - 1];
            }
        }
        for (int j = 0; j < 4; j++) {
            blueMap[j][0] = 0;
        }
    }


    private static boolean isInGreenMap(int y, int x) {
        return 0 <= y && y < 6 && 0 <= x && x < 4;
    }

    private static boolean isInBlueMap(int y, int x) {
        return 0 <= y && y < 4 && 0 <= x && x < 6;
    }

    private static class Block {

        int[] y = new int[2];
        int[] x = new int[2];

        public Block(int t, int y, int x) {
            this.y[0] = y;
            this.x[0] = x;
            if (t == 1) {
                this.y[1] = y;
                this.x[1] = x;
            } else if (t == 2) {
                this.y[1] = y;
                this.x[1] = x + 1;
            } else {
                this.y[1] = y + 1;
                this.x[1] = x;
            }
        }
    }
}