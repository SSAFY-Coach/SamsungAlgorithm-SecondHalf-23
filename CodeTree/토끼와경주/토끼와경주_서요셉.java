package SamsungAlgorithm_SecondHalf_23.CodeTree.토끼와경주;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class 토끼와경주_서요셉 {

    private final static UserSolution userSolution = new UserSolution();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int score = 0;

        int Q = Integer.parseInt(br.readLine());
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());

            int command = Integer.parseInt(st.nextToken());         // 100 : 경주 시작 준비  200 : 경주 진행  300 : 이동거리 변경 400 : 최고 토끼 선정

            switch (command) {
                case 100:
                    int N = Integer.parseInt(st.nextToken());
                    int M = Integer.parseInt(st.nextToken());
                    int P = Integer.parseInt(st.nextToken());

                    int[][] rabbits = new int[P][2];

                    for (int p = 0; p < P; p++) {
                        int pid = Integer.parseInt(st.nextToken());
                        int dist = Integer.parseInt(st.nextToken());

                        rabbits[p][0] = pid;
                        rabbits[p][1] = dist;
                    }

                    userSolution.init(N, M, P, rabbits);
                    break;
                case 200:
                    int K = Integer.parseInt(st.nextToken());
                    int S = Integer.parseInt(st.nextToken());

                    userSolution.raceStart(K, S);
                    break;
                case 300:
                    int pid = Integer.parseInt(st.nextToken());
                    int L = Integer.parseInt(st.nextToken());

                    userSolution.changeDist(pid, L);
                    break;
                case 400:
                    score = userSolution.getScore();
                    break;
            }
        }

        System.out.println(score);
    }

}

/**
 * - 100 (init) : 최초 한 번 호출
 * - 200 (raceStart) : 최대 2,000 호출
 * - 300 (changeDist) : 최대 2,000 호출
 * - 400 (getScore) : 마지막 한 번 호출
 *
 * - 도합 Q 최대값 : 4,002
 */
class UserSolution {

    // 경주 우선순위 토끼 Comparator
    static final Comparator<Rabbit> raceRabbitComparator = (r1, r2) -> {
        if (r1.jumpCnt != r2.jumpCnt) return r1.jumpCnt - r2.jumpCnt;
        if ((r1.x + r1.y) != (r2.x + r2.y)) return (r1.x + r1.y) - (r2.x + r2.y);
        if (r1.x != r2.x) return r1.x - r2.x;
        if (r1.y != r2.y) return r1.y - r2.y;
        return r1.id - r2.id;
    };

    // 점수 받을 우선순위 토끼 Comparator
    static final Comparator<Rabbit> scoreRabbitComparator = (r1, r2) -> {
        if ((r1.x + r1.y) != (r2.x + r2.y)) return (r2.x + r2.y) - (r1.x + r1.y);
        if (r1.x != r2.x) return r2.x - r1.x;
        if (r1.y != r2.y) return r2.y - r1.y;
        return r2.id - r1.id;
    };

    // 다음 좌표 우선순위 Comparator
    static final Comparator<Point> pointComparator = (p1, p2) -> {
        if ((p1.x + p1.y) != (p2.x + p2.y)) return (p2.x + p2.y) - (p1.x + p1.y);
        if (p1.x != p2.x) return p2.x - p1.x;
        return p2.y - p1.y;
    };

