import java.io.*;
import java.util.*;

public class Solution {

    public static int T;                // 테스트케이스 (T <= 50)
    public static int S = 2;            // 계단 개수 (항상 2개 고정)
    public static int MAX = 3;          // 계단에 올라갈 수 있는 사람 최대 수
    public static int P;                // 사람 수
    public static int MIN;              // 정답값 : 최소 이동완료시간
    public static Step[] STEP;          // 계단 정보
    public static List<Point> PERSON;   // 사람 위치
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuffer sb = new StringBuffer();
        T = Integer.parseInt(br.readLine());

        for (int t = 1;t <= T;t++) {
            sb.append("#").append(t).append(" ");
            init(br);
            checkCases();
            sb.append(MIN);
            if (t < T) sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    public static void checkCases() {
        for (int number = 0;number < (1 << P);number++) {
            StringBuffer sb = new StringBuffer();
            String bit = Integer.toBinaryString(number);
            for (int add = 0;add < P - bit.length();add++)
                sb.append("0");
            sb.append(bit);
            checkEachCase(sb.toString().toCharArray());
        }
    }

    public static void checkEachCase(char[] selectedData) {
        for (int s = 0;s < S;s++)
            STEP[s] = new Step(STEP[s].point, STEP[s].depth);

        // 선택사항에 대한 Step 세팅
        for (int s = 0; s < selectedData.length;s++) {
            int idx = selectedData[s] - '0';
            STEP[idx].addPerson(PERSON.get(s));
        }

        // 우선순위 세팅하기
        for (int s = 0;s < S;s++)
            STEP[s].donePerson();

        // 단계마다 확인하기
        int COUNT = 0;
        while (!isDone()) {

            COUNT++;
            if (COUNT >= MIN) return; // 지금까지 구한 최소보다 커지면, 더 검사하지 않음

            // 계단에 내려가고 있는 사람 먼저 정리하기
            for (int s = 0; s < S;s++)
                STEP[s].doDown(COUNT);

            // 적절한 사람 움직이기
            for (int s = 0;s < S;s++)
                STEP[s].checkDown(COUNT);
        }

        MIN = Math.min(MIN, COUNT);
    }

    public static boolean isDone() {
        for (int s = 0;s < S;s++) {
            if (!STEP[s].nowDown.isEmpty()) return false;
            if (!STEP[s].readyPerson.isEmpty()) return false;
        }
        return true;
    }

    public static void init(BufferedReader br) throws Exception {
        MIN = Integer.MAX_VALUE;
        PERSON = new ArrayList<>();
        STEP = new Step[S];
        int N = Integer.parseInt(br.readLine());

        for (int n = 0;n < N;n++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int m = 0;m < N;m++) {
                int num = Integer.parseInt(st.nextToken());

                if (num == 0) continue;
                Point point = new Point(n, m);

                if (num == 1) {
                    // 사람 정보
                    PERSON.add(point);
                } else {
                    // 계단 정보
                    if (STEP[0] == null) STEP[0] = new Step(point, num);
                    else STEP[1] = new Step(point, num);
                }
            }
        }

        P = PERSON.size();
    }

    public static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(x=" + x + ", y=" + y + ')';
        }
    }

    public static class Person implements Comparable<Person> {
        Point point;
        int toStepTime;

        public Person(Point point, int toStepTime) {
            this.point = point;
            this.toStepTime = toStepTime;
        }

        @Override
        public int compareTo(Person o) {
            return this.toStepTime - o.toStepTime;
        }

        @Override
        public String toString() {
            return "[" +
                    "point=" + point +
                    ", toStepTime=" + toStepTime +
                    ']';
        }
    }

    public static class Step {
        Point point;
        int depth;
        Queue<Person> nowDown, readyPerson;
        List<Person> selected;

        public Step(Point point, int depth) {
            this.point = point;
            this.depth = depth;
            this.nowDown = new LinkedList<>();
            this.readyPerson = new LinkedList<>();
            this.selected = new ArrayList<>();
        }

        public void addPerson(Point point) {
            Person person = new Person(point, getLength(point));
            selected.add(person);
        }

        public int getLength(Point point) {
            return Math.abs(this.point.x - point.x) + Math.abs(this.point.y - point.y);
        }

        public void donePerson() {
            Collections.sort(selected);
            for (int i = 0;i < selected.size();i++)
                readyPerson.add(selected.get(i));
        }

        public void doDown(int nowTime) {
            int SIZE = nowDown.size();

            for (int size = 0;size < SIZE;size++) {
                Person person = nowDown.poll();
                if (person.toStepTime + depth + 1 > nowTime) nowDown.offer(person);
            }
        }

        public void checkDown(int nowTime) {
            if (!readyPerson.isEmpty() && readyPerson.peek().toStepTime > nowTime) return;

            while (nowDown.size() < MAX
                    && !readyPerson.isEmpty()
                    && readyPerson.peek().toStepTime <= nowTime) {
                nowDown.offer(readyPerson.poll());
            }
        }
    }
}

/**
 * 
<현재 결과 : 1, 9번 테케 오류>

#1 8
#2 8
#3 9
#4 7
#5 8
#6 8
#7 11
#8 11
#9 16
#10 12

 */