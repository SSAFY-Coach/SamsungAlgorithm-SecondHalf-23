package SamsungAlgorithm_SecondHalf_23.CodeTree.격자숫자놀이;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - 3 * 3 격자판
 *
 *
 * - 진행 : 놀이에 필요한 연산
 *  1. 행의 개수가 열의 개수보다 크거나 같은 경우
 *      - 모든 행에 대해 정렬 수행. 정렬 기준은 출현 빈도 수가 적은 순서대로 정렬.
 *      - 출현하는 횟수가 같은 숫자가 있는 경우에는 해당 숫자가 작은 순서대로 정렬.
 *      - 정렬 수행할 때 숫자와 해당하는 숫자의 출현 빈도 수를 함께 출력.
 *
 *  2. 행의 개수가 열의 개수보다 작은 경우
 *      - 모든 열에 대해 위의 과정을 수행.
 *
 *  3. 행이나 열의 길이가 100을 넘어가는 경우는 처음 100개의 격자를 제외하고 모두 버린다.
 *
 *
 * - answer : A[r][c] 의 값이 원하는 값이 되는 시간을 구하라.
 */

public class 격자숫자놀이_서요셉 {

    static class Number implements Comparable<Number> {
        int num, cnt;

        public Number(int num, int cnt) {
            this.num = num;
            this.cnt = cnt;
        }

        @Override
        public int compareTo(Number n) {
            if (n.cnt != this.cnt) return this.cnt - n.cnt;
            return this.num - n.num;
        }
    }

    static int R, C, K, answer;
    static int[][] board;

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[101][101];
        answer = -1;

        for (int i = 1; i <= 3; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= 3; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

    }

    public static void main(String[] args) throws Exception {
        input();

        for (int time = 0; time <= 100; time++) {
            if (board[R][C] == K) {
                answer = time;
                break;
            }

            if (isRowBig()) {
                for (int i = 1; i <= 100; i++) {
                    rowSort(i);
                }

            } else {
                for (int i = 1; i <= 100; i++) {
                    colSort(i);
                }
            }
        }

        System.out.println(answer);
    }

    static boolean isRowBig() {

        int maxRowCnt = 0;
        int maxColCnt = 0;

        int rowCnt;
        int colCnt;

        // 행 cnt
        for (int i = 1; i <= 100; i++) {
            rowCnt = 0;
            for (int j = 1; j <= 100; j++) {
                if (board[j][i] != 0) {
                    rowCnt++;
                }
            }

            maxRowCnt = Math.max(maxRowCnt, rowCnt);
        }

        // 열 cnt
        for (int i = 1; i <= 100; i++) {
            colCnt = 0;
            for (int j = 1; j <= 100; j++) {
                if (board[i][j] != 0) {
                    colCnt++;
                }
            }

            maxColCnt = Math.max(maxColCnt, colCnt);
        }

        return maxRowCnt >= maxColCnt;
    }

    static void rowSort(int r) {
        HashMap<Integer, Integer> hm = new HashMap<>();
        ArrayList<Number> numberList = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            if (board[r][i] != 0) {
                hm.put(board[r][i], hm.getOrDefault(board[r][i], 0) + 1);
            }
        }

        for (int key: hm.keySet()) {
            int cnt = hm.get(key);

            numberList.add(new Number(key, cnt));
        }
        Collections.sort(numberList);

        int idx = 1;
        for (Number n: numberList) {
            if (idx > 100) break;

            board[r][idx] = n.num;
            board[r][idx + 1] = n.cnt;

            idx += 2;
        }

        for (int i = idx; i <= 100; i++) {
            board[r][i] = 0;
        }
    }

    static void colSort(int c) {
        HashMap<Integer, Integer> hm = new HashMap<>();
        ArrayList<Number> numberList = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            if (board[i][c] != 0) {
                hm.put(board[i][c], hm.getOrDefault(board[i][c], 0) + 1);
            }
        }

        for (int key: hm.keySet()) {
            int cnt = hm.get(key);

            numberList.add(new Number(key, cnt));
        }
        Collections.sort(numberList);

        int idx = 1;
        for (Number n: numberList) {
            if (idx > 100) break;

            board[idx][c] = n.num;
            board[idx + 1][c] = n.cnt;

            idx += 2;
        }

        for (int i = idx; i <= 100; i++) {
            board[i][c] = 0;
        }

    }
}
