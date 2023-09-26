package SamsungAlgorithm_SecondHalf_23.SWEA.특이한자석;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 자석
 *      - 4개의 자석이 놓여져있음
 *      - 각 자석은 각각 N(0), S(1) 극을 지니고 있는 8개의 날을 가지고 있다.
 *      - 1번 부터 4번 까지 판에 일렬로 배치되어 있다.
 *      - 빨간색 화살표 위치에 날 하나가 오도록 배치되어 있다.
 *
 * - 규칙
 *  1. 회전
 *      - 하나의 자석이 1칸 회전될때, 붙어 있는 자석은 서로 붙어있는 날의 자성과 "다를"(N - S || S - N) 때만 반대 방향으로 1칸 회전한다.
 *  2. 점수 계산
 *      - 1번 자석 : 빨간색 화살표 위치에 N 이면 0, S 이면 1
 *      - 2번 자석 : 빨간색 화살표 위치에 N 이면 0, S 이면 2
 *      - 3번 자석 : 빨간색 화살표 위치에 N 이면 0, S 이면 4
 *      - 4번 자석 : 빨간색 화살표 위치에 N 이면 0, S 이면 8
 *
 * - 진행
 *  1. 임의의 자석을 1칸씩 K 번 회전시킨다.
 *  2. 점수를 계산한다.
 *
 *
 * - answer : 4 개 자석의 자성 정보와 자석을 1 칸씩 K 번 회전 시킨 후에 획득하는 점수의 총 합을 구하라.
 */
public class 특이한_자석_서요셉 {

    static int K, score;
    static int[][] magnetics;
    static boolean[] match;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int T = Integer.parseInt(br.readLine());

        for (int t = 1; t <= T; t++) {

            K = Integer.parseInt(br.readLine());
            score = 0;

            // 자석 정보 입력
            magnetics = new int[4][8];

            for (int i = 0; i < 4; i++) {
                st = new StringTokenizer(br.readLine());
                for (int j = 0; j < 8; j++) {
                    magnetics[i][j] = Integer.parseInt(st.nextToken());
                }
            }

            // K 번 회전
            for (int i = 0; i < K; i++) {
                st = new StringTokenizer(br.readLine());

                int idx = Integer.parseInt(st.nextToken()) - 1;     // 회전 시킬 자석 번호
                int dir = Integer.parseInt(st.nextToken());     // 회전 방향 (1 : 시계방향, -1 : 반시계방향)

                match = new boolean[4];

                // 회전 하기 전 자성을 체크
                matchMagnetic(idx);

                rotate(idx, dir);

            }

            // 점수 구하기
            getScore();
            System.out.println("#" + t + " " + score);
        }
    }

    // 회전
    // 기준 바퀴의 방향대로 나머지 바퀴의 회전 방향이 주어진다.
    static void rotate(int idx, int dir) {
        for (int i = 0; i < 4; i++) {
            if (match[i]) {
                if (i == idx || i == ((idx + 2) % 4)) {
                    subRotate(i, dir);
                } else {
                    subRotate(i, -dir);
                }
            }
        }
    }

    static void subRotate(int idx, int dir) {
        int[] arr = magnetics[idx];

        // 시계
        if (dir == 1) {
            int tmp = arr[7];

            for (int i = arr.length - 1; i > 0; i--) {
                arr[i] = arr[i - 1];
            }

            arr[0] = tmp;
        }
        // 반시계
        else {
            int tmp = arr[0];

            for (int i = 0; i < arr.length - 1; i++) {
                arr[i] = arr[i + 1];
            }

            arr[7] = tmp;
        }
    }

    // 회전하기 전 자성 체크
    static void matchMagnetic(int idx) {
        // 이미 회전할 바퀴로 체크 되어 있으면 리턴
        if (match[idx]) return;

        match[idx] = true;

        // idx 번 바퀴 기준 옆에 놓인 바퀴들과 자성을 확인한다.
        // 맨 왼쪽 인 경우
        if (idx == 0) {
            // 맨 왼쪽 바퀴의 오른쪽과 오른쪽 바퀴의 왼쪽의 자성 비교
            if (magnetics[idx][2] != magnetics[idx + 1][6]) matchMagnetic(idx + 1);
        }
        // 맨 오른쪽인 경우
        else if (idx == 3) {
            if (magnetics[idx][6] != magnetics[idx - 1][2]) matchMagnetic(idx - 1);
        }
        // 그 외의 경우 (양쪽 모두 확인)
        else {
            if (magnetics[idx][2] != magnetics[idx + 1][6]) matchMagnetic(idx + 1);
            if (magnetics[idx][6] != magnetics[idx - 1][2]) matchMagnetic(idx - 1);
        }

    }

    // 점수 구하기
    static void getScore() {
        for (int i = 0; i < 4; i++) {
            if (magnetics[i][0] == 1) {
                score += (int) Math.pow(2, i);
            }
        }
    }
}
