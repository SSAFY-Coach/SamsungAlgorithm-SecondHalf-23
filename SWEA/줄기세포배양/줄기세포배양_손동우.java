import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Solution {
    private static final int[] dy = {-1, 1, 0, 0};
    private static final int[] dx = {0, 0, -1, 1};
    private static final int MAX_LENGTH = 1000;

    private static int T;
    private static int N, M, K;
    private static int[][] map = new int[MAX_LENGTH][MAX_LENGTH];
    private static Queue<Cell> cellQueue = new LinkedList<>();
    private static Queue<Node> expandedCellQueue = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        T = Integer.parseInt(br.readLine());
        for (int tc = 1; tc <= T; tc++) {
            init(br);
            int answer = solution();
            System.out.println("#" + tc + " " + answer);
        }
    }

    private static void init(BufferedReader br) throws Exception {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        cellQueue.clear();
        for (int i = 0; i < MAX_LENGTH; i++) {
            Arrays.fill(map[i], 0);
        }
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                int life = Integer.parseInt(st.nextToken());
                int y = MAX_LENGTH / 2 + i;
                int x = MAX_LENGTH / 2 + j;
                map[y][x] = life;
                if (life > 0) {
                    cellQueue.offer(new Cell(y, x, life, Cell.UNACTIVATED));
                }
            }
        }
    }

    private static int solution() {
        for (int i = 0; i < K; i++) {
            simulate();
        }
        return cellQueue.size();
    }

    private static void simulate() {
        int size = cellQueue.size();
        for (int i = 0; i < size; i++) {
            Cell cell = cellQueue.poll();
            if (cell.status == Cell.UNACTIVATED) {
                simulateUnactivated(cell);
            } else if (cell.status == Cell.ACTIVATED) {
                simulateActivated(cell);
            }
        }
        while (!expandedCellQueue.isEmpty()) {
            Node expandedCell = expandedCellQueue.poll();
            if (map[expandedCell.y][expandedCell.x] < 0) {
                map[expandedCell.y][expandedCell.x] = -map[expandedCell.y][expandedCell.x];
                cellQueue.offer(new Cell(expandedCell.y, expandedCell.x, map[expandedCell.y][expandedCell.x], Cell.UNACTIVATED));
            }
        }
    }

    private static void simulateUnactivated(Cell cell) {
        if (cell.currentLife == 1) {
            cell.status = Cell.ACTIVATED;
            cell.currentLife = cell.life;
        } else {
            cell.currentLife--;
        }
        cellQueue.offer(cell);
    }

    private static void simulateActivated(Cell cell) {
        if (cell.currentLife == cell.life) {
            expand(cell);
        }

        if (cell.currentLife == 1) {
            cell.currentLife = cell.life;
            cell.status = Cell.DEAD;
        } else {
            cell.currentLife--;
            cellQueue.offer(cell);
        }
    }

    private static void expand(Cell cell) {
        for (int i = 0; i < 4; i++) {
            int ny = cell.y + dy[i];
            int nx = cell.x + dx[i];
            if (map[ny][nx] <= 0) {
                map[ny][nx] = Math.min(map[ny][nx], -cell.life);
                expandedCellQueue.offer(new Node(ny, nx));
            }
        }
    }

    private static class Node {
        int y;
        int x;

        public Node(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }

    private static class Cell extends Node {
        int life;
        int currentLife;
        int status; // 0: 비활성 상태, 1: 활성 상태, 2: 죽은 상태
        static final int UNACTIVATED = 0;
        static final int ACTIVATED = 1;
        static final int DEAD = 2;

        public Cell(int y, int x, int life, int status) {
            super(y, x);
            this.life = life;
            this.currentLife = life;
            this.status = status;
        }
    }
}