    static int N, M, P;

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};

    static Rabbit[] rabbitsPool;
    /**
     * P 마리 토끼가 N * M 크기의 격자 위에서 경주 준비를 한다.
     * 각 토끼에게는 고유한 번호가 있으며 한번 움직일 시 꼭 이동해야하는 이동 거리도 정해져 있다.
     * i 번 토끼의 고유 번호는 pid(i), 이동해야 하는 거리는 d(i) 이다.
     * 처음 토끼들은 전부 (1, 1)에 위치해 있다.
     *
     * @param n                 : 격자 세로 크기 (2 <= N <= 100,000)
     * @param m                 : 격자 가로 크기 (2 <= M <= 100,000)
     * @param p                 : 토끼 마리 수 (1 <= P <= 2,000)
     * @param mRabbits          : mRabbits[i][0] - 토끼 고유 번호 (1 <= pid <= 10,000,000)
     *                            mRabbits[i][1] - 토끼 이동 방향 (1 <= d(i) <= 1,000,000,000)
     */
    static void init(int n, int m, int p, int[][] mRabbits) {
//        System.out.println("100. 경주 시작 준비");

        N = n; M = m; P = p;

        rabbitsPool = new Rabbit[P];

        for (int i = 0; i < P; i++) {
            int id = mRabbits[i][0];
            int dist = mRabbits[i][1];

            Rabbit rabbit = new Rabbit(id, dist);
            rabbitsPool[i] = rabbit;
        }

    }

    /**
     * 우선순위가 높은 토끼를 뽑아 멀리 보내주는 것을 K 번 반복한다.
     *
     * 우선 순위는 다음과 같다.
     *  - 점프 횟수가 적은 토끼
     *  - 현재 행 + 열 값이 작은 토끼
     *  - 행 값이 작은 토끼
     *  - 열 값이 작은 토끼
     *  - 고유번호가 작은 토끼
     *
     *  우선순위가 결정되고 뽑힌 토끼가 i 번이라 할때,
     *  상하좌우 4방으로 각각 d(i) 만큼 이동할때의 위치를 구한다. 이때 격자를 벗어나면 반대로 방향을 바꾸고 한칸 이동한다.
     *  이 4 칸 중 우선순위가 가장 높은 것을 뽑는다.
     *   - 행 + 열 값이 큰 칸
     *   - 행 값이 큰 칸
     *   - 열 값이 큰 칸
     *  우선순위가 높은 칸으로 i 번 토끼를 이동시키고 이 칸의 위치가 (r, c) 라 했을 때,
     *  i 번 토끼를 제외한 P - 1 마리 토끼들은 (r + c) 만큼의 점수를 동시에 얻는다.
     *
     *  - 진행
     *      1. 우선순위가 높은 토끼를 뽑는다.
     *      2. 해당 토끼 기준 4방 탐색으로 위치 4곳을 구하고 우선순위가 높은 좌표(r, c)를 구한다.
     *      3. 해당 위치로 토끼를 이동시킨다.
     *      4. 나머지 토끼들에게 (r + c) 만큼의 점수를 더해준다.
     *      5. 1~4 과정을 K 번 반복한다. 동일한 토끼가 여러번 선택될 수 있다.
     *      6. K 번의 턴이 종료된 직후에 다음의 우선순위를 통해 가장 우선순위가 높은 토끼를 골라 점수 S를 더한다.
     *         단, 이 경우 K 번의 턴 동안 한번이라도 뽑혔던 적이 있는 토끼 중에서 골라야함을 유의한다.
     *          - 행 + 열 값이 큰 토끼
     *          - 행 값이 큰 토끼
     *          - 열 값이 큰 토끼
     *          - 고유번호가 큰 토끼
     *
     * @param K             : 진행될 턴 수 (1 <= K <= min(100, P)) 최대 100이며 토끼 마리수 만큼 진행된다.
     * @param S             : K 턴이 진행된 후 뽑힌 토끼들 중 우선순위가 높은 토끼에게 더할 점수 (1 <= S <= 1,000,000)
     */
    static void raceStart(int K, int S) {
//        System.out.println("200. 경주 진행");
//        System.out.println("  - " + K + " 턴 동안 진행");

        for (Rabbit rabbit : rabbitsPool) {
            rabbit.isJumped = false;
        }

        for (int k = 0; k < K; k++) {
            Arrays.sort(rabbitsPool, raceRabbitComparator);

            Rabbit bestRabbit = rabbitsPool[0];
            bestRabbit.jumpCnt++;
            bestRabbit.isJumped = true;

            // 4방 좌표를 구한뒤 우선순위 대로 하나의 좌표만 구해온다.
            Point bestPoint = getBestPoint(bestRabbit);

            bestRabbit.x = bestPoint.x;
            bestRabbit.y = bestPoint.y;

            // 해당 토끼를 제외한 모든 토끼들의 점수를 r + c + 2 만큼 더해준다.
            scoreUpOtherRabbits(bestRabbit.id, bestRabbit.x + bestRabbit.y + 2);
        }

        // 뽑혔던 토끼들 중 우선순위가 가장 높은 토끼에게 점수를 부여한다.
        scoreUpBestRabbit(S);

//        printRabbitINFO();
    }

    static void scoreUpOtherRabbits(int pid, int score) {
        for (Rabbit rabbit: rabbitsPool) {
            if (rabbit.id == pid) continue;

            rabbit.score += score;
        }
    }

    static void scoreUpBestRabbit(int score) {
        Arrays.sort(rabbitsPool, scoreRabbitComparator);

        for (Rabbit rabbit : rabbitsPool) {
            if (rabbit.isJumped) {
                rabbit.score += score;
                break;
            }
        }
    }

    static Point getBestPoint(Rabbit rabbit) {
        Point[] points = new Point[4];

        int x = rabbit.x;
        int y = rabbit.y;
        int dist = rabbit.dist;

        for (int d = 0; d < 4; d++) {
            int nx = getPoint(rabbit.x, dx[d] * rabbit.dist, N);
            int ny = getPoint(rabbit.y, dy[d] * rabbit.dist, M);

            points[d] = new Point(nx, ny);
        }

        Arrays.sort(points, pointComparator);
        return points[0];
    }

    // 임의로 0 ~ N-1, 0 ~ M-1 로 변경하였음
    static int getPoint(int cur, int dist, int mod) {
        mod--;

        // 가야할 방향이 상, 좌 여서 0 보다 작을 때
        if (dist < 0) {
            dist += cur;
            dist = -dist;
            cur = 0;
        }
        // 가야할 방향이 하, 우 여서 전체 범위보다 크거나 같을 때
        else if (cur + dist >= mod) {
            dist -= (mod - cur);

            // 그 값이 여전히 mod 보다 크면
            if (dist > mod) {
                dist -= mod;
                cur = 0;
            } else {
                return mod - dist;
            }
        }

        if ((dist / mod) % 2 == 1) return mod - (dist % mod);
        else return (dist + cur) % mod;
    }

    /**
     * mPid 번 토끼의 이동 거리를 L 배 해준다.
     * 이때 토끼의 이동거리가 10억이 넘지 않음이 보장된다.
     *
     * @param mPid          : 토끼 번호 (1 <= mPid <= 10,000,000)
     * @param L             : 이동 거리 l 배 (1 <= L <= 1,000,000,000)
     */
    static void changeDist(int mPid, int L) {
//        System.out.println("300. 이동 거리 변경");

        for (Rabbit rabbit: rabbitsPool) {
            if (rabbit.id == mPid) {
                rabbit.dist *= L;
                break;
            }
        }

    }

    /**
     * @return          : 각 토끼가 모든 경주를 진행하며 얻은 점수 중 가장 높은 점수를 출력한다.
     */
    static int getScore() {
//        System.out.println("400. 최고 토끼 선정");
        int maxScore = 0;

        for (Rabbit rabbit: rabbitsPool) {
            maxScore = Math.max(maxScore, rabbit.score);
        }

        return maxScore;
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Rabbit extends Point {
        int id;
        int dist;
        int jumpCnt;
        int score;
        boolean isJumped;

        public Rabbit(int id, int dist) {
            super(0, 0);
            this.id = id;
            this.dist = dist;
            this.isJumped = false;
        }

        @Override
        public String toString() {
            return "\tRabbit{" +
                    "좌표 = (" + x +
                    ", " + y +
                    "), id=" + id +
                    ", dist=" + dist +
                    ", jumpCnt=" + jumpCnt +
                    ", score=" + score +
                    ", isJumped=" + isJumped +
                    '}';
        }
    }

    static void printRabbitINFO() {
        for (Rabbit r: rabbitsPool) {
            System.out.println(r.toString());
        }
    }
}
