package SamsungAlgorithm_SecondHalf_23.CodeTree.싸움땅;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - N * N 크기
 *      - 각각의 격자에는 무기들이 있을 수 있다.
 *      - 초기애는 무기들이 없는 빈 격자에 플레이어들이 위치한다.
 *      - 빨간색 숫자는 총의 경우 '공격력', 플레이어의 경우 '초기 능력치'
 *      - 노란색 숫자는 플레이어의 '고유 번호'
 *
 *  2. 플레이어
 *      - 초기 능력치를 지닌다.
 *      - 각 플레이어 초기 능력치는 모두 다르다.
 *
 * - 진행
 *  1. 플레이어 이동
 *      - 본인의 방향대로 한 칸 이동. 격자를 벗어나게 되면 정반대 방향으로 바꾸어 한 칸 이동.
 *
 *  2. 이동한 칸 확인
 *      - 다른 플레이어가 없다면
 *          - 해당 칸에 총이 있는지 확인.
 *          - 총이 있다면 해당 플레이어는 총을 획득.
 *          - 이미 플레이어가 총을 갖고 있다면 놓여있는 총들과 플레이어가 가진 총들 중 공격력이 더 센것을 획득하고 나머지는 해당 격자에 둔다.
 *      - 다른 플레이어가 있다면
 *          - 두 플레이어 전투
 *          - 플레이어 초기능력치 + 총 공격력 합이 더큰 플레이어가 이긴다.
 *          - 만일 이 수치가 같다면 초기 능력치가 높은 플레이어가 이긴다.
 *          - 이긴 플레이어는 각 플레이어의 초기 능력치와 가지고 있는 총의 공격력의 합의 차이만큼 포인트로 획득한다.
 *          - 진 플레이어는 본인의 총을 해당 격자에 내려놓고, 해당 플레이어가 원래 가진 방향대로 한 칸 이동.
 *              - 만약 이동하려는 칸에 다른 플레이어가 있거나 격자 범위 밖인 경우는 오른쪽으로 90도 회전하여 빈칸이 보이면 이동한다.
 *              - 만약 해당 칸에 총이 있다면, 해당 플레이어는 가장 공격력이 높은 총을 획득하고 나머지 총은 내려놓는다.
 *          - 이긴 플레이어는 승리한 칸에 떨어져있는 총들과 원래 들고 있던 총 중 가장 공격력이 높은 총을 획득하고 나머지 총을 해당 격자에 내려 놓는다.
 *
 *
 * - answer : K 라운드 동안 게임을 진해앟면서 각 플레이어들이 획득한 포인트를 출력하라.
 *
 */
public class 싸움땅_서요셉 {

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Player extends Point {
        int id;
        int dir;
        int power;
        int score;
        int gun;

        public Player(int x, int y, int dir, int power, int id) {
            super(x, y);
            this.dir = dir;
            this.power = power;
            this.id = id;
            this.gun = 0;
        }

        @Override
        public String toString() {
            return "Player{<" + id + "> " +
                    "좌표 = (" + x +
                    ", " + y +
                    "), power=" + power +
                    ", gun=" + gun +
                    ", dir=" + dir +
                    ", score=" + score +
                    '}';
        }
    }

    static int N, M, K;         // N 격자 크기, M 플레이어 수, K 라운드 수

    static ArrayList<Integer>[][] gunBoard;
    static int[][] playerBoard;
    static Player[] players;

    // 방향 상 우 하 좌    0  1  2  3
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        gunBoard = new ArrayList[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                gunBoard[i][j] = new ArrayList<>();
            }
        }

        playerBoard = new int[N + 1][N + 1];
        players = new Player[M + 1];

        // 격자 위치에 초기 총 위치 배치
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                int gunPower = Integer.parseInt(st.nextToken());
                gunBoard[i][j].add(gunPower);
            }
        }

        // 플레이어 정보 입력
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int dir = Integer.parseInt(st.nextToken());
            int power = Integer.parseInt(st.nextToken());

            Player player = new Player(x, y, dir, power, i);

            players[i] = player;
            playerBoard[x][y] = 1;
        }

