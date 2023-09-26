import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

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
    private static List<Atom> atomList = new LinkedList<>();
    private static List<Atom> combinedAtomList = new LinkedList<>();

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
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int mass = Integer.parseInt(st.nextToken());
            int speed = Integer.parseInt(st.nextToken());
            int direction = directionMapper[Integer.parseInt(st.nextToken())];
            atomList.add(new Atom(y, x, mass, speed, direction));
        }
    }

    private static int solution() {
        int currentTime = 0;
        while (currentTime < K) {
            currentTime++;
//            atomList.forEach(System.out::println);
            moveAtoms();
//            System.out.println("=====after move=====");
//            atomList.forEach(System.out::println);
            while (true) {
                Node overlappedCoord = findOverlappedCoord();
//                System.out.println("overlappedCoord = " + overlappedCoord);
                if (overlappedCoord == null) {
                    break;
                }
                combineAtoms(overlappedCoord);
            }
            if (!combinedAtomList.isEmpty()) {
                atomList.addAll(combinedAtomList);
                combinedAtomList.clear();
            }
        }
        return atomList.stream()
                .mapToInt(atom -> atom.mass)
                .sum();
    }

    private static void moveAtoms() {
        atomList.forEach(atom -> {
            int ny = getCircularCoord(atom.y + dy[atom.direction] * atom.speed);
            int nx = getCircularCoord(atom.x + dx[atom.direction] * atom.speed);
            atom.y = ny;
            atom.x = nx;
        });
    }

    private static Node findOverlappedCoord() {
        for (Atom atom : atomList) {
            int overlappedCount = (int) atomList.stream()
                    .filter(atom2 -> atom.y == atom2.y && atom.x == atom2.x)
                    .count();
            if (overlappedCount >= 2) {
                return atom;
            }
        }
        return null;
    }

    private static void combineAtoms(Node overlappedCoord) {
        List<Atom> overlappedAtomList = atomList.stream()
                .filter(atom -> atom.y == overlappedCoord.y && atom.x == overlappedCoord.x)
                .collect(Collectors.toList());
        atomList.removeAll(overlappedAtomList);

        int newMass = overlappedAtomList.stream()
                .mapToInt(atom -> atom.mass)
                .sum() / 5;
        if (newMass > 0) {
            int newSpeed = overlappedAtomList.stream()
                    .mapToInt(atom -> atom.speed)
                    .sum() / overlappedAtomList.size();
            int[] newDirection = isAllSameKindaDirection(overlappedAtomList) ? new int[]{0, 1, 2, 3} : new int[]{4, 5, 6, 7};
            for (int i = 0; i < 4; i++) {
                combinedAtomList.add(new Atom(overlappedCoord.y, overlappedCoord.x, newMass, newSpeed, newDirection[i]));
            }
        }
    }

    private static boolean isAllSameKindaDirection(List<Atom> overlappedAtomList) {
        return overlappedAtomList.stream().allMatch(atom -> atom.direction < 4)
                || overlappedAtomList.stream().allMatch(atom -> atom.direction >= 4);
    }

    private static int getCircularCoord(int num) {
        if (num <= 0) {
            while (num <= 0) {
                num += N;
            }
            return num;
        } else if (num > N) {
            return num % N;
        } else {
            return num;
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

    private static class Atom extends Node {
        int mass;
        int speed;
        int direction;

        public Atom(int y, int x, int mass, int speed, int direction) {
            super(y, x);
            this.mass = mass;
            this.speed = speed;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "Atom{" +
                    "y=" + y +
                    ", x=" + x +
                    ", mass=" + mass +
                    ", speed=" + speed +
                    ", direction=" + direction +
                    '}';
        }
    }
}