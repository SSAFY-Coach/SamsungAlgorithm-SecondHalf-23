package SamsungAlgorithm_SecondHalf_23.CodeTree.포탑부수기;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * - 개체
 *  1. 격자
 *      - N * M 크기 (4 <= N, M <= 10)
 *
 *  2. 포탑
 *      - N * M 개 존재
 *      - 공격 시점 (가장 최근 공격한 포탑을 판단하기 위해)
 *      - 공격력(0 <= 공격력 <= 5,000)이 존재하며 상황에 따라 증가하거나 감소할 수 있다.
 *      - 공격력이 0 이하가 된다면 해당 포탑은 부서지고 더 이상 공격할 수 없다.
 *      - 최초에 공격력이 0 인 포탑, 즉 부서진 포탑이 존재할 수 있다.
 *
 * - 진행
 *  - (총 K 번 수행된다)  -  (1 <= K <= 1,000)
 *  - 만약 부서지지 않은 포탑이 1개가 되는 즉시 중지된다.
 *
 *  1. 공격자 선정
 *      - 부서지지 않은 포탑 중 가장 약한 포탑이 선정된다. 선정된 포탑은 N + M 만큼 공격력이 증가한다.
 *        * 공격자(가장 약한 포탑) 선정 기준
 *          1-1. 공격력이 가장 낮은 포탑
 *          1-2. 그러한 포탑이 2개 이상이라면, 가장 최근에 공격한 포탑이 가장 약한 포탑.
 *          1-3. 그러한 포탑이 2개 이상이라면, 행과 열의 합이 가장 큰 포탑이 가장 약한 포탑.
 *          1-4. 그러한 포탑이 2개 이상이라면, 열 값이 가장 큰 포탑이 가장 약한 포탑.
 *
 *  2. 공격자의 공격
 *      - 1에서 선정된 공격자는 자신을 제외한 가장 강한 포탑을 공격한다.
 *        * 공격 대상(가장 강한 포탑) 선정 기준 -> 공격자(가장 약한 포탑) 선정 기준과 반대
 *          2-1. 공격력이 가장 높은 포탑
 *          2-2. 그러한 포탑이 2개 이상이라면, 공격한지 가장 오래된 포탑이 가장 강한 포탑.
 *          2-3. 그러한 포탑이 2개 이상이라면, 행과 열의 합이 가장 작은 포탑이 가장 강한 포탑.
 *          2-4. 그러한 포탑이 2개 이상이라면, 열 값이 가장 작은 포탑이 가장 약한 포탑.
 *
 *      - 공격을 할 때에는 레이저 공격을 먼저 시도하고, 그게 안 된다면 포탄 공격을 한다.
 *        * 레이저 공격
 *          2-1. 상하좌우 4방으로 움직일 수 있다.
 *          2-2. 부서진 포탑이 있는 위치는 지날 수 없다.
 *          2-3. 경계선을 만나면 반대로 나온다.
 *
 *          - 레이저 공격은 공격자의 위치에서 공격 대상 포탑까지의 최단 경로로 공격한다.
 *          - 만약 최단 경로가 존재하지 않으면 '포탄 공격'을 진행한다.
 *          - 만약 최단 경로가 2개 이상이라면 우 - 하 - 좌 - 상 우선순위대로 먼저 움직인 경로가 선택된다.
 *
 *          - 최단 경로가 정해지면, 공격대상에는 공격자의 공격력 만큼 피해를 입고 피해를 입은 포탑은 해당 수치만큼 공격력이 감소한다.
 *          - 공격 대상을 제외한 레이저 경로에 있는 포탑도 공격을 받는다. 이 포탑들은 공격자 공격력의 절반 수치만큼 공격력이 감소한다.
 *
 *        * 포탄 공격
 *          - 공격 대상은 공격자 공격력 만큼 피해를 입는다.
 *          - 공격 대상 기준 8방의 포탑도 피해를 입는다. 이 포탑들은 공격자 공격력의 절반 수치만큼 공격력이 감소한다.
 *          - 공격자는 해당 공격에 영향을 받지 않는다.
 *          - 만약 가장자리에 포탄이 떨어졌다면 레이저 이동과 마찬가지로 반대편 격자에 피해가 간다.
 *
 *  3. 포탑 부서짐
 *      - 공격을 받아 공격력이 0 이하가 된 포탑은 부서진다.
 *
 *  4. 포탑 정비
 *      - 부서지지 않은 포탑들 중, 공격과 무관한 포탑은 공격력이 1 증가한다.
 *      - 공격과 무관함은 공격자도 아니고 피해를 받지 않은 포탑을 의미한다.
 *
 * - answer : 전체 과정이 종료된 후 남아있는 포탑 중 가장 강한 포탑의 공격력을 출력한다. 만약 부서지지 않은 포탑이 1개가 되면 즉시 중지된다.
 *
 */
public class 포탑부수기_서요셉 {

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Tower implements Comparable<Tower> {
        int x, y;
        int power;
        int turn;
        boolean relatedAttack;

        public Tower(int x, int y, int power, int turn) {
            this.x = x;
            this.y = y;
            this.power = power;
            this.turn = turn;
            this.relatedAttack = false;
        }

        @Override
        public int compareTo(Tower t) {
            if (this.power != t.power) return this.power - t.power;
            if (this.turn != t.turn) return t.turn - this.turn;
            if ((this.x + this.y) != (t.x + t.y)) return (t.x + t.y) - (this.x + this.y);
            return t.y - this.y;
        }

        @Override
        public String toString() {
            String s = "";

            s = relatedAttack ? "IS_ATTACKED" : "NONE";

            return " Tower {좌표 = " +
                    "(" + x +
                    ", " + y +
                    "), power=" + power +
                    ", turn=" + turn +
                    ", relatedAttack=" + s +
                    '}';
        }
    }

