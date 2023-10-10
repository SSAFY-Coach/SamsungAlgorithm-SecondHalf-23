import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    private static final int[] dy = {0, 1, 0, -1};
    private static final int[] dx = {1, 0, -1, 0};
    private static final int[] mazeDy = {0, 1, 0, -1};
    private static final int[] mazeDx = {-1, 0, 1, 0};
    private static int N, M;
    private static int[][] map;
    private static int[][] directionMap;
    private static Attack[] attacks;
    private static Node tower;

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
        directionMap = new int[N][N];
        attacks = new Attack[M];
        tower = new Node(N / 2, N / 2);
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int direction = Integer.parseInt(st.nextToken());
            int distance = Integer.parseInt(st.nextToken());
            attacks[i] = new Attack(direction, distance);
        }
        // 각 칸마다 방향은 언제나 일정하니까 처음 시작할 때 방향을 잡아주고 시작
        initDirectionMap();
    }

    private static void initDirectionMap() {
        int curSize = 0;
        int size = 1;
        int sizeCount = 2;
        int y = tower.y;
        int x = tower.x;
        int direction = 0;
        while (true) {
            directionMap[y][x] = direction;
            y = y + mazeDy[direction];
            x = x + mazeDx[direction];
            if (isInMap(y, x)) {
                if (++curSize == size) {
                    curSize = 0;
                    direction = (direction + 1) % 4;
                    if (--sizeCount == 0) {
                        sizeCount = 2;
                        size++;
                    }
                }
            } else {
                break;
            }
        }
    }

    private static int solution() {
        int score = 0;
        for (Attack attack : attacks) {
            // 공격
            score += doAttack(attack);
            // 빈 공간 당겨오기
            shiftMaze();
            // 4개이상 반복하는 숫자 지우기
            while (true) {
                // 반복 숫자 지우기
                int addScore = removeRepeat();
                if (addScore == 0) {
                    break;
                } else {
                    score += addScore;
                }
                // 빈 공간 당겨오기
                shiftMaze();
            }
            // 숫자 세서 (총 개수, 숫자의 크기) 리스트로 받기
            List<Integer> countNumberList = countRepeatNumbers();
            // 리스트는 항상 현재 맵에 남아있는 것보다 많으니까 초기화할 필요없음
            // 리스트를 미로에 채우기
            setMaze(countNumberList);
        }
        return score;
    }

    private static int doAttack(Attack attack) {
        int score = 0;
        for (int i = 1; i <= attack.distance; i++) {
            int ny = tower.y + dy[attack.direction] * i;
            int nx = tower.x + dx[attack.direction] * i;
            if (isInMap(ny, nx)) {
                score += map[ny][nx];
                map[ny][nx] = 0;
            }
        }
        return score;
    }

    private static int removeRepeat() {
        int score = 0;
        int y = tower.y + mazeDy[directionMap[tower.y][tower.x]];
        int x = tower.x + mazeDx[directionMap[tower.y][tower.x]];
        while (true) {
            if (isInMap(y, x)) {
                if (map[y][x] != 0) {
                    int count = countNumber(y, x);
                    for (int i = 0; i < count; i++) {
                        if (count >= 4) {
                            score += map[y][x];
                            map[y][x] = 0;
                        }
                        int direction = directionMap[y][x];
                        y = y + mazeDy[direction];
                        x = x + mazeDx[direction];
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return score;
    }

    private static List<Integer> countRepeatNumbers() {
        List<Integer> countNumberList = new ArrayList<>();
        int y = tower.y + mazeDy[directionMap[tower.y][tower.x]];
        int x = tower.x + mazeDx[directionMap[tower.y][tower.x]];
        while (isInMap(y, x) && map[y][x] != 0) {
            int count = countNumber(y, x);
            countNumberList.add(count);
            countNumberList.add(map[y][x]);
            for (int i = 0; i < count; i++) {
                int direction = directionMap[y][x];
                y = y + mazeDy[direction];
                x = x + mazeDx[direction];
            }
        }
        return countNumberList;
    }

    private static void setMaze(List<Integer> countNumberList) {
        int y = tower.y + mazeDy[directionMap[tower.y][tower.x]];
        int x = tower.x + mazeDx[directionMap[tower.y][tower.x]];
        int index = 0;
        while (isInMap(y, x) && index < countNumberList.size()) {
            map[y][x] = countNumberList.get(index);
            index++;
            int direction = directionMap[y][x];
            y = y + mazeDy[direction];
            x = x + mazeDx[direction];
        }
    }


    private static int countNumber(int y, int x) {
        int count = 0;
        int number = map[y][x];
        while (isInMap(y, x) && map[y][x] == number) {
            count++;
            int direction = directionMap[y][x];
            y = y + mazeDy[direction];
            x = x + mazeDx[direction];
        }
        return count;
    }

    private static void shiftMaze() {
        Node emptyNode = tower;
        while (true) {
            // 다음 노드부터 0인 노드 찾기
            emptyNode = findNextNode(emptyNode, true);
            if (emptyNode != null) {
                // 다음 노드부터 0이 아닌 노드 찾기
                Node nextNode = findNextNode(emptyNode, false);
                if (nextNode != null) {
                    map[emptyNode.y][emptyNode.x] = map[nextNode.y][nextNode.x];
                    map[nextNode.y][nextNode.x] = 0;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private static Node findNextNode(Node node, boolean isTargetZero) {
        int y = node.y;
        int x = node.x;
        while (true) {
            int direction = directionMap[y][x];
            y = y + mazeDy[direction];
            x = x + mazeDx[direction];
            if (isInMap(y, x)) {
                if (isTargetZero == (map[y][x] == 0)) {
                    return new Node(y, x);
                }
            } else {
                break;
            }
        }
        return null;
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

    private static class Attack {
        int direction;
        int distance;

        public Attack(int direction, int distance) {
            this.direction = direction;
            this.distance = distance;
        }
    }
}