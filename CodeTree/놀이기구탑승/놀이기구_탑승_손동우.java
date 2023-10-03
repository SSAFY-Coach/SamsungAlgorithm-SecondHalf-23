package SamsungAlgorithm_SecondHalf_23.CodeTree.놀이기구탑승;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class 놀이기구_탑승_손동우 {
    private static final int[] dy = {-1, 1, 0, 0};
    private static final int[] dx = {0, 0, -1, 1};
    private static int N;
    private static int[][] map;
    private static Set<Integer>[] favoriteNumberSets;
    private static List<Integer> orderList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        map = new int[N][N];
        favoriteNumberSets = new Set[N * N + 1];
        for (int i = 0; i < favoriteNumberSets.length; i++) {
            favoriteNumberSets[i] = new HashSet<>();
        }
        for (int i = 0; i < N * N; i++) {
            int[] numbers = new int[5];
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                numbers[j] = Integer.parseInt(st.nextToken());
            }
            orderList.add(numbers[0]);
            for (int j = 0; j < 4; j++) {
                favoriteNumberSets[numbers[0]].add(numbers[1 + j]);
            }
        }
    }

    private static int solution() {
        for (int boardOrder : orderList) {
            board(boardOrder);
        }
        return getScore();
    }

    private static void board(int studentNumber) {
        int maxFavoriteNumbersCount = -1;
        int maxVacantCount = -1;
        int studentY = 0;
        int studentX = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] == 0) {
                    int favoriteNumbersCount = countFavoriteNumbers(i, j, studentNumber);
                    if (favoriteNumbersCount > maxFavoriteNumbersCount) {
                        maxFavoriteNumbersCount = favoriteNumbersCount;
                        studentY = i;
                        studentX = j;
                        int vacantCount = countVacant(i, j);
                        maxVacantCount = vacantCount;
                    } else if (favoriteNumbersCount == maxFavoriteNumbersCount) {
                        int vacantCount = countVacant(i, j);
                        if (vacantCount > maxVacantCount) {
                            maxVacantCount = vacantCount;
                            studentY = i;
                            studentX = j;
                        }
                    }
                }
            }
        }
        map[studentY][studentX] = studentNumber;
    }

    private static int countFavoriteNumbers(int y, int x, int studentNumber) {
        int favoriteNumbersCount = 0;
        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (isInMap(ny, nx) && favoriteNumberSets[studentNumber].contains(map[ny][nx])) {
                favoriteNumbersCount++;
            }
        }
        return favoriteNumbersCount;
    }

    private static int countVacant(int y, int x) {
        int vacantCount = 0;
        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (isInMap(ny, nx) && map[ny][nx] == 0) {
                vacantCount++;
            }
        }
        return vacantCount;
    }

    private static int getScore() {
        int score = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int favoriteNumbersCount = countFavoriteNumbers(i, j, map[i][j]);
                if (favoriteNumbersCount > 0) {
                    score += Math.pow(10, favoriteNumbersCount - 1);
                }
            }
        }
        return score;
    }

    private static boolean isInMap(int y, int x) {
        return 0 <= y && y < N && 0 <= x && x < N;
    }

    private static void printMap() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}