    static int N, M, K, answer;
    static List<Tower> towerList;
    static Tower[][] towerBoard;
    static Point[][] prevPoint;

    static Tower attack_tower, target_tower;

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        towerBoard = new Tower[N][M];
        towerList = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                int power = Integer.parseInt(st.nextToken());

                // 입력으로 공격력이 0 인 포탑도 주어진다.
                // 따라서 0 이상이 경우에만 보드와 리스트에 추가한다.
                if (power > 0) {
                    Tower tower = new Tower(i, j, power, 0);

                    towerBoard[i][j] = tower;
                }
            }
        }

    }
    public static void main(String[] args) throws Exception {
        input();

        // K 번 진행
        for (int k = 1; k <= K; k++) {
            initTower();

            if (towerList.size() == 1) {
                answer = towerList.get(0).power;
                break;
            }

            attack_tower = towerList.get(0);
            attack_tower.power += (N + M);
            attack_tower.turn = k;
            attack_tower.relatedAttack = true;

            target_tower = towerList.get(towerList.size() - 1);
            target_tower.relatedAttack = true;

            attackToTarget();
            repairTower();
        }

        getAnswer();
        System.out.println(answer);
    }

    // 1. 공격자, 타겟을 정하기 위한 초기화 단계 (우선순위에 따른 정렬, 공격 관련 상태 초기화)
    //  - 공격과 관련된 상태들을 초기화 시킴
    //  - 최단 경로를 찾는 이전 좌표 배열도 초기화
    static void initTower() {
        towerList = new ArrayList<>();
        prevPoint = new Point[N][M];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (towerBoard[i][j] != null) {
                    towerBoard[i][j].relatedAttack = false;
                    towerList.add(towerBoard[i][j]);
                }
            }
        }

        Collections.sort(towerList);
    }

    // 2. 공격자의 공격
    //  - 레이저 공격이 가능한지 확인하고
    //  - 가능하면 레이저 공격, 불가능하면 포탄 공격
    static void attackToTarget() {
        if (possibleLaser()) attackLaser();
        else attackBomb();
    }

    static boolean possibleLaser() {
        // 우 - 하 - 좌 - 상
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};

        Queue<Point> q = new LinkedList<>();
        boolean[][] visited = new boolean[N][M];

        q.add(new Point(attack_tower.x, attack_tower.y));
        visited[attack_tower.x][attack_tower.y] = true;

        while (!q.isEmpty()) {
            Point cur = q.poll();

            int cx = cur.x;
            int cy = cur.y;

            if (cx == target_tower.x && cy == target_tower.y) {
                return true;
            }

            for (int d = 0; d < 4; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];

                // 경계선 넘으면 반대편으로 이동
                if (nx < 0) nx = N - 1;
                if (nx >= N) nx = 0;
                if (ny < 0) ny = M - 1;
                if (ny >= M) ny = 0;

                // 이미 방문했거나 무너진 포탑이면 지날 수 없다.
                if (visited[nx][ny] || towerBoard[nx][ny] == null) continue;

                q.add(new Point(nx, ny));
                visited[nx][ny] = true;
                prevPoint[nx][ny] = cur;
            }
        }

        return false;
    }

    static void attackLaser() {
        Point back = new Point(target_tower.x, target_tower.y);

        while (back.x != attack_tower.x || back.y != attack_tower.y) {
            int power = attack_tower.power / 2;

            if (back.x == target_tower.x && back.y == target_tower.y) {
                power = attack_tower.power;
            }

            attack(back.x, back.y, power);
            back = prevPoint[back.x][back.y];
        }
    }

    static void attackBomb() {
        // 8방 - 상 우상 우 우하 하 좌하 좌 좌상
        int[] dx = {0, -1, -1, 0, 1, 1, 1, 0, -1};
        int[] dy = {0, 0, 1, 1, 1, 0, -1, -1, -1};

        for (int d = 0; d < 9; d++) {
            int nx = target_tower.x + dx[d];
            int ny = target_tower.y + dy[d];

            // 경계선 넘으면 반대편으로 이동
            if (nx < 0) nx = N - 1;
            if (nx >= N) nx = 0;
            if (ny < 0) ny = M - 1;
            if (ny >= M) ny = 0;

            // 이미 무너진 포탑이거나 attacker 이면 넘어간다.
            if (towerBoard[nx][ny] == null || nx == attack_tower.x && ny == attack_tower.y) continue;

            int power = ((nx == target_tower.x) && (ny == target_tower.y)) ? attack_tower.power : attack_tower.power / 2;

            attack(nx, ny, power);
        }

    }

    static void attack(int x, int y, int power) {
        towerBoard[x][y].power = Math.max(towerBoard[x][y].power - power, 0);
        towerBoard[x][y].relatedAttack = true;

        if (towerBoard[x][y].power == 0) {
            towerBoard[x][y] = null;
        }
    }

    // 3. 포탑 재정비
    //  - 공격과 관련되지 않은 포탑들은 공격력을 1 증가 시킨다.
    static void repairTower() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (towerBoard[i][j] != null && !towerBoard[i][j].relatedAttack) towerBoard[i][j].power++;
            }
        }
    }

    // 4. 포탑 리스트를 정렬시키고 가장 강한 포탑의 공격력을 반환한다.
    static void getAnswer() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (towerBoard[i][j] != null) {
                    answer = Math.max(answer, towerBoard[i][j].power);
                }
            }
        }
    }

}
