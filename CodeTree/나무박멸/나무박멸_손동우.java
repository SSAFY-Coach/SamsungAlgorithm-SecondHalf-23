import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static final int WALL = -100;
    private static final int[] treeDy = {-1, 1, 0, 0};
    private static final int[] treeDx = {0, 0, -1, 1};
    private static final int[] herbicideDy = {-1, -1, 1, 1};
    private static final int[] herbicideDx = {-1, 1, -1, 1};
    private static int N, M, K, C;
    private static int[][] map;

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
        C = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] == -1) {
                    map[i][j] = WALL;
                }
            }
        }
    }

    private static int solution() {
        int answer = 0;
        printMap();
        for (int i = 0; i < M; i++) {
            growAndPropagateTrees();
            System.out.println("==============after growAndPropagate==============");
            printMap();
            reduceHerbicide();
            answer += putHerbicide();
            System.out.println("==============after putHerbicide==============");
            printMap();
        }
        return answer;
    }

    private static void growAndPropagateTrees() {
        Queue<Node> treeQueue = growAndGetTrees();
        Queue<Tree> propagatedTreeQueue = new LinkedList<>();
        while (!treeQueue.isEmpty()) {
            Node tree = treeQueue.poll();
            propagatedTreeQueue.addAll(propagateTree(tree));
        }
        while (!propagatedTreeQueue.isEmpty()) {
            Tree tree = propagatedTreeQueue.poll();
            map[tree.y][tree.x] += tree.numberOfTrees;
        }
    }

    private static Queue<Node> growAndGetTrees() {
        Queue<Node> treeQueue = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] > 0) {
                    map[i][j] += countAdjacentTrees(i, j);
                    treeQueue.offer(new Node(i, j));
                }
            }
        }
        return treeQueue;
    }

    private static int countAdjacentTrees(int y, int x) {
        int adjacentTreeCount = 0;
        for (int i = 0; i < 4; i++) {
            int ny = y + treeDy[i];
            int nx = x + treeDx[i];
            if (isInMap(ny, nx) && map[ny][nx] > 0) {
                adjacentTreeCount++;
            }
        }
        return adjacentTreeCount;
    }

    private static List<Tree> propagateTree(Node tree) {
        List<Tree> propagatedTreeList = new ArrayList<>();
        int availableCellCount = countAvailableCells(tree);
        for (int i = 0; i < 4; i++) {
            int ny = tree.y + treeDy[i];
            int nx = tree.x + treeDx[i];
            if (isInMap(ny, nx) && map[ny][nx] == 0) {
                propagatedTreeList.add(new Tree(ny, nx, map[tree.y][tree.x] / availableCellCount));
            }
        }
        return propagatedTreeList;
    }

    private static int countAvailableCells(Node tree) {
        int availableCellCount = 0;
        for (int i = 0; i < 4; i++) {
            int ny = tree.y + treeDy[i];
            int nx = tree.x + treeDx[i];
            if (isInMap(ny, nx) && map[ny][nx] == 0) {
                availableCellCount++;
            }
        }
        return availableCellCount;
    }

    private static void reduceHerbicide() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (WALL < map[i][j] && map[i][j] < 0) {
                    map[i][j]++;
                }
            }
        }
    }

    private static int putHerbicide() {
        Tree maxKillingCell = findMaxKillingCell();
        System.out.println("maxKillingCell = " + maxKillingCell);
        if (maxKillingCell.numberOfTrees == 0) {
            return 0;
        }
        int killingCount = map[maxKillingCell.y][maxKillingCell.x];
        map[maxKillingCell.y][maxKillingCell.x] = -C;
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j <= K; j++) {
                int ny = maxKillingCell.y + herbicideDy[i] * j;
                int nx = maxKillingCell.x + herbicideDx[i] * j;

                if (isInMap(ny, nx)) {
                    if (map[ny][nx] > 0) {
                        killingCount += map[ny][nx];
                        map[ny][nx] = -C;
                    } else if (map[ny][nx] > WALL) {
                        map[ny][nx] = -C;
                        break;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return killingCount;
    }

    private static Tree findMaxKillingCell() {
        Tree maxKillingCell = new Tree(0, 0, 0);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] > 0) {
                    int killingCount = countKillingTrees(i, j);
                    if (killingCount > maxKillingCell.numberOfTrees) {
                        maxKillingCell.numberOfTrees = killingCount;
                        maxKillingCell.y = i;
                        maxKillingCell.x = j;
                    }
                }
            }
        }
        return maxKillingCell;
    }

    private static int countKillingTrees(int y, int x) {
        int killingCount = map[y][x];
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j <= K; j++) {
                int ny = y + herbicideDy[i] * j;
                int nx = x + herbicideDx[i] * j;
                if (isInMap(ny, nx) && map[ny][nx] > 0) {
                    killingCount += map[ny][nx];
                } else {
                    break;
                }
            }
        }
        return killingCount;
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

        @Override
        public String toString() {
            return "Node{" +
                    "y=" + y +
                    ", x=" + x +
                    '}';
        }
    }

    private static class Tree extends Node {
        int numberOfTrees;

        public Tree(int y, int x, int numberOfTrees) {
            super(y, x);
            this.numberOfTrees = numberOfTrees;
        }

        @Override
        public String toString() {
            return "Tree{" +
                    "numberOfTrees=" + numberOfTrees +
                    ", y=" + y +
                    ", x=" + x +
                    '}';
        }
    }

    private static void printMap() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] == WALL) {
                    System.out.print("**\t\t");
                } else {
                    System.out.print(map[i][j] + "\t\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}