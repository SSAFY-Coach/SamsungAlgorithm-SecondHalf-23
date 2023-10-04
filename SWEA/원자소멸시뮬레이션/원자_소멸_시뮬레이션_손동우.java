import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Solution {
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static int T;
    private static int N;
    private static List<Atom> atomList = new ArrayList<>();
    private static List<AtomPair> atomPairList = new LinkedList<>();

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
        N = Integer.parseInt(br.readLine());
        atomList.clear();
        atomPairList.clear();
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());
            int energy = Integer.parseInt(st.nextToken());
            atomList.add(new Atom(y, x, direction, energy));
        }
    }

    private static int solution() {
        //같은 행 혹은 열에 있고 만나는 원자 탐색
        atomPairList.addAll(findAcrossPair());
        //타이밍이 맞아서 만나는 원자 탐색(두 원자가 정사각형의 대각선 끝 점)
        atomPairList.addAll(findSquarePair());
        //시간 순으로 정렬해서 파괴되는 원자 파괴
        Collections.sort(atomPairList);

        int index = 0;
        double curTime;
        int answer = 0;
        while (index < atomPairList.size()) {
            AtomPair atomPair = atomPairList.get(index);
            curTime = atomPair.time;
            Set<Atom> atomSet = new HashSet<>();
            while (index < atomPairList.size() && Double.compare(atomPairList.get(index).time, curTime) == 0) {
                atomPair = atomPairList.get(index);
                if (!atomPair.atom1.isDestroyed && !atomPair.atom2.isDestroyed) {
                    atomSet.add(atomPair.atom1);
                    atomSet.add(atomPair.atom2);
                }
                index++;
            }
            for (Atom atom : atomSet) {
                atom.isDestroyed = true;
                answer += atom.energy;
            }
        }
        return answer;
    }

    private static List<AtomPair> findAcrossPair() {
        List<AtomPair> tempAtomPairList = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            Atom atom1 = atomList.get(i);
            for (int j = i + 1; j < N; j++) {
                Atom atom2 = atomList.get(j);
                double time = getAcrossTime(atom1, atom2);
                if (Double.compare(time, 0) != 0) {
                    tempAtomPairList.add(new AtomPair(atom1, atom2, time));
                }
            }
        }
        return tempAtomPairList;
    }

    private static double getAcrossTime(Atom atom1, Atom atom2) {
        double time = 0;
        if (atom1.direction == UP || atom1.direction == DOWN) {
            if (atom1.x == atom2.x) {
                if (atom1.direction == UP) {
                    if (atom1.y < atom2.y && atom2.direction == DOWN) {
                        time = (atom2.y - atom1.y) / 2d;
                    }
                } else {
                    if (atom1.y > atom2.y && atom2.direction == UP) {
                        time = (atom1.y - atom2.y) / 2d;
                    }
                }
            }
        } else {
            if (atom1.y == atom2.y) {
                if (atom1.direction == LEFT) {
                    if (atom1.x > atom2.x && atom2.direction == RIGHT) {
                        time = (atom1.x - atom2.x) / 2d;
                    }
                } else {
                    if (atom1.x < atom2.x && atom2.direction == LEFT) {
                        time = (atom2.x - atom1.x) / 2d;
                    }
                }
            }
        }
        return time;
    }

    private static List<AtomPair> findSquarePair() {
        List<AtomPair> tempAtomPairList = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            Atom atom1 = atomList.get(i);
            for (int j = i + 1; j < N; j++) {
                Atom atom2 = atomList.get(j);
                double time = getSquareTime(atom1, atom2);
                if (Double.compare(time, 0) != 0) {
                    tempAtomPairList.add(new AtomPair(atom1, atom2, time));
                }
            }
        }
        return tempAtomPairList;
    }

    private static double getSquareTime(Atom atom1, Atom atom2) {
        boolean isSucceed = false;
        if (Math.abs(atom1.y - atom2.y) == Math.abs(atom1.x - atom2.x)) {
            if ((atom1.direction == UP && atom1.y < atom2.y) || (atom1.direction == DOWN && atom1.y > atom2.y)) {
                if ((atom2.direction == LEFT && atom1.x < atom2.x) || (atom2.direction == RIGHT && atom1.x > atom2.x)) {
                    isSucceed = true;
                }
            } else if ((atom1.direction == LEFT && atom1.x > atom2.x) || (atom1.direction == RIGHT && atom1.x < atom2.x)) {
                if ((atom2.direction == UP && atom1.y > atom2.y) || (atom2.direction == DOWN && atom1.y < atom2.y)) {
                    isSucceed = true;
                }
            }
        }
        return isSucceed ? Math.abs(atom1.y - atom2.y) : 0d;
    }

    private static class Atom {
        int y;
        int x;
        int direction;
        int energy;
        boolean isDestroyed = false;

        public Atom(int y, int x, int direction, int energy) {
            this.y = y;
            this.x = x;
            this.direction = direction;
            this.energy = energy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Atom atom = (Atom) o;
            return y == atom.y && x == atom.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x);
        }

        @Override
        public String toString() {
            return "Atom{" +
                    "y=" + y +
                    ", x=" + x +
                    ", direction=" + direction +
                    ", energy=" + energy +
                    ", isDestroyed=" + isDestroyed +
                    '}';
        }
    }

    private static class AtomPair implements Comparable<AtomPair> {
        Atom atom1;
        Atom atom2;
        double time;

        public AtomPair(Atom atom1, Atom atom2, double time) {
            this.atom1 = atom1;
            this.atom2 = atom2;
            this.time = time;
        }

        @Override
        public int compareTo(AtomPair atomPair) {
            return Double.compare(this.time, atomPair.time);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AtomPair atomPair = (AtomPair) o;
            return Double.compare(atomPair.time, time) == 0 && atom1.equals(atomPair.atom1) && atom2.equals(atomPair.atom2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(atom1, atom2, time);
        }

        @Override
        public String toString() {
            return "AtomPair{" +
                    "atom1=" + atom1 +
                    ", atom2=" + atom2 +
                    ", time=" + time +
                    '}';
        }
    }
}