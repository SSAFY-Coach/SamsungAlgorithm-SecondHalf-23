import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;

public class Main {
    //laser delta array 우선 순위 : 우 하 상 좌
    private static final int[] laserDy = {0, 1, -1, 0};
    private static final int[] laserDx = {1, 0, 0, -1};
    private static final int[] bombDy = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static final int[] bombDx = {-1, 0, 1, 1, 1, 0, -1, -1};
    private static int N, M, K;
    private static Cannon[][] map;
    private static int cannonCount;

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
                map[i][j] = new Cannon(i, j, Integer.parseInt(st.nextToken()));
                if (map[i][j].attackScore > 0) {
                    cannonCount++;
                }
            }
        }
    }

    private static int solution() {
        for (int i = 1; i <= K && cannonCount > 1; i++) {
            // 가장 약한 캐논 찾기
            Cannon minCannon = findMinCannon();
            // 가장 강한 캐논(공격 타겟) 찾기
            Cannon maxCannon = findMaxCannon();
            // 어드벤티지로 공격력 올려주기, 공격과 관련 있음 표시, 공격 턴 업데이트
            attackScoreUp(minCannon, i);
            // 레이저 공격 시도
            boolean isSucceed = laserAttack(minCannon, maxCannon);
            if (!isSucceed) {
                // 레이저 공격 실패했으면 폭탄 공격
                bombAttack(minCannon, maxCannon);
            }
            // 정비하기, 공격과 관련 없음 초기화
            fixCannonAndInitIsRelevantToAttack();
        }
        return findMaxCannon().attackScore;
    }

    private static Cannon findMinCannon() {
        int minAttackScore = Integer.MAX_VALUE;
        int maxAttackTurn = -1;
        int maxY = -1;
        int maxX = -1;
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (map[i][j].attackScore <= 0) {
                    continue;
                }
                if (minAttackScore > map[i][j].attackScore) {
                    minAttackScore = map[i][j].attackScore;
                    maxAttackTurn = map[i][j].attackTurn;
                    maxY = i;
                    maxX = j;
                } else if (minAttackScore == map[i][j].attackScore) {
                    if (maxAttackTurn < map[i][j].attackTurn) {
                        maxAttackTurn = map[i][j].attackTurn;
                        maxY = i;
                        maxX = j;
                    } else if (maxAttackTurn == map[i][j].attackTurn) {
                        if (maxY + maxX < i + j) {
                            maxY = i;
                            maxX = j;
                        } else if (maxY + maxX == i + j) {
                            if (maxX < j) {
                                maxY = i;
                                maxX = j;
                            }
                        }
                    }
                }
            }
        }
        return map[maxY][maxX];
    }

    private static Cannon findMaxCannon() {
        int maxAttackScore = -1;
        int minAttackTurn = Integer.MAX_VALUE;
        int minY = 0;
        int minX = 0;
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (maxAttackScore < map[i][j].attackScore) {
                    maxAttackScore = map[i][j].attackScore;
                    minAttackTurn = map[i][j].attackTurn;
                    minY = i;
                    minX = j;
                } else if (maxAttackScore == map[i][j].attackScore) {
                    if (minAttackTurn > map[i][j].attackTurn) {
                        minAttackTurn = map[i][j].attackTurn;
                        minY = i;
                        minX = j;
                    } else if (minAttackTurn == map[i][j].attackTurn) {
                        if (minY + minX > i + j) {
                            minY = i;
                            minX = j;
                        } else if (minY + minX == i + j) {
                            if (minX > j) {
                                minY = i;
                                minX = j;
                            }
                        }
                    }
                }
            }
        }
        return map[minY][minX];
    }

    private static boolean laserAttack(Cannon from, Cannon to) {
        Stack<Node> laserRoute = findLaserRoute(from, to);
        if (laserRoute.isEmpty()) {
            return false;
        } else {
            while (laserRoute.size() > 1) {
                Node node = laserRoute.pop();
                attack(map[node.y][node.x], from.attackScore / 2);
            }
            attack(to, from.attackScore);
            return true;
        }
    }

    private static Stack<Node> findLaserRoute(Cannon from, Cannon to) {
        Queue<Node> nodeQueue = new LinkedList<>();
        Stack<Node> nodeStack = new Stack<>();
        boolean[][] visited = new boolean[N + 1][M + 1];
        nodeQueue.offer(new Node(from.y, from.x));
        visited[from.y][from.x] = true;
        while (!nodeQueue.isEmpty() && !visited[to.y][to.x]) {
            Node node = nodeQueue.poll();
            for (int i = 0; i < 4 && !visited[to.y][to.x]; i++) {
                int ny = getCoord(node.y + laserDy[i], N);
                int nx = getCoord(node.x + laserDx[i], M);
                if (!visited[ny][nx] && map[ny][nx].attackScore > 0) {
                    visited[ny][nx] = true;
                    nodeQueue.offer(new Node(ny, nx));
                    nodeStack.push(new Node(ny, nx, node));
                }
            }
        }

        Stack<Node> laserRoute = new Stack<>();
        if (visited[to.y][to.x]) {
            Node targetNode = new Node(to.y, to.x);
            while (!nodeStack.isEmpty()) {
                Node node = nodeStack.pop();
                if (node.y == targetNode.y && node.x == targetNode.x) {
                    laserRoute.push(node);
                    targetNode = node.prevNode;
                }
            }
        }
        return laserRoute;
    }

    private static void bombAttack(Cannon from, Cannon to) {
        for (int i = 0; i < 8; i++) {
            int ny = getCoord(to.y + bombDy[i], N);
            int nx = getCoord(to.x + bombDx[i], M);
            if ((from.y != ny || from.x != nx)) {
                attack(map[ny][nx], from.attackScore / 2);
            }
        }
        attack(to, from.attackScore);
    }

    private static void fixCannonAndInitIsRelevantToAttack() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if (map[i][j].attackScore > 0) {
                    if (!map[i][j].isRelevantToAttack) {
                        map[i][j].attackScore++;
                    } else {
                        map[i][j].isRelevantToAttack = false;
                    }
                }
            }
        }
    }

    private static void attack(Cannon target, int damage) {
        if (map[target.y][target.x].attackScore > 0) {
            target.attackScore = Math.max(target.attackScore - damage, 0);
            target.isRelevantToAttack = true;
            if (target.attackScore == 0) {
                cannonCount--;
            }
        }
    }

    private static int getCoord(int coord, int mod) {
        if (coord > mod) {
            coord -= mod;
        } else if (coord <= 0) {
            coord += mod;
        }
        return coord;
    }

    private static void attackScoreUp(Cannon minCannon, int attackTurn) {
        minCannon.attackScore += N + M;
        minCannon.isRelevantToAttack = true;
        minCannon.attackTurn = attackTurn;
    }

    private static class Node {
        int y;
        int x;
        Node prevNode;

        public Node(int y, int x) {
            this.y = y;
            this.x = x;
        }

        public Node(int y, int x, Node prevNode) {
            this.y = y;
            this.x = x;
            this.prevNode = prevNode;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "y=" + y +
                    ", x=" + x +
                    ", prevNode=" + prevNode +
                    '}';
        }
    }

    private static class Cannon extends Node {
        int attackScore;
        int attackTurn;
        boolean isRelevantToAttack = false;

        public Cannon(int y, int x, int attackScore) {
            super(y, x);
            this.attackScore = attackScore;
        }

        @Override
        public String toString() {
            return "Cannon{" +
                    "attackScore=" + attackScore +
                    ", attackTurn=" + attackTurn +
                    ", y=" + y +
                    ", x=" + x +
                    '}';
        }
    }

    private static void printMap() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                System.out.print(map[i][j].attackScore + "\t");
//                System.out.print(String.format("%04d", map[i][j].attackScore) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}