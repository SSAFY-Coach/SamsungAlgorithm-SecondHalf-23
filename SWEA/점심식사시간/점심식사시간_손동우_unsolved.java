import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    private static int T;
    private static int N;
    private static List<Person> peopleList = new ArrayList<>();
    private static Steps steps1;
    private static Steps steps2;
    private static final Comparator<Person> personComparator = Comparator.comparingInt(person -> person.arrivalTime);
    private static PriorityQueue<Person> stepsPq1 = new PriorityQueue<>(personComparator);
    private static PriorityQueue<Person> stepsPq2 = new PriorityQueue<>(personComparator);
    private static int[][] moveTimes;
    /*
    moveTimes의 행은 1번 계단으로 들어가는 사람의 수, 열은 2번 계단으로 들어가는 사람의 수
     */

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
        peopleList.clear();
        steps1 = null;
        steps2 = null;
        N = Integer.parseInt(br.readLine());
        StringTokenizer st;
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                int num = Integer.parseInt(st.nextToken());
                if (num == 1) {
                    peopleList.add(new Person(i, j));
                } else if (num > 1) {
                    if (steps1 == null) {
                        steps1 = new Steps(i, j, num);
                    } else {
                        steps2 = new Steps(i, j, num);
                    }
                }
            }
        }
        moveTimes = new int[peopleList.size() + 1][peopleList.size() + 1];
    }

    private static int solution() {
        for (int i = 0; i <= peopleList.size(); i++) {
            putAllPeopleInStepsPq(i);
            int moveTime1 = calculateMoveTime(stepsPq1, steps1.length);
            int moveTime2 = calculateMoveTime(stepsPq2, steps2.length);
            moveTimes[i][peopleList.size() - i] = Math.max(moveTime1, moveTime2);
        }
        int answer = Integer.MAX_VALUE;
        for (int i = 0; i <= peopleList.size(); i++) {
            for (int j = 0; j <= peopleList.size(); j++) {
                if (moveTimes[i][j] > 0) {
                    answer = Math.min(answer, moveTimes[i][j]);
                }
            }
        }
        return answer;
    }

    private static void putAllPeopleInStepsPq(int numberOfPeopleInSteps1) {
        peopleList.sort(Comparator.comparingInt(person -> getDistanceToSteps(person, steps1)));
        stepsPq1.addAll(peopleList.subList(0, numberOfPeopleInSteps1).stream()
                .peek(person -> {
                    person.arrivalTime = getDistanceToSteps(person, steps1);
                    person.startMovingTime = 0;
                })
                .collect(Collectors.toList()));
        stepsPq2.addAll(peopleList.subList(numberOfPeopleInSteps1, peopleList.size()).stream()
                .peek(person -> {
                    person.arrivalTime = getDistanceToSteps(person, steps2);
                    person.startMovingTime = 0;
                })
                .collect(Collectors.toList()));
    }

    private static int calculateMoveTime(PriorityQueue<Person> stepsPq, int stepsLength) {
        Queue<Person> movingQueue = new LinkedList<>();
        Queue<Person> waitingQueue = new LinkedList<>();
        int currentTime = 0;
        while (!stepsPq.isEmpty() || !movingQueue.isEmpty() || !waitingQueue.isEmpty()) {
            currentTime++;

            while (!movingQueue.isEmpty() && movingQueue.peek().startMovingTime + stepsLength <= currentTime) {
                movingQueue.poll();
            }
            while (!waitingQueue.isEmpty() && movingQueue.size() < 3) {
                Person movingPerson = waitingQueue.poll();
                movingPerson.startMovingTime = currentTime;
                movingQueue.offer(movingPerson);
            }
            while (!stepsPq.isEmpty() && stepsPq.peek().arrivalTime + 1 <= currentTime) {
                Person arrivedPerson = stepsPq.poll();
                if (movingQueue.size() < 3) {
                    arrivedPerson.startMovingTime = arrivedPerson.arrivalTime + 1;
                    movingQueue.offer(arrivedPerson);
                } else {
                    waitingQueue.offer(arrivedPerson);
                }
            }
        }
        return currentTime;
    }

    private static int getDistanceToSteps(Person person, Steps steps) {
        return Math.abs(person.y - steps.y) + Math.abs(person.x - steps.x);
    }

    private static class Person {
        int y;
        int x;
        int arrivalTime;
        int startMovingTime;

        public Person(int y, int x) {
            this.y = y;
            this.x = x;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "y=" + y +
                    ", x=" + x +
                    ", arrivalTime=" + arrivalTime +
                    ", startMovingTime=" + startMovingTime +
                    '}';
        }
    }

    private static class Steps {
        int y;
        int x;
        int length;

        public Steps(int y, int x, int length) {
            this.y = y;
            this.x = x;
            this.length = length;
        }

        @Override
        public String toString() {
            return "Steps{" +
                    "y=" + y +
                    ", x=" + x +
                    ", length=" + length +
                    '}';
        }
    }
}