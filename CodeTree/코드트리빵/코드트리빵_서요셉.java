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
        boolean isStarted;
        boolean isArrived;
        Point destination;          // 목표 편의점 좌표
        Queue<Point> minPointQ;

        public Person() {
            super(0, 0);        // 최초 격자 밖에 존재.
            this.isStarted = false;
            this.isArrived = false;
            this.minPointQ = new LinkedList<>();
        }

        // 출발 여부를 true 로 설정하고 베이스캠프 위치에 위치시킨다.
        public void start(Point basecamp) {
            this.x = basecamp.x;
            this.y = basecamp.y;
            this.isStarted = true;
        }

        @Override
        public String toString() {
            String s = "도착";

            if (!isArrived) s = "X";

            return "\tPerson{" +
                    "현재 위치 = (" + x +
                    ", " + y +
                    "), isArrived=" + s +
                    '}';
        }
    }

    // 상 좌 우 하
    static final int[] dx = {-1, 0, 0, 1};
    static final int[] dy = {0, -1, 1, 0};

    static int N, M, time;

    static int[][] board;           // (1, 1) 부터 시작
    static Person[] people;
    static Point[][] back;

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new int[N + 1][N + 1];
        people = new Person[M + 1];
        for (int i = 1; i <= M; i++) {
            people[i] = new Person();
        }

        // 베이스 캠프와 빈공간 격자 정보 입력
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 편의점 위치 정보 입력
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());

            people[i].destination = new Point(x, y);
        }

    }
    public static void main(String[] args) throws Exception {
        input();

        time = 0;
        // 모든 사람이 목표 편의점에 도착하면 종료
        while (!allArrived()) {
            time++;
            System.out.println("< " + time + " 분 경과 >");
            // 사람들 이동
            move(time);

            System.out.println("\t -- 이동 후 --");
            printPersonStatus();
        }

        System.out.println(time);
    }

    static void move(int t) {

        // 모든 사람이 아직 출발 전이라면
        if (t <= M) {
            // 시간 순대로 사람들을 출발 시킨다.
            for (int i = 1; i <= t; i++) {
                Person p = people[i];

                // 아직 출발하지 않은 사람들은 자신의 목표 편의점에서 가장 가까운 베이스캠프를 찾고 그곳에 위치시킨다.
                if (!p.isStarted) {
                    System.out.println("\t" + i + " 번 사람이 베이스캠프에 위치합니다.");

                    Point basecamp = findMinBasecamp(p);
                    p.start(basecamp);
                }
                // 출발한 사람들은 자신의 목표 편의점을 향해 최단 거리로 한칸씩 이동시킨다.
                else {
                    // 이미 편의점에 도착했다면 continue
                    if (p.isArrived) {
                        System.out.println("\t" + i + " 번 사람은 이미 편의점에 도착했습니다.");
                        continue;
                    }

                    // 최단거리로 목표를 향해 한 칸씩 이동
                    System.out.println("\t" + i + " 번 사람은 한 칸 이동합니다.");
                    moveSub(p);
                }
            }

        }
        // 모든 사람이 출발했다면
        else {
            // 편의점에 도착한 사람을 제외한 모든 사람을 이동 시킨다.
            for (int i = 1; i <= M; i++) {
                Person p = people[i];

                // 이미 편의점에 도착했다면 continue
                if (p.isArrived) {
                    System.out.println("\t" + i + " 번 사람은 이미 편의점에 도착했습니다.");
                    continue;
                }

                // 최단거리로 목표를 향해 한 칸씩 이동
                System.out.println("\t" + i + " 번 사람은 한 칸 이동합니다.");
                moveSub(p);
            }
        }
    }

    static Point findMinBasecamp(Person p) {
        // 최단 경로 역추적하기 위한 back 배열
        back = new Point[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                back[i][j] = new Point(0, 0);
            }
        }

        Point basecamp = new Point(0, 0);

        int sx = p.destination.x;
        int sy = p.destination.y;

        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[N + 1][N + 1];

        q.add(new Point(sx, sy));
        visited[sx][sy] = true;

        while (!q.isEmpty()) {
            Point cur = q.poll();

            int cx = cur.x;
            int cy = cur.y;

            // 베이스캠프를 찾으면
            if (board[cx][cy] == 1) {
                basecamp.x = cx;
                basecamp.y = cy;
                board[cx][cy] = 2;      // 선택한 베이스캠프는 2로 마스킹
                break;
            }

            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];

                // 경계 밖이거나, 이미 방문했거나, 이미 선택된 베이스캠프이거나
                // 이미 다른 사람이 도착한 편의점이라면 continue
                if (isNotBoundary(nx, ny)) continue;
                if (visited[nx][ny]) continue;
                if (board[nx][ny] == 2 || board[nx][ny] == 3) continue;

                q.add(new Point(nx, ny));
                visited[nx][ny] = true;
                back[nx][ny] = cur;
            }
        }

        // t번의 사람이 이동할 최단 경로를 저장한다.
        Point backPoint = basecamp;

        while (backPoint.x != sx || backPoint.y != sy) {
            backPoint = back[backPoint.x][backPoint.y];
            p.minPointQ.add(backPoint);
        }

        return basecamp;
    }

    static boolean isNotBoundary(int x, int y) {
        return !(1 <= x && x <= N && 1 <= y && y <= N);
    }

    static void moveSub(Person p) {
        // 도착한 편의점은 3으로 표기
        // 이미 선택된 베이스캠프 (2) 이거나 이미 다른 사람이 도착한 편의점(3) 이라면 continue

        // 이 사람의 최단경로 Q에서 하나를 뽑아본다.
        Point np = p.minPointQ.peek();

        // 만약 이미 선택된 베이스캠프 (2) 이거나 이미 다른 사람이 도착한 편의점(3) 이라면
        if (board[np.x][np.y] == 2 || board[np.x][np.y] == 3) {
            // 최단 경로를 다시 탐색해야한다.
            findMinPoint(p);
        }

        // 하나를 뽑아내서 한칸 이동시킨다.
        np = p.minPointQ.poll();

        p.x = np.x;
        p.y = np.y;

        if (p.x == p.destination.x && p.y == p.destination.y) {
            p.isArrived = true;
            board[p.x][p.y] = 3;
        }
    }

    static void findMinPoint(Person p) {
        p.minPointQ.clear();

        back = new Point[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                back[i][j] = new Point(0, 0);
            }
        }

        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[N + 1][N + 1];

        int sx = p.destination.x;
        int sy = p.destination.y;

        q.add(new Point(sx, sy));
        visited[sx][sy] = true;

        while (!q.isEmpty()) {
            Point cur = q.poll();

            int cx = cur.x;
            int cy = cur.y;

            // 사람의 현재 위치라면 종료
            if (cx == p.x && cy == p.y) {
                break;
            }

            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];

                if (isNotBoundary(nx, ny)) continue;
                if (visited[nx][ny]) continue;
                if (board[nx][ny] == 2 || board[nx][ny] == 3) continue;

                q.add(new Point(nx, ny));
                visited[nx][ny] = true;
                back[nx][ny] = cur;
            }
        }

        Point backPoint = back[p.x][p.y];

        while (backPoint.x != sx || backPoint.y != sy) {
            backPoint = back[backPoint.x][backPoint.y];
            p.minPointQ.add(backPoint);
        }
    }

    static boolean allArrived() {
        for (int i = 1; i <= M; i++) {
            Person p = people[i];
            if (!p.isArrived) return false;
        }
        return true;
    }

    static void printPersonStatus() {
        for (int i = 1; i < people.length; i++) {
            Person p = people[i];

            System.out.println(p.toString());
        }
    }
}
