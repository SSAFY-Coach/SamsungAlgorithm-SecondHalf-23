import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    // direction 우선 순위 우,하,좌,상
    private static final int[] laserDy = {0, 1, 0, -1};
    private static final int[] laserDx = {1, 0, -1, 0};
    private static final int[] bombDy = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static final int[] bombDx = {-1, 0, 1, 1, 1, 0, -1, -1};

    private static int N, M, K;
    private static Cannon[][] map;
    private static List<Cannon> cannonList = new LinkedList<>();


    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new Cannon[N + 1][M + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= M; j++) {
                int attackScore = Integer.parseInt(st.nextToken());
                if (attackScore != 0) {
                    map[i][j] = new Cannon(i, j, attackScore);
                    cannonList.add(map[i][j]);
                }
            }
        }
    }

    private static int solution() {
        for (int i = 1; i <= K && cannonList.size() > 1; i++) {
            Cannon[] minMaxCannons = findMinMaxCannonsAndIncreaseMinCannonScore(i);
            Cannon minCannon = minMaxCannons[0];
            Cannon maxCannon = minMaxCannons[1];
//            System.out.println("minCannon = " + minCannon);
//            System.out.println("maxCannon = " + maxCannon);
            boolean isSucceed = laserAttack(minCannon, maxCannon);
            if (!isSucceed) {
                bombAttack(minCannon, maxCannon);
            }
//            System.out.println("===== after attack =====");
//            printMap();
            fixAndFallDownCannons();
//            System.out.println("===== after fix =====");
//            printMap();
        }
        Collections.sort(cannonList);
        return cannonList.get(0).attackScore;
    }

    private static Cannon[] findMinMaxCannonsAndIncreaseMinCannonScore(int turn) {
        Collections.sort(cannonList);
        Cannon minCannon = cannonList.get(cannonList.size() - 1);
        Cannon maxCannon = cannonList.get(0);

        minCannon.attackScore += N + M;
        minCannon.attackTurn = turn;
        minCannon.isRelevantToAttack = true;
        return new Cannon[]{minCannon, maxCannon};
    }

    private static boolean laserAttack(Cannon minCannon, Cannon maxCannon) {
        findLaserRoute(minCannon, maxCannon);
        if (maxCannon.prevCannon != null) {
            Cannon cannon = maxCannon;
            attack(cannon, minCannon.attackScore);
            while (true) {
                cannon = cannon.prevCannon;
                if (cannon.equals(minCannon)) {
                    break;
                }
                attack(cannon, minCannon.attackScore / 2);
            }
        }
        return maxCannon.prevCannon != null;
    }


    private static void findLaserRoute(Cannon minCannon, Cannon maxCannon) {
        Queue<Cannon> cannonQueue = new LinkedList<>();
        boolean[][] visited = new boolean[N + 1][M + 1];
        cannonQueue.offer(minCannon);
        visited[minCannon.y][minCannon.x] = true;
        while (!cannonQueue.isEmpty() && !visited[maxCannon.y][maxCannon.x]) {
            Cannon cannon = cannonQueue.poll();
            for (int i = 0; i < 4; i++) {
                int ny = getCoord(cannon.y + laserDy[i], N);
                int nx = getCoord(cannon.x + laserDx[i], M);
                if (!visited[ny][nx] && map[ny][nx] != null) {
                    visited[ny][nx] = true;
                    map[ny][nx].prevCannon = cannon;
                    cannonQueue.offer(map[ny][nx]);
                }
            }
        }
    }

    private static void bombAttack(Cannon minCannon, Cannon maxCannon) {
        attack(maxCannon, minCannon.attackScore);
        for (int i = 0; i < 8; i++) {
            int ny = getCoord(maxCannon.y + bombDy[i], N);
            int nx = getCoord(maxCannon.x + bombDx[i], M);
            if (map[ny][nx] != null && !minCannon.equals(map[ny][nx])) {
                attack(map[ny][nx], minCannon.attackScore / 2);
            }
        }
    }

    private static void fixAndFallDownCannons() {
        cannonList.stream()
                .filter(cannon -> cannon.attackScore == 0)
                .forEach(cannon -> map[cannon.y][cannon.x] = null);
        cannonList = cannonList.stream()
                .filter(cannon -> cannon.attackScore > 0)
                .peek(cannon -> {
                    if (!cannon.isRelevantToAttack) {
                        cannon.attackScore++;
                    } else {
                        cannon.isRelevantToAttack = false;
                    }
                    cannon.prevCannon = null;
                })
                .collect(Collectors.toList());
    }

    private static void attack(Cannon target, int damage) {
        target.attackScore = Math.max(target.attackScore - damage, 0);
        target.isRelevantToAttack = true;
    }

    private static int getCoord(int coord, int m) {
        if (coord > m) {
            coord -= m;
        } else if (coord <= 0) {
            coord += m;
        }
        return coord;
    }

    private static class Cannon implements Comparable<Cannon> {
        int y;
        int x;
        int attackScore;
        int attackTurn;
        boolean isRelevantToAttack = false;
        Cannon prevCannon;


        public Cannon(int y, int x, int attackScore) {
            this.y = y;
            this.x = x;
            this.attackScore = attackScore;
        }

        @Override
        public int compareTo(Cannon cannon) {
            if (this.attackScore != cannon.attackScore) {
                return cannon.attackScore - this.attackScore;
            } else if (this.attackTurn != cannon.attackTurn) {
                return this.attackTurn - cannon.attackTurn;
            } else if (this.y + this.x != cannon.y + cannon.x) {
                return this.y + this.x - cannon.y + cannon.x;
            } else {
                return this.x - cannon.x;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cannon cannon = (Cannon) o;
            return y == cannon.y && x == cannon.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x);
        }

        @Override
        public String toString() {
            return "Cannon{" +
                    "y=" + y +
                    ", x=" + x +
                    ", attackScore=" + attackScore +
                    ", attackTurn=" + attackTurn +
                    ", isRelevantToAttack=" + isRelevantToAttack +
                    ", prevCannon=" + prevCannon +
                    '}';
        }
    }

    private static void printMap() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (map[i][j] != null)
                    System.out.print(map[i][j].attackScore + "\t");
                else
                    System.out.print("0\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}