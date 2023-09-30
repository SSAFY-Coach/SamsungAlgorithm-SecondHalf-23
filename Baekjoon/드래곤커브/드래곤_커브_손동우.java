import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    private static final int MAX_LENGTH = 100;
    private static final int[] dy = {0, -1, 0, 1};
    private static final int[] dx = {1, 0, -1, 0};
    private static int N;
    private static DragonCurve[] curves;
    private static int[][] map = new int[MAX_LENGTH + 1][MAX_LENGTH + 1];

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        curves = new DragonCurve[N];
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());
            int generation = Integer.parseInt(st.nextToken());
            curves[i] = new DragonCurve(y, x, direction, generation);
        }
        for (int i = 0; i < 101; i++) {
            Arrays.fill(map[i], -1);
        }
    }

    private static int solution() {
        for (DragonCurve curve : curves) {
            drawDragonCurve(curve);
        }
        return findSquareAllDragonCurves();
    }

    private static void drawDragonCurve(DragonCurve curve) {
        List<Integer> directionList = new ArrayList<>();
        int curGeneration = 0;
        int curY = curve.y;
        int curX = curve.x;
        directionList.add(curve.direction);
        map[curY][curX] = curGeneration;
        curY += dy[curve.direction];
        curX += dx[curve.direction];
        map[curY][curX] = curGeneration;

        while (curGeneration < curve.generation) {
            curGeneration++;
            for (int i = directionList.size() - 1; i >= 0; i--) {
                int curDirection = directionList.get(i);
                curDirection = (curDirection + 1) % 4;
                curY += dy[curDirection];
                curX += dx[curDirection];
                directionList.add(curDirection);
                map[curY][curX] = curGeneration;
            }
        }
    }

    private static int findSquareAllDragonCurves() {
        int squareCount = 0;
        for (int i = 0; i < MAX_LENGTH; i++) {
            for (int j = 0; j < MAX_LENGTH; j++) {
                if (isAllDragonCurves(i, j)) {
                    squareCount++;
                }
            }
        }
        return squareCount;
    }

    private static boolean isAllDragonCurves(int y, int x) {
        if (map[y][x] != -1 && map[y + 1][x] != -1 && map[y][x + 1] != -1 && map[y + 1][x + 1] != -1) {
            return true;
        } else {
            return false;
        }
    }

    private static class DragonCurve {
        int y;
        int x;
        int direction;
        int generation;

        public DragonCurve(int y, int x, int direction, int generation) {
            this.y = y;
            this.x = x;
            this.direction = direction;
            this.generation = generation;
        }
    }
}