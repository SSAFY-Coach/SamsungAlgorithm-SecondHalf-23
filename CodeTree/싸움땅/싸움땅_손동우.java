import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    //delta array : 상 우 하 좌
    private static int[] dy = {-1, 0, 1, 0};
    private static int[] dx = {0, 1, 0, -1};
    private static int N, M, K;
    private static List<Node>[][] map;
    private static List<Node> playerList = new ArrayList<>();


    public static void main(String[] args) throws Exception {
        init();
        int[] answer = solution();
        for (int i = 0; i < answer.length; i++) {
            System.out.print(answer[i] + " ");
        }
        System.out.println();
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

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                int gun = Integer.parseInt(st.nextToken());
                if (gun > 0) {
                    map[i][j].add(new Node(i, j, -1, -1, gun));
                }
            }
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());
            int stat = Integer.parseInt(st.nextToken());
            Node node = new Node(y, x, direction, stat);
            map[y][x].add(node);
            playerList.add(node);
        }
    }

    private static int[] solution() {
        for (int i = 0; i < K; i++) {
            play();
        }
        return playerList.stream()
                .mapToInt(player -> player.point)
                .toArray();
    }

    private static void play() {
        for (Node player : playerList) {
            map[player.y][player.x].remove(player);

            int ny = player.y + dy[player.direction];
            int nx = player.x + dx[player.direction];
            if (!isInMap(ny, nx)) {
                ny = getCoord(ny);
                nx = getCoord(nx);
                player.direction = (player.direction + 2) % 4;
            }
            player.y = ny;
            player.x = nx;

            map[player.y][player.x].add(player);
            step2(player);
        }
    }

    private static void step2(Node player) {
        List<Node> fightPlayerList = map[player.y][player.x].stream()
                .filter(Node::isPlayer)
                .collect(Collectors.toList());

        if (fightPlayerList.size() == 1) {
            getGun(player);
        } else {
            fight(fightPlayerList.get(0), fightPlayerList.get(1));
        }
    }

    private static void fight(Node player1, Node player2) {
        int player1Power = player1.stat + player1.gunPower;
        int player2Power = player2.stat + player2.gunPower;
        if (player1Power > player2Power) {
            lose(player2);
            win(player1, player1Power - player2Power);
        } else if (player1Power < player2Power) {
            lose(player1);
            win(player2, player2Power - player1Power);
        } else {
            if (player1.stat > player2.stat) {
                lose(player2);
                win(player1, 0);
            } else {
                lose(player1);
                win(player2, 0);
            }
        }
    }

    private static void lose(Node player) {
        putGunDown(player);
        moveLooser(player);
        getGun(player);
    }

    private static void putGunDown(Node player) {
        map[player.y][player.x].add(new Node(player.y, player.x, -1, -1, player.gunPower));
        player.gunPower = 0;
    }

    private static void moveLooser(Node player) {
        map[player.y][player.x].remove(player);
        int ny = player.y + dy[player.direction];
        int nx = player.x + dx[player.direction];
        while (!isInMap(ny, nx) || getPlayerCount(map[ny][nx]) != 0) {
            player.direction = (player.direction + 1) % 4;
            ny = player.y + dy[player.direction];
            nx = player.x + dx[player.direction];
        }
        player.y = ny;
        player.x = nx;
        map[player.y][player.x].add(player);
    }

    private static void win(Node player, int point) {
        getGun(player);
        player.point += point;
    }

    private static void getGun(Node player) {
        map[player.y][player.x].stream()
                .filter(Node::isGun)
                .max(Comparator.comparing(tempGun -> tempGun.gunPower))
                .ifPresent(gun -> {
                    if (player.gunPower < gun.gunPower) {
                        int tempGunPower = player.gunPower;
                        player.gunPower = gun.gunPower;
                        gun.gunPower = tempGunPower;
                    }
                });

    }

    private static int getPlayerCount(List<Node> nodeList) {
        return (int) nodeList.stream()
                .filter(Node::isPlayer)
                .count();
    }

    private static boolean isInMap(int y, int x) {
        return 0 < y && y <= N && 0 < x && x <= N;
    }

    private static int getCoord(int coord) {
        if (coord > N) {
            coord = N - 1;
        } else if (coord <= 0) {
            coord = 2;
        }
        return coord;
    }

    private static class Node {
        int y;
        int x;
        int direction;
        int stat;
        int gunPower;
        int point;

        public Node(int y, int x, int direction, int stat) {
            this.y = y;
            this.x = x;
            this.direction = direction;
            this.stat = stat;
        }

        public Node(int y, int x, int direction, int stat, int gunPower) {
            this.y = y;
            this.x = x;
            this.direction = direction;
            this.stat = stat;
            this.gunPower = gunPower;
        }

        public boolean isGun() {
            return direction == -1 && stat == -1;
        }

        public boolean isPlayer() {
            return direction != -1 && stat != -1;
        }
    }
}