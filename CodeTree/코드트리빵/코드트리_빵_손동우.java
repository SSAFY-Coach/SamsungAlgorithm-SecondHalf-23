import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    private static final int[] dy = {-1, 0, 0, 1};
    private static final int[] dx = {0, -1, 1, 0};
    private static final int BASECAMP = 1;
    private static final int CONV = 2;
    private static int N, M;
    private static int[][] map;
    private static List<Person> people = new LinkedList<>();

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
        map = new int[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            map[y][x] = CONV;
            people.add(new Person(new Node(y, x)));
        }
    }

    private static int solution() {
        int time = 0;
        while (!isAllArrived()) {
            for (Person person : people) {
                if (!person.isArrived) {
                    movePerson(person);
                }
            }
            if (time < M) {
                goToBasecamp(time);
            }
            time++;
        }
        return time;
    }

    private static void movePerson(Person person) {
        int size = person.nodeQueue.size();
        for (int i = 0; i < size && !person.isArrived; i++) {
            Node node = person.nodeQueue.poll();
            for (int d = 0; d < 4 && !person.isArrived; d++) {
                int ny = node.y + dy[d];
                int nx = node.x + dx[d];
                if (isInMap(ny, nx) && !person.visited[ny][nx] && (map[ny][nx] == 0 || map[ny][nx] == BASECAMP || map[ny][nx] == CONV)) {
                    person.visited[ny][nx] = true;
                    if (isPersonArrived(person.conv, ny, nx)) {
                        person.isArrived = true;
                        map[ny][nx] = -CONV;
                    } else {
                        person.nodeQueue.offer(new Node(ny, nx));
                    }
                }
            }
        }
    }

    private static void goToBasecamp(int time) {
        Person person = people.get(time);
        Node closestBasecamp = findClosestBasecamp(person.conv);
        person.nodeQueue.offer(closestBasecamp);
        person.visited[closestBasecamp.y][closestBasecamp.x] = true;
        map[closestBasecamp.y][closestBasecamp.x] = -BASECAMP;
    }

    private static Node findClosestBasecamp(Node conv) {
        Node closestBasecamp = null;
        Queue<Node> nodeQueue = new LinkedList<>();
        boolean[][] visited = new boolean[N + 1][N + 1];
        nodeQueue.offer(conv);
        visited[conv.y][conv.x] = true;
        while (!nodeQueue.isEmpty() && closestBasecamp == null) {
            Node node = nodeQueue.poll();
            for (int d = 0; d < 4 && closestBasecamp == null; d++) {
                int ny = node.y + dy[d];
                int nx = node.x + dx[d];
                if (isInMap(ny, nx) && !visited[ny][nx] && (map[ny][nx] == 0 || map[ny][nx] == CONV || map[ny][nx] == BASECAMP)) {
                    visited[ny][nx] = true;
                    if (map[ny][nx] == BASECAMP) {
                        closestBasecamp = new Node(ny, nx);
                    }
                    nodeQueue.offer(new Node(ny, nx));
                }
            }
        }
        return closestBasecamp;
    }

    private static boolean isPersonArrived(Node conv, int personY, int personX) {
        return conv.y == personY && conv.x == personX;
    }

    private static boolean isAllArrived() {
        return people.stream().allMatch(person -> person.isArrived);
    }

    private static boolean isInMap(int y, int x) {
        return 0 < y && y <= N && 0 < x && x <= N;
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

    private static class Person {
        Node basecamp;
        Node conv;
        boolean isArrived;
        boolean[][] visited = new boolean[N + 1][N + 1];
        Queue<Node> nodeQueue = new LinkedList<>();

        public Person(Node conv) {
            this.conv = conv;
        }
    }

    private static void printMap() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (map[i][j] >= 0)
                    System.out.print(" ");
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}