//        printPlayerBoard();
//        System.out.println(":: Player INFO ::");
//        printPlayerInfo();
    }
    public static void main(String[] args) throws Exception {
        input();
        solve();
        printAnswer();
    }

    static void solve() {
        // K 라운드 동안 게임 진행
        for (int k = 0; k < K; k++) {
            for (int i = 1; i <= M; i++) {
                movePlayers(players[i]);
            }
        }
    }

    static void movePlayers(Player p) {
        int cx = p.x;
        int cy = p.y;

        int nx = cx + dx[p.dir];
        int ny = cy + dy[p.dir];

        if (isNotBoundary(nx, ny)) {
            p.dir = (p.dir + 2) % 4;

            nx = cx + dx[p.dir];
            ny = cy + dy[p.dir];
        }

        p.x = nx;
        p.y = ny;

        playerBoard[cx][cy]--;
        playerBoard[nx][ny]++;

        if (playerBoard[nx][ny] == 2) {
            battle(p);
        } else {
            getGun(p);
        }

    }

    static void battle(Player p1) {
        Player p2 = null;

        for (int i = 1; i <= M; i++) {
            if (p1.id == players[i].id) continue;
            if (p1.x == players[i].x && p1.y == players[i].y) {
                p2 = players[i];
            }
        }

        int p1_sum = p1.power + p1.gun;
        int p2_sum = p2.power + p2.gun;
        int score = Math.abs(p1_sum - p2_sum);

        // p1 승
        if (p1_sum > p2_sum) {
            getResult(p1, p2, score);
        } else if (p1_sum == p2_sum) {
            // p1 승
            if (p1.power > p2.power) {
                getResult(p1, p2, score);
            }
            // p2 승
            else if (p1.power < p2.power) {
                getResult(p2, p1, score);
            }
        }
        // p2 승
        else {
            getResult(p2, p1, score);
        }
    }

    static void getResult(Player winner, Player loser, int score) {
        gunBoard[loser.x][loser.y].add(loser.gun);
        loser.gun = 0;
        loserMove(loser);

        getGun(winner);
        getScore(winner, score);
    }

    static void getScore(Player p, int score) {
        p.score += score;
    }

    static void loserMove(Player p) {
        int cx = p.x;
        int cy = p.y;

        int nx = cx + dx[p.dir];
        int ny = cy + dy[p.dir];

        while (isNotBoundary(nx, ny) || playerBoard[nx][ny] == 1) {
            p.dir = (p.dir + 1) % 4;

            nx = cx + dx[p.dir];
            ny = cy + dy[p.dir];
        }

        p.x = nx;
        p.y = ny;

        playerBoard[cx][cy]--;
        playerBoard[nx][ny]++;

        getGun(p);
    }

    static void getGun(Player p) {
        if (gunBoard[p.x][p.y].size() == 0) return;

        if (p.gun != 0) {
            int maxIdx = 0;

            gunBoard[p.x][p.y].add(p.gun);
            p.gun = 0;

            int size = gunBoard[p.x][p.y].size();
            for (int i = 0; i < size; i++) {
                if (gunBoard[p.x][p.y].get(i) > p.gun) {
                    p.gun = gunBoard[p.x][p.y].get(i);
                    maxIdx = i;
                }
            }

            gunBoard[p.x][p.y].remove(maxIdx);
        } else {
            int max = 0;
            int maxIdx = 0;

            int size = gunBoard[p.x][p.y].size();
            for (int i = 0; i < size; i++) {
                if (gunBoard[p.x][p.y].get(i) > max) {
                    max = gunBoard[p.x][p.y].get(i);
                    maxIdx = i;
                }
            }

            p.gun = max;
            gunBoard[p.x][p.y].remove(maxIdx);
        }
    }

    static boolean isNotBoundary(int x, int y) {
        return !(1 <= x && x <= N && 1 <= y && y <= N);
    }

    static void printAnswer() {
        for (int i = 1; i <= M; i++) {
            System.out.print(players[i].score + " ");
        }
    }

}
