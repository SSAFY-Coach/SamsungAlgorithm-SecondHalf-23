package SamsungAlgorithm_SecondHalf_23.SWEA.점심식사시간;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 1. 격자
 *      - N * N 크기 격자
 *      - P : 사람 위치, S : 계단 위치
 *
 *
 * 2. 이동 완료 시간 : 모든 사람들이 계단을 내려가 아래 층으로 이동을 완료한 시간.
 *      - 사람들이 아래층으로 이동하는 시간 = 계단 입구까지 이동 시간 + 계단을 내려가는 시간
 *
 *      2-1. 계단 입구까지 이동 시간
 *          - 사람이 현재 위치에서 계단 입구까지 이동하는데 걸리는 시간.
 *          - 이동 시간(분) = |PR - SR| + |PC - SC|  -> 사람위치, 계단위치의 맨하튼 거리
 *
 *      2-2. 계단을 내려가는 시간
 *          - 계단 입구에 도착한 후, 계단을 완전히 내려갈 때까지의 시간.
 *          - 계단 입구에 도착하면 1분 후 아래칸으로 내려갈 수 있다.
 *          - 계단 위에는 동시에 "최대 3명"까지만 올라가 있을 수 있다. -> 대기가 필요.
 *          - 이미 계단을 3명이 내려가고 있는 경우 그 중 한명이 계단을 완전히 내려갈 때까지 계단 입구에서 대기해야함.
 *          - 계단마다 길이 K 가 주어지며 계단에 올라간 후 완전히 내려가는데 K 분이 걸린다.
 *
 *
 * answer = 모든 사람이 계단을 내려가 이동이 완료되는 최소 시간을 찾아라.
 */
public class 점심_식사시간_서요셉 {

    static int N, ans;

    static List<Person> peopleList;
    static Stair[] stairs;
    static Queue<Person>[] stairQ;
    static boolean[] visited;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int T = Integer.parseInt(br.readLine());
        for (int t = 1; t <= T; t++) {
            N = Integer.parseInt(br.readLine());

            peopleList = new ArrayList<>();
            stairs = new Stair[2];
            stairQ = new LinkedList[2];
            for (int i = 0; i < 2; i++) {
                stairQ[i] = new LinkedList<>();
            }

            ans = Integer.MAX_VALUE;

            int stairIdx = 0;

            for (int i = 0; i < N; i++) {
                st = new StringTokenizer(br.readLine());
                for (int j = 0; j < N; j++) {
                    int num = Integer.parseInt(st.nextToken());

                    // 빈 칸이면 continue
                    if (num == 0) continue;
                    // 사람이면
                    else if (num == 1) peopleList.add(new Person(i, j));
                    // 계단이면
                    else {
                        stairs[stairIdx] = new Stair(i, j, num);
                        stairIdx++;
                    }
                }
            }

            movePerson(0);

            System.out.println("#" + t + " " + ans);
        }
    }

    /**
     * 모든 사람들을 대상으로 0, 1 계단으로 이동시켜보면서 최소 이동 완료 시간을 구한다.
     *
     * @param idx       : 사람의 인덱스
     */
    static void movePerson(int idx) {
        if (idx == peopleList.size()) {
            visited = new boolean[peopleList.size()];

            int moveCnt = goDown();

            ans = Math.min(ans, moveCnt);

            return;
        }

        // 0 계단 가보기
        peopleList.get(idx).stair = 0;
        peopleList.get(idx).getArriveStairTime();
        movePerson(idx + 1);

        // 1 계단 가보기
        peopleList.get(idx).stair = 1;
        peopleList.get(idx).getArriveStairTime();
        movePerson(idx + 1);
    }

    /**
     * @return          : 모든 사람들의 목적 계단이 정해지면 이동 시간을 구한다.
     */
    static int goDown() {
        int cnt = 0;
        int time = 1;

        while (true) {
            for (Queue<Person> q : stairQ) {
                int size = q.size();

                for (int i = 0; i < size; i++) {
                    Person p = q.poll();
                    Stair s = stairs[p.stair];

                    // 내려갈 시간이 됐다면 continue
                    if (p.departStairTime + s.k <= time) continue;

                    q.add(p);
                }
            }

            // 모든 사람이 계단에서 내려오면 time 반환
            if (cnt == peopleList.size() && stairQ[0].isEmpty() && stairQ[1].isEmpty()) return time;

            // 계단 대기 큐에 넣기
            // 각자 사람들의 목적 계단으로 가서 넣을 수 있도록 구현한다.
            for (int i = 0; i < peopleList.size(); i++) {
                if (visited[i]) continue;

                Person p = peopleList.get(i);
                Queue<Person> q = stairQ[p.stair];

                // 도착시간이 되었고 대기 인원이 만원이 아니면
                if (p.arriveStairTime + 1 <= time && q.size() < 3) {
                    p.departStairTime = time;
                    visited[i] = true;
                    q.add(p);
                    cnt++;
                }
            }

            time++;
        }

    }

    static class Person {
        int x, y, stair;
        int arriveStairTime;
        int departStairTime;

        public Person(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private void getArriveStairTime() {
            this.arriveStairTime = Math.abs(x - stairs[stair].x) + Math.abs(y - stairs[stair].y);
        }
    }

    static class Stair {
        int x, y, k;

        public Stair(int x, int y, int k) {
            this.x = x;
            this.y = y;
            this.k = k;
        }
    }
}
