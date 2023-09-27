import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static final int[] dy = {0, 0, -1, -1, -1, 0, 1, 1, 1};
    private static final int[] dx = {0, 1, 1, 0, -1, -1, -1, 0, 1};
    private static int N, M;
    private static int[][] map;
    private static List<Move> moveList = new ArrayList<>();
    private static Set<Node> supplementSet = new HashSet<>();

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
        map = new int[N][N];
        addSupplements();
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int direction = Integer.parseInt(st.nextToken());
            int speed = Integer.parseInt(st.nextToken());
            moveList.add(new Move(direction, speed));
        }
    }

    private static void addSupplements() {
        supplementSet.add(new Node(N - 1, 0));
        supplementSet.add(new Node(N - 1, 1));
        supplementSet.add(new Node(N - 2, 0));
        supplementSet.add(new Node(N - 2, 1));
    }

    private static int solution() {
        for (Move move : moveList) {
            moveSupplements(move);
            growUpOne();
            growUpNearBy();
            cutGreaterThanOrEqualsToTwo();
        }
        return getSumHeight();
    }

    private static void moveSupplements(Move move) {
        supplementSet.forEach(supplement -> {
            supplement.y = (supplement.y + dy[move.direction] * move.speed + N) % N;
            supplement.x = (supplement.x + dx[move.direction] * move.speed + N) % N;
        });
    }

    private static void growUpOne() {
        supplementSet.forEach(supplement -> {
            map[supplement.y][supplement.x]++;
        });
    }

    private static void growUpNearBy() {
        supplementSet.forEach(supplement -> {
            int nearByCount = 0;
            for (int i = 2; i <= 8; i += 2) {
                int ny = supplement.y + dy[i];
                int nx = supplement.x + dx[i];
                if (isInMap(ny, nx) && map[ny][nx] > 0) {
                    nearByCount++;
                }
            }
            map[supplement.y][supplement.x] += nearByCount;
        });
    }

    private static void cutGreaterThanOrEqualsToTwo() {
        List<Node> newSupplementList = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] >= 2 && isNotSupplement(i, j)) {
                    map[i][j] -= 2;
                    newSupplementList.add(new Node(i, j));
                }
            }
        }
        supplementSet.clear();
        supplementSet.addAll(newSupplementList);
    }

    private static int getSumHeight() {
        int sum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sum += map[i][j];
            }
        }
        return sum;
    }

    private static boolean isNotSupplement(int y, int x) {
        return supplementSet.stream().noneMatch(supplement -> supplement.y == y && supplement.x == x);
    }

    private static boolean isInMap(int y, int x) {
        return 0 <= y && y < N && 0 <= x && x < N;
    }

    private static class Move {

        int direction;
        int speed;

        public Move(int direction, int speed) {
            this.direction = direction;
            this.speed = speed;
        }
    }

    private static class Node {
        int y;
        int x;

        public Node(int y, int x) {
            this.y = y;
            this.x = x;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "y=" + y +
                    ", x=" + x +
                    '}';
        }
    }
}