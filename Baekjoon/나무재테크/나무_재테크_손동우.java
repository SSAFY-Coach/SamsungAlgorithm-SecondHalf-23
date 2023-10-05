import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    /*
    0 1 2
    7 - 3
    6 5 4
     */
    private static final int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static final int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};

    private static int N, M, K;
    private static PriorityQueue<Integer>[][] treesPq;
    private static final Queue<Tree> deadTreesQueue = new LinkedList<>();
    private static int[][] nourishments;
    private static int[][] s2d2;

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
        treesPq = new PriorityQueue[N + 1][N + 1];
        s2d2 = new int[N + 1][N + 1];
        nourishments = new int[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                s2d2[i][j] = Integer.parseInt(st.nextToken());
                nourishments[i][j] = 5;
                treesPq[i][j] = new PriorityQueue<>();
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int age = Integer.parseInt(st.nextToken());
            treesPq[y][x].offer(age);
        }
    }

    private static int solution() {
        for (int i = 0; i < K; i++) {
            spring();
            summer();
            fall();
            winter();
        }
        return countTrees();
    }

    private static void spring() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (!treesPq[i][j].isEmpty()) {
                    List<Integer> tempTreeList = new ArrayList<>();
                    while (!treesPq[i][j].isEmpty()) {
                        int age = treesPq[i][j].poll();
                        if (nourishments[i][j] >= age) {
                            tempTreeList.add(age + 1);
                            nourishments[i][j] -= age;
                        } else {
                            deadTreesQueue.offer(new Tree(i, j, age));
                        }
                    }
                    treesPq[i][j].addAll(tempTreeList);
                }
            }
        }
    }

    private static void summer() {
        while (!deadTreesQueue.isEmpty()) {
            Tree tree = deadTreesQueue.poll();
            nourishments[tree.y][tree.x] += tree.age / 2;
        }
    }

    private static void fall() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (!treesPq[i][j].isEmpty()) {
                    int propagationCount = 0;
                    for (Integer age : treesPq[i][j]) {
                        if (age % 5 == 0) {
                            propagationCount++;
                        }
                    }
                    if (propagationCount > 0) {
                        propagate(propagationCount, i, j);
                    }
                }
            }
        }
    }

    private static void propagate(int propagationCount, int y, int x) {
        for (int i = 0; i < 8; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (isInMap(ny, nx)) {
                for (int j = 0; j < propagationCount; j++) {
                    treesPq[ny][nx].add(1);
                }
            }
        }

    }

    private static void winter() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                nourishments[i][j] += s2d2[i][j];
            }
        }
    }

    private static int countTrees() {
        int treeCount = 0;
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                treeCount += treesPq[i][j].size();
            }
        }
        return treeCount;
    }

    private static boolean isInMap(int y, int x) {
        return 0 < y && y <= N && 0 < x && x <= N;
    }

    private static class Tree {
        int y;
        int x;
        int age;

        public Tree(int y, int x, int age) {
            this.y = y;
            this.x = x;
            this.age = age;
        }
    }
}