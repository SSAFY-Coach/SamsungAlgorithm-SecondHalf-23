import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static final int[] dy = {-1, 1, 0, 0};
    private static final int[] dx = {0, 0, -1, 1};
    private static final int VIRUS = 0;
    private static final int WALL = -1;
    private static final int HOSPITAL = -2;

    private static int N, M;
    private static int[][] map;
    private static List<Node> hospitalList = new ArrayList<>();
    private static int minVaccineCount = -1;

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
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] == -HOSPITAL) {
                    map[i][j] = HOSPITAL;
                    hospitalList.add(new Node(i, j));
                } else if (map[i][j] == -WALL) {
                    map[i][j] = WALL;
                }
            }
        }
    }

    private static int solution() {
        Node[] pickedHospitals = new Node[M];
        pickHospitals(pickedHospitals, 0, 0);
        return minVaccineCount;
    }

    private static void pickHospitals(Node[] pickedHospitals, int srcIndex, int tgtIndex) {
        if (tgtIndex == M) {
            initMap();
            int vaccineCount = countVaccine(pickedHospitals);
            if (minVaccineCount == -1) {
                minVaccineCount = vaccineCount;
            } else if (vaccineCount != -1) {
                minVaccineCount = Math.min(minVaccineCount, vaccineCount);
            }
            return;
        } else if (srcIndex == hospitalList.size()) {
            return;
        }
        pickedHospitals[tgtIndex] = hospitalList.get(srcIndex);
        pickHospitals(pickedHospitals, srcIndex + 1, tgtIndex + 1);
        pickHospitals(pickedHospitals, srcIndex + 1, tgtIndex);
    }

    private static void initMap() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] != WALL && map[i][j] != HOSPITAL) {
                    map[i][j] = 0;
                }
            }
        }
    }

    private static int countVaccine(Node[] pickedHospitals) {
        Queue<Node> vaccineQueue = new LinkedList<>();
        boolean[][] visited = new boolean[N][N];
        for (Node pickedHospital : pickedHospitals) {
            vaccineQueue.offer(new Node(pickedHospital.y, pickedHospital.x));
            visited[pickedHospital.y][pickedHospital.x] = true;
        }
        int size = vaccineQueue.size();
        int count = 1;
        while (!vaccineQueue.isEmpty()) {
            Node vaccine = vaccineQueue.poll();
            for (int i = 0; i < 4; i++) {
                int ny = vaccine.y + dy[i];
                int nx = vaccine.x + dx[i];
                if (isInMap(ny, nx) && !visited[ny][nx] && (map[ny][nx] == VIRUS || map[ny][nx] == HOSPITAL)) {
                    if (map[ny][nx] == VIRUS) {
                        map[ny][nx] = count;
                    }
                    visited[ny][nx] = true;
                    vaccineQueue.offer(new Node(ny, nx));
                }
            }
            if (--size == 0) {
                size = vaccineQueue.size();
                count++;
            }
        }
        return getMinCount();
    }

    private static int getMinCount() {
        int maxCount = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] == VIRUS) {
                    return -1;
                } else if (map[i][j] > 0) {
                    maxCount = Math.max(maxCount, map[i][j]);
                }
            }
        }
        return maxCount;
    }

    private static boolean isInMap(int y, int x) {
        return 0 <= y && y < N && 0 <= x && x < N;
    }

    private static class Node {
        int y;
        int x;

        public Node(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }
}