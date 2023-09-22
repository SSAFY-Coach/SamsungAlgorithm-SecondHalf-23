package SamsungAlgorithm_SecondHalf_23.CodeTree.놀이기구_탑승;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - N * N 크기의 격자
 *      - 최초에는 모든 칸이 비어있다.
 *
 *  2. 학생
 *      - 학생별로 좋아하는 학생이 정확히 4명씩 정해져있다.
 *      - 자기 자신을 좋아하는 경우는 없고 중복 번호가 주어지는 경우 또한 없다.
 *
 *  3. 최종 점수
 *      - 모든 학생들이 탑승한 이후, 각 학생마다의 점수를 합한 점수가 된다.
 *      - 각 학생 점수는 해당 학생의 인접한 곳에 앉아있는 좋아하는 친구의 수로 결정된다.
 *          - 0명 : 0
 *          - 1 ~ 4명 : 10 ^ (인접한 칸에 존재하는 좋아하는 친구 수 - 1)
 *
 * - 학생 탑승 조건 : 입력으로 주어진 순서대로 다음 조건에 따라 가장 우선순위가 높은 칸으로 탑승하려한다. 단, 항상 비어있는 칸으로만 이동한다.
 *  1. 격자를 벗어나지 않는 4방으로 인접한 칸 중 앉아있는 좋아하는 친구의 수가 가장 많은 위치로 간다.
 *  2. 만약 1번 조건을 만족하는 칸의 위치가 여러곳이면 그 칸들의 인접한 칸 중에서 빈 칸의 수가 가장 많은 위치로 간다. 이때 격자를 벗어나는 칸은 빈칸으로 간주하지 않는다.
 *  3. 2번까지 동일하면 행 번호가 가장 작은 위치로 간다.
 *  4. 3번까지 동일하면 열 번호가 가장 적은 위치로 간다.
 *
 *  answer : 들어오는 학생의 순서와 학생마다 좋아하는 4명의 학생 번호가 주어졌을때, 최종 점수를 구하라.
 *
 */
public class 놀이기구_탑승 {

    static class Point {
        int x, y, fCnt, bCnt;

        public Point(int x, int y, int fCnt, int bCnt) {
            this.x = x;
            this.y = y;
            this.fCnt = fCnt;
            this.bCnt = bCnt;
        }

        public boolean isHigher(Point np) {
            if (this.fCnt != np.fCnt) return this.fCnt > np.fCnt;
            if (this.bCnt != np.bCnt) return this.bCnt > np.bCnt;
            if (this.x != np.x) return this.x < np.x;
            return this.y < np.y;
        }
    }

    static int N, ans;
    static int[][] board;
    static int[] friends;
    static boolean[][] relation;

    static int[] dx = {-1, 0, 0, 1};
    static int[] dy = {0, -1, 1, 0};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        N = Integer.parseInt(br.readLine());

        board = new int[N][N];
        friends = new int[N * N];
        relation = new boolean[N * N][N * N];

        for (int i = 0; i < N * N; i++) {
            st = new StringTokenizer(br.readLine());

            int s = Integer.parseInt(st.nextToken()) - 1;
            friends[i] = s;

            for (int j = 0; j < 4; j++) {
                int r = Integer.parseInt(st.nextToken()) - 1;
                relation[s][r] = true;
            }

        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = -1;
            }
        }


    }
    public static void main(String[] args) throws Exception {
        input();

        for (int i = 0; i < N * N; i++) {
            int num = friends[i];
            seat(num);
        }

        ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int cnt = getFriendCnt(i, j);
                ans += getTotalScore(cnt);
            }
        }
        System.out.println(ans);
    }

    static void seat(int num) {
        Point best = new Point(N, N, 0, 0);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == -1) {
                    Point next = getPoint(num, i, j);

                    if (next.isHigher(best)) {
                        best = next;
                    }
                }
            }
        }

        board[best.x][best.y] = num;
    }

    static Point getPoint(int num, int x, int y) {
        int fCnt = 0;
        int bCnt = 0;

        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];

            if (isNotBoundary(nx, ny)) continue;

            // 빈칸 이면
            if (board[nx][ny] == -1) bCnt++;
            // 좋아하는 친구이면
            else if (isRelation(num, board[nx][ny])) fCnt++;
        }

        return new Point(x, y, fCnt, bCnt);
    }

    static boolean isNotBoundary(int x, int y) {
        return !(0 <= x && x < N && 0 <= y && y < N);
    }

    static boolean isRelation(int f1, int f2) {
        return relation[f1][f2];
    }

    static int getFriendCnt(int x, int y) {
        int cnt = 0;

        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];

            if (isNotBoundary(nx, ny)) continue;

            if (isRelation(board[x][y], board[nx][ny])) cnt++;
        }

        return cnt;
    }

    static int getTotalScore(int cnt) {
        int score = 0;

        if (cnt > 0) score = (int) Math.pow(10, cnt - 1);

        return score;
    }

}
