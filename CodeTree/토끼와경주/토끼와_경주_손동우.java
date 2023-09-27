import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

public class Main {
    private static final int[] dy = {-1, 1, 0, 0};
    private static final int[] dx = {0, 0, -1, 1};
    private static final Comparator<Rabbit> raceRabbitComparator = (rabbit1, rabbit2) -> {
        if (rabbit1.jumpCount != rabbit2.jumpCount) {
            return rabbit1.jumpCount - rabbit2.jumpCount;
        } else if (rabbit1.y + rabbit1.x != rabbit2.y + rabbit2.x) {
            return (rabbit1.y + rabbit1.x) - (rabbit2.y + rabbit2.x);
        } else if (rabbit1.y != rabbit2.y) {
            return rabbit1.y - rabbit2.y;
        } else if (rabbit1.x != rabbit2.x) {
            return rabbit1.x - rabbit2.x;
        } else {
            return rabbit1.id - rabbit2.id;
        }
    };

    private static final Comparator<Node> nodeComparator = (node1, node2) -> {
        if (node1.y + node1.x != node2.y + node2.x) {
            return (node1.y + node1.x) - (node2.y + node2.x);
        } else if (node1.y != node2.y) {
            return node1.y - node2.y;
        } else {
            return node1.x - node2.x;
        }
    };

    private static final Comparator<Rabbit> scoreRabbitComparator = (rabbit1, rabbit2) -> {
        if (rabbit1.y + rabbit1.x != rabbit2.y + rabbit2.x) {
            return (rabbit1.y + rabbit1.x) - (rabbit2.y + rabbit2.x);
        } else if (rabbit1.y != rabbit2.y) {
            return rabbit1.y - rabbit2.y;
        } else if (rabbit1.x != rabbit2.x) {
            return rabbit1.x - rabbit2.x;
        } else {
            return rabbit1.id - rabbit2.id;
        }
    };
    private static int Q;
    private static int N, M, P;
    private static Rabbit[] rabbits;
    private static Order[] orders;

    public static void main(String[] args) throws Exception {
        init();
        long answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Q = Integer.parseInt(br.readLine());
        StringTokenizer st = new StringTokenizer(br.readLine());
        st.nextToken();
        initRace(st);
        orders = new Order[Q - 2];
        for (int i = 0; i < Q - 2; i++) {
            st = new StringTokenizer(br.readLine());
            int orderNumber = Integer.parseInt(st.nextToken());
            if (orderNumber == Order.RACE_ORDER) {
                int number = Integer.parseInt(st.nextToken());
                int score = Integer.parseInt(st.nextToken());
                orders[i] = new RaceOrder(orderNumber, number, score);
            } else if (orderNumber == Order.DISTANCE_ORDER) {
                int id = Integer.parseInt(st.nextToken());
                int multiple = Integer.parseInt(st.nextToken());
                orders[i] = new DistanceOrder(orderNumber, id, multiple);
            }
        }
    }

    private static void initRace(StringTokenizer st) {
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        rabbits = new Rabbit[P];
        for (int i = 0; i < P; i++) {
            int id = Integer.parseInt(st.nextToken());
            int distance = Integer.parseInt(st.nextToken());
            rabbits[i] = new Rabbit(id, distance);
        }
    }

    private static long solution() {
        for (Order order : orders) {
            if (order instanceof RaceOrder) {
                RaceOrder raceOrder = (RaceOrder) order;
                doRace(raceOrder);
            } else if (order instanceof DistanceOrder) {
                DistanceOrder distanceOrder = (DistanceOrder) order;
                changeDistance(distanceOrder);
            }
        }
        return Arrays.stream(rabbits)
                .mapToLong(rabbit -> rabbit.score)
                .max()
                .getAsLong();
    }

    private static void doRace(RaceOrder order) {
        for (Rabbit rabbit : rabbits) {
            rabbit.isPicked = false;
        }

        for (int i = 0; i < order.number; i++) {
            Rabbit rabbit = Arrays.stream(rabbits).min(raceRabbitComparator).get();
            rabbit.isPicked = true;
            rabbit.jumpCount++;

            Node nextNode = findNextNode(rabbit);
            rabbit.y = nextNode.y;
            rabbit.x = nextNode.x;
            scoreUpOtherRabbits(rabbit.id, nextNode.y + nextNode.x + 2);
        }
        scoreUpMaxRabbit(order.score);
    }

    private static Node findNextNode(Rabbit rabbit) {
        Node[] nextNodes = new Node[4];
        for (int j = 0; j < 4; j++) {
            int ny = getCoord(rabbit.y, dy[j] * rabbit.distance, N);
            int nx = getCoord(rabbit.x, dx[j] * rabbit.distance, M);
            nextNodes[j] = new Node(ny, nx);
        }
        return Arrays.stream(nextNodes)
                .max(nodeComparator)
                .get();
    }

    private static void scoreUpOtherRabbits(int rabbitId, int score) {
        for (Rabbit otherRabbit : rabbits) {
            if (otherRabbit.id != rabbitId) {
                otherRabbit.score += score;
            }
        }
    }

    private static void scoreUpMaxRabbit(int score) {
        Rabbit maxRabbit = Arrays.stream(rabbits)
                .filter(rabbit -> rabbit.isPicked)
                .max(scoreRabbitComparator)
                .get();
        maxRabbit.score += score;
    }

    private static void changeDistance(DistanceOrder distanceOrder) {
        Rabbit rabbit = Arrays.stream(rabbits)
                .filter(tempRabbit -> tempRabbit.id == distanceOrder.id)
                .findFirst()
                .get();
        rabbit.distance *= distanceOrder.multiple;
    }

    private static int getCoord(int currentCoord, int distance, int mod) {
        mod--;
        if (distance < 0) {
            distance += currentCoord;
            distance = -distance;
            currentCoord = 0;
        } else if (currentCoord + distance >= mod) {
            distance -= (mod - currentCoord);
            if (distance > mod) {
                distance -= mod;
                currentCoord = 0;
            } else {
                return mod - distance;
            }
        }
        if ((distance / mod) % 2 == 1) {
            return mod - (distance % mod);
        } else {
            return (distance + currentCoord) % mod;
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

    private static class Rabbit extends Node {
        int id;
        int distance;
        int jumpCount = 0;
        long score = 0;
        boolean isPicked = false;

        public Rabbit(int id, int distance) {
            super(0, 0);
            this.id = id;
            this.distance = distance;
        }
    }

    private static abstract class Order {
        int orderNumber;
        static int RACE_ORDER = 200;
        static int DISTANCE_ORDER = 300;

        public Order(int orderNumber) {
            this.orderNumber = orderNumber;
        }
    }

    private static class RaceOrder extends Order {
        int number;
        int score;

        public RaceOrder(int orderNumber, int number, int score) {
            super(orderNumber);
            this.number = number;
            this.score = score;
        }
    }

    private static class DistanceOrder extends Order {
        int id;
        int multiple;

        public DistanceOrder(int orderNumber, int id, int multiple) {
            super(orderNumber);
            this.id = id;
            this.multiple = multiple;
        }
    }
}