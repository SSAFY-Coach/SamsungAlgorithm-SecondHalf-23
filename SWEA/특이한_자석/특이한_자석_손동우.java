package SamsungAlgorithm_SecondHalf_23.SWEA.특이한_자석;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class 특이한_자석_손동우 {
    private static final int N = 0;
    private static final int S = 1;
    private static final int NUMBER_OF_MAGNETS = 4;
    private static final int NUMBER_OF_BLADES = 8;
    private static int T;
    private static int K;
    private static int[][] magnets = new int[NUMBER_OF_MAGNETS][8];
    private static int[] magnetPoints = new int[NUMBER_OF_MAGNETS];
    private static Rotate[] rotates;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        T = Integer.parseInt(br.readLine());
        for (int tc = 1; tc <= T; tc++) {
            init(br);
            int answer = solution();
            System.out.println("#" + tc + " " + answer);
        }
    }

    private static void init(BufferedReader br) throws Exception {
        K = Integer.parseInt(br.readLine());
        rotates = new Rotate[K];
        StringTokenizer st;
        for (int i = 0; i < NUMBER_OF_MAGNETS; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 8; j++) {
                magnets[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int number = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());
            rotates[i] = new Rotate(number, direction);
        }
        Arrays.fill(magnetPoints, 0);
    }

    private static int solution() {
        for (Rotate rotate : rotates) {
            doRotate(rotate);
        }
        return getScore();
    }

    private static void doRotate(Rotate rotate) {
        int[] isRotating = new int[NUMBER_OF_MAGNETS];
        isRotating[rotate.number] = rotate.direction;
        findRotate(rotate.number + 1, isRotating, -rotate.direction);
        findRotate(rotate.number - 1, isRotating, -rotate.direction);
        for (int i = 0; i < NUMBER_OF_MAGNETS; i++) {
            if (isRotating[i] == Rotate.CLOCKWISE) {
                rotateClockwise(i);
            } else if (isRotating[i] == Rotate.COUNTERCLOCKWISE) {
                rotateCounterclockwise(i);
            }
        }
    }

    private static void findRotate(int number, int[] isRotating, int direction) {
        if (number < 0 || number >= NUMBER_OF_MAGNETS) {
            return;
        }
        if (number - 1 >= 0 && isRotating[number - 1] != 0) {
            if (magnets[number - 1][getBladeIndex(magnetPoints[number - 1] + 2)] != magnets[number][getBladeIndex(magnetPoints[number] - 2)]) {
                isRotating[number] = direction;
                findRotate(number + 1, isRotating, -direction);
            }
            return;
        }
        if (number + 1 < NUMBER_OF_MAGNETS && isRotating[number + 1] != 0) {
            if (magnets[number + 1][getBladeIndex(magnetPoints[number + 1] - 2)] != magnets[number][getBladeIndex(magnetPoints[number] + 2)]) {
                isRotating[number] = direction;
                findRotate(number - 1, isRotating, -direction);
            }
        }
    }

    private static void rotateClockwise(int number) {
        magnetPoints[number] = (magnetPoints[number] - 1 + NUMBER_OF_BLADES) % NUMBER_OF_BLADES;
    }

    private static void rotateCounterclockwise(int number) {
        magnetPoints[number] = (magnetPoints[number] + 1) % NUMBER_OF_BLADES;
    }

    private static int getBladeIndex(int index) {
        return (index + NUMBER_OF_BLADES) % NUMBER_OF_BLADES;
    }

    private static int getScore() {
        int score = 0;
        for (int i = 0; i < NUMBER_OF_MAGNETS; i++) {
            if (magnets[i][magnetPoints[i]] == S) {
                score += 1 << i;
            }
        }
        return score;
    }

    private static class Rotate {
        static final int CLOCKWISE = 1;
        static final int COUNTERCLOCKWISE = -1;
        int number;
        int direction;

        public Rotate(int number, int direction) {
            this.number = number - 1;
            this.direction = direction;
        }
    }
}