package SamsungAlgorithm_SecondHalf_23.CodeTree.코드트리빵;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * - 개체
 *  1. 사람
 *      - M 명의 사람이 있음
 *      - 1번은 정확히 1분에, 2번은 정확히 2분에 각자 베이스캠프에서 출발해 편의점으로 이동한다.
 *      - 사람들은 출발 시간이 되기 전까지 격자 밖에 나와있으며 사람들이 목표로 하는 편의점은 모두 다르다.
 *
 *  2. 격자
 *      - N * N 크기의 격자
 *
 *
 * - 진행
 *  1. 이동
 *      - 격자에 있는 모든 사람이 본인이 가고 싶은 편의점 방향을 향해 1칸 움직인다.
 *      - 최단 거리로 움직이며 최단거리로 움직이는 방법이 여러가지라면 (상 좌 우 하) 의 우선순위로 움직인다.
 *      - 여기서 최단거리는 상하좌우 인접한 칸 중 이동가능한 칸으로만 이동하여 도달하기까지 거쳐야 하는 칸의 수가 최소가 되는 거리를 뜻한다.
 *
 *  2. 편의점 도착
 *      - 편의점에 도착하게 되면 해당 편의점에서 멈추고 이때부터 다른 사람들은 해당 편의점 칸을 지날 수 없다.
 *      - 격자에 있는 사람들이 "모두 이동한 다음"에 해당 칸을 지나갈 수 없음에 유의한다.
 *
 *  3. 베이스캠프 이동
 *      - 현재 시간이 t 분이고 t <= m 을 만족한다면 t 번 사람은 자신이 가고 싶은 편의점과 가장 가까이 있는 베이스 캠프에 들어간다.
 *      - 여기서 가장 가까이에 있는 것 역시 1번 조건의 최단거리 조건과 동일하다.
 *      - 가장 가까운 베이스캠프가 여러가지인 경우는
 *          - 행 값이 작은 것
 *          - 여러개라면 열이 작은 베이스 캠프로 들어간다.
 *      - t 번 사람이 베이스 캠프로 이동하는데는 시간이 소요되지 않는다.
 *      - 이때부터 다른 사람들은 해당 베이스캠프가 있는 칸을 지날 수 없다.
 *      - t 번 사람이 편의점을 향해 움직이기 시작했더라도 해당 베이스캠프는 앞으로 절대 지나갈 수 없음에 유의한다.
 *      - 마찬가지로 격자에 있는 "모든 사람들이 이동"한 뒤에 해당 칸을 지나갈 수 없음에 유의한다.
 *
 * - answer : 모든 사람들이 편의점에 도착하는 시간을 구하라. (어떤 사람이 원하는 편의점에 도착 못하는 경우는 없다고 가정해도 됨)
 */
public class 코드트리빵_서요셉 {

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Person extends Point {
        Point destination;

        public Person(Point destination) {
            super(0, 0);
            this.destination = destination;
        }
    }

    static int N, M;
    static int[][] board;
    static int[][] dist;
    static Person[] people = new Person[31];

    // 상 좌 우 하
    static final int[] dx = {-1, 0, 0, 1};
    static final int[] dy = {0, -1, 1, 0};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new int[N + 1][N + 1];
        dist = new int[N + 1][N + 1];

        for (int i = 1; i < people.length; i++) {
            people[i] = new Person(null);
        }

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());

            Point destination = new Point(x, y);
            people[i].destination = destination;
        }

    }
    public static void main(String[] args) throws Exception {
        input();

        int time = 0;
        while (!allArrived()) {
            time++;

            // 모든 사람 한 칸씩 이동
            for (int i = 1; i < time && i <= M; i++) {
                move(people[i]);
            }

            // 도착한 사람 도착 처리
            for (int i = 1; i < time && i <= M; i++) {
                checkArrived(people[i]);
            }

            // 처음 출발하는 사람들 베이스캠프에 위치
            if (time <= M) {
                start(people[time]);
            }
        }

        System.out.println(time);
    }

    static void move(Person p) {
        // 이미 도착한 사람이라면 return
        if (isArrived(p)) return;

        // 최단 경로 업데이트
        findMinDist(p.destination);

        int minDist = Integer.MAX_VALUE;
        int minDir = -1;

        for (int d = 0; d < 4; d++) {
            int nx = p.x + dx[d];
            int ny = p.y + dy[d];

            // 경계밖이거나 방문할 수 없는 곳이면 continue
            if (isNotBoundary(nx, ny)) continue;
            if (board[nx][ny] == -1) continue;

            if (minDist > dist[nx][ny]) {
                minDist = dist[nx][ny];
                minDir = d;
            }
        }

        p.x += dx[minDir];
        p.y += dy[minDir];
    }

    static void checkArrived(Person p) {
        if (isArrived(p)) board[p.x][p.y] = -1;
    }

    // 목적 편의점에서부터 거리가 제일 가까운 베이스캠프를 고른다.
    static void start(Person p) {
        // 한 사람의 목적 편의점에서부터 시작하여 모든 좌표에 최단 거리를 기록한다.
        findMinDist(p.destination);

        int minDist = Integer.MAX_VALUE;
        int minX = 0;
        int minY = 0;

        // 격자를 순회하면서 (행값이 작은것, 여러개라면 열값이 작은것이므로 순차적으로 순회하면 됨.) 거리가 최소인 좌표를 찾는다.
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                // 베이스캠프가 아니면 continue
                if (board[i][j] != 1) continue;
                if (minDist > dist[i][j]) {
                    minDist = dist[i][j];
                    minX = i;
                    minY = j;
                }
            }
        }

        p.x = minX;
        p.y = minY;

        // 선택된 베이스캠프는 지나가지 못하는 위치로 마킹
        board[p.x][p.y] = -1;
    }

    // destination 좌표 기준으로 최단거리를 모든 좌표에 기록한다.
    static void findMinDist(Point destination) {
        int sx = destination.x;
        int sy = destination.y;

        // 거리 2차원 배열 초기화
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }

        Queue<Point> q = new LinkedList<>();
        q.add(new Point(sx, sy));

        dist[sx][sy] = 0;

        while (!q.isEmpty()) {
            Point cur = q.poll();

            int cx = cur.x;
            int cy = cur.y;

            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];

                // 경계 밖이거나 이미 방문하여 거리가 기록된곳이거나 지날 수 없는 곳은 continue
                if (isNotBoundary(nx, ny)) continue;
                if (dist[nx][ny] != Integer.MAX_VALUE) continue;
                if (board[nx][ny] == -1) continue;

                dist[nx][ny] = dist[cx][cy] + 1;
                q.add(new Point(nx, ny));
            }
        }
    }

    static boolean allArrived() {
        for (int i = 1; i <= M; i++) {
            if (!isArrived(people[i])) return false;
        }
        return true;
    }

    static boolean isArrived(Person p) {
        return p.x == p.destination.x && p.y == p.destination.y;
    }

    static boolean isNotBoundary(int x, int y) {
        return !(1 <= x && x <= N && 1 <= y && y <= N);
    }

}
