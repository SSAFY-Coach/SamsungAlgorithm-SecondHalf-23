import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    /*
    delta array
    0: 상
    1: 하
    2: 좌
    3: 우
    4: 상좌
    5: 상우
    6: 하좌
    7: 하우
     */
    private static final int[] dy = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] dx = {0, 0, -1, 1, -1, 1, -1, 1};
    private static final int[] directionMapper = {0, 5, 3, 7, 1, 6, 2, 4};
    private static int N, M, K;
    private static List<Atom>[][] map;

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
        map = new List[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                map[i][j] = new LinkedList<>();
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int mass = Integer.parseInt(st.nextToken());
            int speed = Integer.parseInt(st.nextToken());
            int direction = directionMapper[Integer.parseInt(st.nextToken())];
            Atom atom = new Atom(y, x, mass, speed, direction);
            map[y][x].add(atom);
        }
    }

    private static int solution() {
        int currentTime = 0;
        while (currentTime < K) {
            currentTime++;
            moveAtoms();
            combineAtoms();
        }
        return getAtomSum();
    }

    private static void moveAtoms() {
        List<Atom> movedAtomList = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (!map[i][j].isEmpty()) {
                    for (Atom atom : map[i][j]) {
                        int ny = getCircularCoord(atom.y + dy[atom.direction] * atom.speed);
                        int nx = getCircularCoord(atom.x + dx[atom.direction] * atom.speed);
                        movedAtomList.add(new Atom(ny, nx, atom.mass, atom.speed, atom.direction));
                    }
                    map[i][j].clear();
                }
            }
        }
        for (Atom atom : movedAtomList) {
            map[atom.y][atom.x].add(atom);
        }
    }

    private static void combineAtoms() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (map[i][j].size() >= 2) {
                    combine(map[i][j]);
                }
            }
        }
    }

    private static void combine(List<Atom> overlappedAtomList) {
        int y = overlappedAtomList.get(0).y;
        int x = overlappedAtomList.get(0).x;
        int newMass = overlappedAtomList.stream()
                .mapToInt(atom -> atom.mass)
                .sum() / 5;

        if (newMass > 0) {
            int newSpeed = overlappedAtomList.stream()
                    .mapToInt(atom -> atom.speed)
                    .sum() / overlappedAtomList.size();
            int[] newDirection = isAllSameKindaDirection(overlappedAtomList) ? new int[]{0, 1, 2, 3} : new int[]{4, 5, 6, 7};
            map[y][x].clear();
            for (int i = 0; i < 4; i++) {
                map[y][x].add(new Atom(y, x, newMass, newSpeed, newDirection[i]));
            }
        } else {
            map[y][x].clear();
        }
    }

    private static boolean isAllSameKindaDirection(List<Atom> overlappedAtomList) {
        return overlappedAtomList.stream().allMatch(atom -> atom.direction < 4)
                || overlappedAtomList.stream().allMatch(atom -> atom.direction >= 4);
    }

    private static int getCircularCoord(int num) {
        while (num <= 0) {
            num += N;
        }
        while (num > N) {
            num -= N;
        }
        return num;
    }

    private static int getAtomSum() {
        int sum = 0;
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (!map[i][j].isEmpty()) {
                    for (Atom atom : map[i][j]) {
                        sum += atom.mass;
                    }
                }
            }
        }
        return sum;
    }

    private static class Atom {
        int y;
        int x;
        int mass;
        int speed;
        int direction;

        public Atom(int y, int x, int mass, int speed, int direction) {
            this.y = y;
            this.x = x;
            this.mass = mass;
            this.speed = speed;
            this.direction = direction;
        }
    }
}