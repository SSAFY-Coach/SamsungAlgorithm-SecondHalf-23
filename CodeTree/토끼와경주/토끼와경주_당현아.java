import java.io.*;
import java.util.*;

public class Main {

    public static int Q;        // 명령의 수 (2 <= Q <= 4002)
    public static int N, M;     // 격자의 크기 (2 <= N, M <= 100000)
    public static int P;        // 토끼의 마리수 (1 <= P <= 2000)
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Q = Integer.parseInt(br.readLine());

        raceReady(br);                                      // 100 명령어
        for (int q = 1;q < Q - 1;q++) {
            String data = br.readLine();
            if (data.startsWith("200")) raceStart(data);    // 200 명령어
            else updateDistance(data);                      // 300 명령어
        }
        System.out.println(selectBestRabbit(br));           // 400 명령어
    }

    public static void print() {
        System.out.println("SELCT : " + SELECT_RABBIT);
        System.out.println("SCORE : " + SELECT_RABBIT);
        System.out.println("FINAL : " + FINAL_RABBIT);
    }

    /**
     * P 마리의 토끼가 N×M 크기의 격자 위에서 경주를 진행할 준비를 합니다.
     * 각 토끼에게는 고유한 번호가 붙어있으며, 한번 움직일 시 꼭 이동해야 하는 거리도 정해져 있습니다.
     * i번 토끼의 고유번호는 pid, 이동해야 하는 거리는 d입니다.
     * 처음 토끼들은 전부 (1행, 1열)에 있습니다.
     * @param br    : 입력 스트림
     * @call        : 반드시 처음에 1번만 실행
     * */
    public static void raceReady(BufferedReader br) throws Exception {
        StringTokenizer st = new StringTokenizer(br.readLine());
        String c = st.nextToken();

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());

        for (int p = 0;p < P;p++) {
            int id = Integer.parseInt(st.nextToken());
            int distance = Integer.parseInt(st.nextToken());
            Rabbit rabbit = new Rabbit(id, distance);
            SELECT_RABBIT.offer(rabbit);
            SCORE_RABBIT.offer(rabbit);
            FINAL_RABBIT.offer(rabbit);
        }

        print();
    }

    /**
     * 가장 우선순위가 높은 토끼(SELECT_RABBIT)를 뽑아 멀리 보내주는 것을 K번 반복(중복 선택 가능)합니다.
     * 토끼 선정 후 상하좌우 방향대로 각 d만큼 이동했을 때의 위치 중 우선순위(SELECT_POINT)로 이동합니다.
     * 이동 중 격자를 벗어나게 된다면 방향 반대로 바꿔 한 칸 이동합니다.
     * 최종 이동하게 된 좌표 (r, c)에 대해, 선정된 토끼 외 전부 r+c만큼 점수를 동시에 얻게 됩니다.
     * K번이 모두 종료된다면, 최종 점수 S를 추가로 얻는 우선순위 토끼(SCORE_RABBIT)를 선정해 부여합니다.
     * 단, 이 경우에는 K번의 턴 동안 한번이라도 뽑혔던 적이 있던 토끼 중 가장 우선순위가 높은 토끼를 골라야만 함에 꼭 유의합니다.
     * @param command : 명령 문자열
     * @call          : 최대 2000번
     * */
    public static int D = 4;
    public static int[][] DELTA = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    public static void raceStart(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String c = st.nextToken();
        int K = Integer.parseInt(st.nextToken());   // 반복 횟수 (1 <= K <= min(100, P))
        int S = Integer.parseInt(st.nextToken());   // 최종 점수 (1 <= S <= 1000000)

        PriorityQueue<Rabbit> newSelectRabbit = getNewRabbitPQ();

        System.out.println(command);
        for (int k = 0;k < K;k++) {
            SELECT_POINT.clear();
            newSelectRabbit.clear();

            Rabbit rabbit = SELECT_RABBIT.poll();
            Point nowPoint = new Point(rabbit.x, rabbit.y);
            System.out.println(k + ")\tSELCET RABBIT : " + rabbit);

            for (int d = 0;d < D;d++)
                SELECT_POINT.offer(getNextPoint(nowPoint, rabbit.d, 0));

            System.out.println("\t" + SELECT_POINT);


            SELECT_RABBIT = newSelectRabbit;
        }

        SELECT_RABBIT.peek().score += S;
    }

    /**
     * 해당 방향으로 이동한 거리를 반환한다.
     * @param nowPoint  : 지금 좌표
     * @param dist      : 이동해야하는 거리
     * @param dect      : 이동해야하는 방향
     * @return          : 이동한 결과 좌표
     * */
    public static Point getNextPoint(Point nowPoint, long dist, int dect) {
        int x = nowPoint.x, y = nowPoint.y;

        return new Point(x, y);
    }

    public static PriorityQueue<Rabbit> getNewRabbitPQ() {
        PriorityQueue<Rabbit> result = new PriorityQueue<>(new Comparator<Rabbit>() {
            @Override
            public int compare(Rabbit o1, Rabbit o2) {
                if (o1.jumpCount == o2.jumpCount) {
                    if ((o1.x + o1.y) == (o2.x + o2.y)) {
                        if (o1.x == o2.x) {
                            if (o1.y == o2.y)
                                return o1.id - o2.id;
                            return o1.y - o2.y;
                        } return o1.x - o2.x;
                    } return (o1.x + o1.y) - (o2.x + o2.y);
                } return o1.jumpCount - o2.jumpCount;
            }
        });
        return result;
    }

    /**
     * [토끼 선정 우선순위]
     * 1. 현재까지의 총 점프 횟수가 적은 토끼
     * 2. 현재 서있는 행 번호 + 열 번호가 작은 토끼
     * 3. 행 번호가 작은 토끼
     * 4. 열 번호가 작은 토끼
     * 5. 고유번호가 작은 토끼
     */
    public static PriorityQueue<Rabbit> SELECT_RABBIT = new PriorityQueue<>(new Comparator<Rabbit>() {
        @Override
        public int compare(Rabbit o1, Rabbit o2) {
            if (o1.jumpCount == o2.jumpCount) {
                if ((o1.x + o1.y) == (o2.x + o2.y)) {
                    if (o1.x == o2.x) {
                        if (o1.y == o2.y)
                            return o1.id - o2.id;
                        return o1.y - o2.y;
                    } return o1.x - o2.x;
                } return (o1.x + o1.y) - (o2.x + o2.y);
            } return o1.jumpCount - o2.jumpCount;
        }
    });

    /**
     * [위치 선정 우선순위]
     * 1. 행 번호 + 열 번호가 큰 칸
     * 2. 행 번호가 큰 칸
     * 3. 열 번호가 큰 칸
     */
    public static PriorityQueue<Point> SELECT_POINT = new PriorityQueue<>(new Comparator<Point>() {
        @Override
        public int compare(Point p1, Point p2) {
            if ((p1.x + p1.y) == (p2.x + p2.y)) {
                if (p1.x == p2.x) {
                    return p2.y - p1.y;
                } return p2.x - p1.x;
            } return (p2.x + p2.y) - (p1.x + p1.y);
        }
    });

    /**
     * [점수 S 부여 우선순위]
     * 1. 현재 서있는 행 번호 + 열 번호가 큰 토끼
     * 2. 행 번호가 큰 토끼
     * 3. 열 번호가 큰 토끼
     * 4. 고유번호가 큰 토끼
     */
    public static PriorityQueue<Rabbit> SCORE_RABBIT = new PriorityQueue<>(new Comparator<Rabbit>() {
        @Override
        public int compare(Rabbit o1, Rabbit o2) {
            if ((o1.x + o1.y) == (o2.x + o2.y)) {
                if (o1.x == o2.x) {
                    if (o1.y == o2.y)
                        return o2.id - o1.id;
                    return o2.y - o1.y;
                } return o2.x - o1.x;
            } return (o2.x + o2.y) - (o1.x + o1.y);
        }
    });

    /**
     * 고유번호가 pid 인 토끼의 이동거리를 L배 해줍니다.
     * 단, 계산 도중 특정 토끼의 이동거리가 10억을 넘어가는 일은 발생하지 않음을 가정해도 좋습니다.
     * @param command : 명령 문자열
     * @call          : 최대 2000번
     * */
    public static void updateDistance(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String c = st.nextToken();
        int id = Integer.parseInt(st.nextToken());
        int L = Integer.parseInt(st.nextToken());
    }

    /**
     * 각 토끼가 모든 경주를 진행하며 얻은 점수 중 가장 높은 점수를 출력합니다.
     * @param br      : 입력스트림
     * @return        : 가장 높은 점수
     * */
    public static long selectBestRabbit(BufferedReader br) throws Exception {
        return FINAL_RABBIT.peek().score;
    }

    public static PriorityQueue<Rabbit> FINAL_RABBIT = new PriorityQueue<>(new Comparator<Rabbit>() {
        @Override
        public int compare(Rabbit o1, Rabbit o2) {
            return (int)(o2.score - o1.score);
        }
    });

    public static class Rabbit {
        int id, x, y, jumpCount;
        long d, score;

        public Rabbit(int id, long d) {
            this.id = id;           // 고유번호 (1 <= id <= 10000000)
            this.d = d;             // 이동해야하는 거리 (1 <= d <= 1000000000)
            this.x = 1;
            this.y = 1;
            this.jumpCount = 0;
            this.score = 0;
        }

        @Override
        public String toString() {
            return "(id=" + id +
                    ", x=" + x +
                    ", y=" + y +
                    ", jumpCount=" + jumpCount +
                    ", d=" + d +
                    ", score=" + score +
                    ')';
        }
    }

    public static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(x=" + x +
                    ", y=" + y +
                    ')';
        }
    }
}
