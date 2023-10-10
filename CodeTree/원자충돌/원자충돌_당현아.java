import java.io.*;
import java.util.*;

public class Main {

    public static int N;    // 한 변의 사이즈 (4 <= n <= 50)
    public static int M;    // 원자의 개수 (0 <= m <= 2500)
    public static int K;    // 실험시간 (1 <= K <= 1000)
    public static int[][] DELTA = {
            {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
            {1, 0}, {1, -1}, {0, -1}, {-1, -1}
    };                      // 방향순서대로
    public static List<Core>[][] MAP;
    public static List<Core> CORE;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        init(br);

        for (int k = 0;k < K;k++) {
            coreMove();
            coreCheck();
        }

        System.out.println(getTotalM());
    }

    public static void coreMove() {
        for (Core core : CORE) {
            int nx = core.x;
            int ny = core.y + DELTA[core.d][1];

            for (int s = 0;s < core.s;s++) {
                nx += DELTA[core.d][0];
                ny += DELTA[core.d][1];

                nx = nx < 1 ? N : nx;
                nx = nx > N ? 1 : nx;
                ny = ny < 1 ? N : ny;
                ny = ny > N ? 1 : ny;
            }

            MAP[core.x][core.y].remove(core);
            MAP[nx][ny].add(core);

            core.x = nx;
            core.y = ny;
        }
    }

    public static void coreCheck() {
        for (int n = 1;n <= N;n++) {
            for (int m = 1;m <= N;m++) {
                if (MAP[n][m].size() <= 1) continue;

                int totalM = 0, totalS = 0, size = MAP[n][m].size();
                boolean isRowCol = true, isCross = true;

                for (Core core : MAP[n][m]) {
                    totalM += core.m;
                    totalS += core.s;
                    isRowCol &= core.d % 2 == 0;
                    isCross &= core.d % 2 == 1;
                    CORE.remove(core);
                }
                totalM = totalM / 5;

                if (totalM == 0) continue;
                totalS = totalS / size;
                int startIdx = isRowCol && isCross ? 1 : 0;

                for (int i = startIdx; i < 8;i += 2) {
                    Core core = new Core(n, m, totalM, totalS, i);
                    MAP[n][m].add(core);
                    CORE.add(core);
                }
            }
        }
    }

    public static int getTotalM() {
        int result = 0;
        for (Core core : CORE)
            result += core.m;
        return result;
    }

    public static void init(BufferedReader br) throws Exception {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        MAP = new List[N + 1][N + 1]; // 0 dummy
        CORE = new ArrayList<>();

        for (int n = 1;n <= N;n++)
            for (int m = 1;m <= N;m++)
                MAP[n][m] = new ArrayList<>();

        for (int i = 0;i < M;i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()); // 1 <= x <= 50
            int y = Integer.parseInt(st.nextToken()); // 1 <= x <= 50
            int m = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            Core core = new Core(x, y, m, s, d);
            MAP[x][y].add(core);
            CORE.add(core);
        }
    }

    public static class Core {
        int x, y, m, s, d;
        public Core(int x, int y, int m, int s, int d) {
            this.x = x;
            this.y = y;
            this.m = m; // 질량 (1 <= m <= 1000)
            this.s = s; // 속력 (1 <= s <= 1000)
            this.d = d; // 방향 (0 <= d <= 7)
        }

        @Override
        public String toString() {
            return "(x=" + x +
                    ", y=" + y +
                    ", m=" + m +
                    ", s=" + s +
                    ", d=" + d +
                    ')';
        }
    }
}
