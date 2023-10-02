package SamsungAlgorithm_SecondHalf_23.Baekjoon.드래곤커브;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - 선분은 겹쳐질 수 있다.
 *      - 별다른 값을 나타내는 것이 아니라 그어졌는지 안그어졌는지만 보면 되므로 boolean 배열로 설정
 *
 * - 진행
 *  1. 입력 들어오는 대로 방향을 먼저 구한다.
 *  2. 세대를 줄여나가면서 방향값들을 추가시킨다.
 *  3. 이때 반시계 방향으로 추가시켜야 리스트에서 꺼내볼때 시계방향으로 그려나간다.
 *  4. 방향 리스트가 구해지면 방향에 따라 실제 좌표 값을 이동시켜나가면서 true 처리한다.
 *  5. 100 by 100 돌면서 해당 좌표 기준 꼭지점들이 true 이면 드래곤커브에 의해 찍힌 좌표들이다. cnt 증가시킨다.
 *
 * - answer : cnt 반환
 */
public class 드래곤커브_서요셉 {

    static final int RIGHT = 0;
    static final int UP = 1;
    static final int LEFT = 2;
    static final int DOWN = 3;

    static boolean[][] board;

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int N = Integer.parseInt(br.readLine());

        board = new boolean[101][101];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int gen = Integer.parseInt(st.nextToken());

            draw(x, y, getDirs(d, gen));
        }
    }
    public static void main(String[] args) throws Exception {
        input();

        System.out.println(getAnswer());
    }

    static void draw(int x, int y, List<Integer> dirs) {
        board[x][y] = true;

        for (int dir : dirs) {
            switch (dir) {
                case RIGHT:
                    board[++x][y] = true;
                    break;
                case UP:
                    board[x][--y] = true;
                    break;
                case LEFT:
                    board[--x][y] = true;
                    break;
                case DOWN:
                    board[x][++y] = true;
                    break;
            }
        }
    }

    static List<Integer> getDirs(int d, int g) {
        List<Integer> dirsList = new ArrayList<>();
        dirsList.add(d);

        while (g-- > 0) {
            for (int i = dirsList.size() - 1; i >= 0; i--) {
                int dir = (dirsList.get(i) + 1) % 4;
                dirsList.add(dir);
            }
        }

        return dirsList;
    }

    static int getAnswer() {
        int cnt = 0;

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (board[i][j] && board[i + 1][j] && board[i][j + 1] && board[i + 1][j + 1]) cnt++;
            }
        }

        return cnt;
    }
}
