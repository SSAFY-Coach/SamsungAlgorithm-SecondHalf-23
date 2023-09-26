package SamsungAlgorithm_SecondHalf_23.CodeTree.나무타이쿤;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
/*
5 2
1 0 0 4 2
2 1 3 2 1
0 0 0 2 5
1 0 0 0 3
1 2 1 3 3
1 3
2 4

 */


/**
 * - 개체
 *  1. 격자
 *      - N * N 크기
 *
 *  2. 특수 영양제
 *      - 한 칸의 나무 높이를 1 증가 시킨다.
 *      - 땅에 씨앗만 있는 경우 높이 1의 나무로 만든다.
 *      - 초기 특수 영양제는 N * N 격자의 좌하단의 4개의 칸에 주어진다.
 *      - 특수 영양제는 이동 규칙에 따라 움직인다. 이동 규칙은 이동 방향과 이동 칸 수로 주어진다.
 *      - 이동 방향의 경우 1~8 까지 (우, 우상, 상, 좌상, 좌, 좌하, 하, 우하) 8 방으로 주어지며 이동 칸 수만큼 특수 영양제가 이동.
 *      - 격자의 모든 행, 열은 각각 끝과 끝이 연결되어 있다.
 *
 * - 진행과정
 *  1. 특수 영양제 이동 규칙에 따라 이동
 *  2. 특수 영양제 이동 후 해당 땅에 특수 영양제 투입
 *  3. 특수 영양제를 투입한 나무의 대각선 인접 방향에 높이가 1 이상인 나무(cnt 만큼)개수 만큼 높이가 더 성장한다. 대각선 인접 칸이 격자를 벗어나면 세지 않는다.
 *  4. 특수 영양제를 투입한 나무를 제외하고 높이가 2 이상인 나무는 높이 2를 베어서 잘라낸 나무로 특수 영양제를 사고, 해당 위치에 특수 영양제를 올려둔다.
 *
 *  m 년동안 1 ~ 4 번 진행
 *
 *  answer : 남아있는 나무 높이들의 총 합.
 */
public class 나무_타이쿤_서요셉 {

    static int N, M, ans;
    static int[][] board;
    static int[][] rules;

    static boolean[][] nutrients;
    static boolean[][] nextNutrients;

    // 우, 우상, 상, 좌상, 좌, 좌하, 하, 우하
    static int[] dx = {0, -1, -1, -1,  0,  1, 1, 1};
    static int[] dy = {1,  1,  0, -1, -1, -1, 0, 1};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new int[N][N];
        rules = new int[M][2];

        nutrients = new boolean[N][N];
        nextNutrients = new boolean[N][N];

        // 격자 정보 저장
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 영양제 이동 규칙 저장
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());

            int d = Integer.parseInt(st.nextToken()) - 1;
            int p = Integer.parseInt(st.nextToken());

            rules[i][0] = d;
            rules[i][1] = p;
        }

        // 초기 영양제 위치
        for (int i = N - 2; i < N; i++) {
            for (int j = 0; j < 2; j++) {
                nutrients[i][j] = true;
            }
        }
    }

    public static void main(String[] args) throws Exception{
        input();

        for (int i = 0; i < M; i++) {
            // 영양제 이동
            move(i);

            // 영양제 주입
            grow();

            // 대각 방향 나무 수 cnt 후 그만큼 성장
            cntTreeAndGrow();

            // 기존 영양제 위치 제외 2 이상 나무 높이 -2 감소 후 기존 영양제 초기화.
            // 새 영양제 위치 저장
            getNewNutrients();

        }

        // 격자에 남아있는 나무 높이 총 합
        ans = getHeight();
        System.out.println(ans);
    }

    static void move(int idx) {
        int d = rules[idx][0];
        int p = rules[idx][1];

        // 새로운 영양제 정보 초기화
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                nextNutrients[i][j] = false;
            }
        }

        // 영양제 이동
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (nutrients[i][j]) {
                   Point nPoint = nextPoint(i, j, d, p);

                   nextNutrients[nPoint.x][nPoint.y] = true;
                }
            }
        }

        // next 값을 nutrients 로 옮긴다.
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                nutrients[i][j] = nextNutrients[i][j];
            }
        }

    }

    static Point nextPoint(int x, int y, int d, int p) {
        p = p % N;
        int nx = x + dx[d] * p;
        int ny = y + dy[d] * p;

        if (nx < 0) nx += N;
        if (nx >= N) nx %= N;
        if (ny < 0) ny += N;
        if (ny >= N) ny %= N;

        return new Point(nx, ny);
    }

    static void grow() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (nutrients[i][j]) board[i][j]++;
            }
        }
    }

    static void cntTreeAndGrow() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (nutrients[i][j]) {
                    int cnt = cntTree(i, j);
                    board[i][j] += cnt;
                }
            }
        }

    }

    static int cntTree(int x, int y) {
        int cnt = 0;

        for (int i = 1; i < 8; i+=2) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (0 <= nx && nx < N && 0 <= ny && ny < N) {
                if (board[nx][ny] >= 1) {
                    cnt++;
                }
            }
        }

        return cnt;
    }

    static void getNewNutrients() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (nutrients[i][j]) nutrients[i][j] = false;

                else if (board[i][j] >= 2) {
                    board[i][j] -= 2;
                    nutrients[i][j] = true;
                }
            }
        }

    }

    static int getHeight() {
        int sum = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] > 0) sum += board[i][j];
            }
        }

        return sum;
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
