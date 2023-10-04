package SamsungAlgorithm_SecondHalf_23.CodeTree.토끼와경주;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class 토끼와경주_서요셉_PQ {

    private final static UserSolution_2 userSolution = new UserSolution_2();

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
class UserSolution_2 {

    final static int MAX_RABBITS_CNT = 2_000;

    static int N, M, P;

    static Rabbit[] rabbitsPool;
    static HashMap<Integer, Rabbit> rabbitHM;
    static PriorityQueue<Rabbit> rabbitPQ;

    // 상 좌 하 우
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};

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
//        System.out.println("\t- 격자 크기 : " + n + " * " + m);
//        System.out.println("\t- 토끼 마리 수 : " + p);

        N = n;
        M = m;
        P = p;

        rabbitsPool = new Rabbit[MAX_RABBITS_CNT];
        rabbitHM = new HashMap<>();
        rabbitPQ = new PriorityQueue<>();

        // 최대 2,000 마리의 토끼 정보를 저장할 노드풀 생성
        for (int i = 0; i < MAX_RABBITS_CNT; i++) {
            rabbitsPool[i] = new Rabbit();
        }

        for (int i = 0; i < mRabbits.length; i++) {
            int pid = mRabbits[i][0];
            int dist = mRabbits[i][1];

            rabbitsPool[i].pid = pid;
            rabbitsPool[i].dist = dist;

            // pid 번에 해당하는 토끼 HM 에 저장
            rabbitHM.put(rabbitsPool[i].pid, rabbitsPool[i]);

            // 토끼의 우선순위를 결정하고 힙정렬을 위한 PQ 저장
            rabbitPQ.add(rabbitsPool[i]);
        }

//        printRabbitINFO();
//        System.out.println();
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

        PriorityQueue<Point> pointPQ;
        PriorityQueue<Rabbit> scoreRabbitPQ = new PriorityQueue<>(new Comparator<Rabbit>() {
            @Override
            public int compare(Rabbit r1, Rabbit r2) {
                if ((r1.x + r1.y) != (r2.x + r2.y)) return (r2.x + r2.y) - (r1.x + r1.y);
                if (r1.x != r2.x) return r2.x - r1.x;
                if (r1.y != r2.y) return r2.y - r1.y;
                return r2.pid - r1.pid;
            }
        });

        // K 번 반복
        // k 번 마다 우선순위가 가장 높은 토끼가 결정된다. -> 이 토끼는 점수를 더할 PQ 에 저장한다.
        // 결정된 토끼의 거리만큼 4방의 위치를 찾는다. -> ArrayList 에 담아 sort.
        // 해당 위치로 토끼를 이동 시키고
        // 해당 토끼의 점프 횟수를 1 증가 시킨다.
        // 해당 토끼를 제외한 다른 모든 토끼들에게 이동한 토끼의 좌표 r + c 값을 더한다.
        for (int k = 0; k < K; k++) {
            Rabbit bestRabbit = rabbitPQ.poll();

            if (!scoreRabbitPQ.contains(bestRabbit)) scoreRabbitPQ.add(bestRabbit);

            pointPQ = new PriorityQueue<>();

            int x = bestRabbit.x;
            int y = bestRabbit.y;
            int dist = bestRabbit.dist;

            // 4개의 좌표 후보를 뽑는다.
            for (int d = 0; d < 4; d++) {
                int nx = x + dx[d] * dist;
                int ny = y + dy[d] * dist;

                while (nx < 1 || N < nx) {
                    if (nx > N) nx = N - (nx - N);
                    else nx = -nx + 2;
                }
                while (ny < 1 || M < ny) {
                    if (ny > M) ny = M - (ny - M);
                    else ny = -ny + 2;
                }

                pointPQ.add(new Point(nx, ny));
            }

            // 좌표 후보 중 우선순위가 가장 우선인 좌표
            Point bestPoint = pointPQ.poll();

            // 해당 좌표로 토끼 이동, 점프 횟수 +1
            bestRabbit.x = bestPoint.x;
            bestRabbit.y = bestPoint.y;
            bestRabbit.jumpCnt++;

            // 나머지 토끼들에게 해당 좌표 x + y 만큼 점수를 더한다.
            for (Rabbit rabbit : rabbitPQ) {
                rabbit.score += (bestRabbit.x + bestRabbit.y);
            }

            // PQ 에서 빼뒀던 bestRabbit 을 다시 넣어준다.
            rabbitPQ.add(bestRabbit);
        }

        // 점수를 얻을 토끼
        Rabbit getScoreRabbit = scoreRabbitPQ.poll();

        getScoreRabbit.score += S;

//        printRabbitINFO();
//        System.out.println();
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

        // mPid 번에 해당하는 토끼를 찾는다.
        Rabbit rabbit = rabbitHM.get(mPid);

        // 이동 거리를 L 배 한다.
        rabbit.dist *= L;

//        printRabbitINFO();
    }

    /**
     * @return          : 각 토끼가 모든 경주를 진행하며 얻은 점수 중 가장 높은 점수를 출력한다.
     */
    static int getScore() {
//        System.out.println("400. 최고 토끼 선정");

        int maxScore = 0;

        while (!rabbitPQ.isEmpty()) {
            Rabbit rabbit = rabbitPQ.poll();

            maxScore = Math.max(maxScore, rabbit.score);
        }

        return maxScore;
    }

    static class Rabbit implements Comparable<Rabbit> {
        int pid;
        int dist;
        int jumpCnt;
        int score;
        int x, y;

        public Rabbit() {
            this.x = 1;
            this.y = 1;
        }

        @Override
        public int compareTo(Rabbit r) {
            if (this.jumpCnt != r.jumpCnt) return this.jumpCnt - r.jumpCnt;
            if ((this.x + this.y) != (r.x + r.y)) return (this.x + this.y) - (r.x + r.y);
            if (this.x != r.x) return this.x - r.x;
            if (this.y != r.y) return this.y - r.y;
            return this.pid - r.pid;
        }

        @Override
        public String toString() {
            return "\tRabbit{" +
                    "pid=" + pid +
                    ", dist=" + dist +
                    ", jumpCnt=" + jumpCnt +
                    ", score=" + score +
                    ", 좌표 = (" + x +
                    ", " + y + ")}";
        }
    }

    static class Point implements Comparable<Point> {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point p) {
            if ((this.x + this.y) != (p.x + p.y)) return (p.x + p.y) - (this.x + this.y);
            if (this.x != p.x) return p.x - this.x;
            return p.y - this.y;
        }
    }

    static void printRabbitINFO() {
        // 토끼 INFO
        for (Rabbit r: rabbitPQ) {
            System.out.println(r.toString());
        }
        System.out.println();
    }
}
