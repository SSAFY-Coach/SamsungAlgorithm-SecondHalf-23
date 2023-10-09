package SamsungAlgorithm_SecondHalf_23.CodeTree.바이러스백신;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * - 개체
 *  1. 격자
 *      - N * N 크기 격자
 *      - 병원, 벽, 바이러스 정보를 나타냄
 *      - 병원, 벽을 제외한 모든 지역에 바이러스가 생김
 *
 * - 진행
 *  1. 병원고르기
 *      - M 개를 고른다.
 *
 *  2. 바이러스 삭제
 *      - 고른 병원들을 시작으로 매 초 상하좌우로 인접한 지역 중 벽을 제외한 지역에 백신이 공급되면서 그 자리에 있는 바이러스는 사라진다.
 *
 *  3. 격자 위에 바이러스 생존 여부 확인
 *      - 격자를 순회하며 하나라도 바이러스가 남아있다면 answer 는 -1 이다.
 *
 * - answer : M 개의 병원을 적절히 골라 바이러스를 전부 없애는데 걸리는 최소 시간을 구한다.
 *            만약 모든 바이러스를 없앨 수 없다면 -1 을 출력한다.
 *
 */
public class 바이러스백신_서요셉 {

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static int N, M, answer;            // 격자 크기 N (3 <= N <= 50), 골라야할 병원 수
    static int[][] origin;
    static int[][] board;

    static List<Point> hospitals;
    static Point[] selected;

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        answer = Integer.MAX_VALUE;

        origin = new int[N][N];


        hospitals = new ArrayList<>();
        selected = new Point[M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                origin[i][j] = -Integer.parseInt(st.nextToken());

                if (origin[i][j] == -2) hospitals.add(new Point(i, j));
            }
        }

    }
    public static void main(String[] args) throws Exception {
        input();

        // 병원 고르기
        selectHospital(0, 0);

        // 바이러스 생존 여부 확인
        System.out.println((answer == Integer.MAX_VALUE) ? -1 : answer);
    }

    // 주어진 병원 지점 중 M 개의 병원을 고른다.
    static void selectHospital(int idx, int depth) {
        // M 개를 모두 골랐다면
        if (depth == M) {
            // copy : origin -> board
            board = new int[N][N];
            copyBoard(origin, board);

            // 바이러스 없애기 시작
            int time = getKillTime(board);

            if(isSurvive()) return;

            answer = Math.min(answer, time);
            return;
        }

        for (int i = idx; i < hospitals.size(); i++) {
            selected[depth] = hospitals.get(i);
            selectHospital(i + 1, depth + 1);
        }
    }

    // copy 된 보드에 바이러스를 죽여나가면서 시간을 측정한다.
    static int getKillTime(int[][] board) {
        int time = 1;

        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[N][N];

        // 병원 좌표 초기 입력
        for (Point p : selected) {
            q.add(p);
            visited[p.x][p.y] = true;

        }

        while (!q.isEmpty()) {
            int size = q.size();

            for (int i = 0; i < size; i++) {
                Point cur = q.poll();

                int cx = cur.x;
                int cy = cur.y;

                for (int d = 0; d < 4; d++) {
                    int nx = cx + dx[d];
                    int ny = cy + dy[d];

                    // 경계선 밖이거나 방문했거나 벽이면
                    if (isNotBoundary(nx, ny) || visited[nx][ny] || board[nx][ny] == -1) continue;

                    // 병원일 경우 지나는 가지만 보드에 마킹하지는 않는다.
                    if (board[nx][ny] == -2) {
                        visited[nx][ny] = true;
                        q.add(new Point(nx, ny));
                    } else {
                        visited[nx][ny] = true;
                        q.add(new Point(nx, ny));
                        board[nx][ny] = time;
                    }
                }
            }

            time++;
        }

        // 최대값이 바이러스를 없애는데 걸린 시간임.
        int max = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] > 0 && max < board[i][j]) {
                    max = board[i][j];
                }
            }
        }

        return max;
    }

    static boolean isNotBoundary(int x, int y) {
        return !(0 <= x && x < N && 0 <= y && y < N);
    }

    static boolean isSurvive() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) return true;
            }
        }

        return false;
    }

    static void copyBoard(int[][] from, int[][] to) {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                to[i][j] = from[i][j];
            }
        }

    }

}
