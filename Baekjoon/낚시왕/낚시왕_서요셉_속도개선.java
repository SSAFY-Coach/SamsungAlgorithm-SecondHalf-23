package SamsungAlgorithm_SecondHalf_23.Baekjoon.낚시왕;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 1. 격자
 *      - R * C 인 격자판. (1, 1) 좌상단, (R, C) 우하단
 *      - 한 칸에는 한 마리 상어만 가능
 *
 * 2. 상어
 *      - 크기와 속도를 갖는다.
 *
 * 3. 낚시왕
 *      - 맨 처음에 1번열 한 칸 왼쪽(1, 0)에 있음
 *      - 1초에 1칸씩 오른쪽으로 이동한다. 가장 오른쪽 열의 오른쪽 칸에 이동하면 이동을 멈춘다. (1, C + 1)
 *
 * 다음은 1초 동안 일어나는 일이며, 아래 적힌 순서대로 일어난다.
 *
 * 1. 낚시왕이 오른쪽으로 한칸 이동한다.
 * 2. 낚시왕이 있는 열에 있는 상어 중에서 땅과 제일 가까운 (r 값이 제일 작은) 상어를 잡는다. 상어를 잡으면 격자판에서 잡은 상어가 사라진다.
 * 3. 상어가 이동한다.
 * 4. 상어 이동
 *      - 상어는 입력으로 주어진 속도로 이동. 속도의 단위는 칸/초.
 *      - 상어가 이동하려고 하는 칸이 격자판의 경계를 넘을 경우 방향을 반대로 바꿔 속력을 유지한채로 이동한다.
 *      - 즉, 상어는 속도 만큼 주어진 방향대로 이동한다.
 *      - 이동을 마친 후에 한 칸에 상어가 두마리 있으면 크기가 큰 상어가 나머지 상어를 모두 잡아먹는다.
 *
 * answer : 낚시왕이 상어 낚시를 하는 격자판의 상태가 주어지면 낚시왕이 잡은 상어 크기의 합을 구하자.
 */
public class 낚시왕_서요셉_속도개선 {

    static BufferedReader br;
    static StringTokenizer st;

    static class Shark {
        int x, y, s, d, z;        // 속력, 이동방향, 크기

        public Shark(int x, int y, int s, int d, int z) {
            this.x = x;
            this.y = y;
            this.s = s;
            this.d = d;
            this.z = z;
        }
    }

    static int R, C, M, answer;
    static Shark[][] board;
    static List<Shark> sharkList;

    // 방향 (상 하 우 좌)
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, 1, -1};

    static void start(int col) {
        // 현재 낚시왕이 위치한 열에서 땅과 가장 가까운 (r 값이 제일 작은) 상어를 잡는다.
        catchShark(col);

        // 상어가 이동한다.
        moveShark();

        // 상어 이동과 포식에 따른 상어 재배치
        arrangeShark();
    }

    // 현재 낚시왕이 위치한 열에서 땅과 가장 가까운 (r 값이 제일 작은) 상어를 잡는다.
    static void catchShark(int col) {
        for (int i = 1; i <= R; i++) {
            // 격자가 비어있지 않으면 -> 상어가 있는 것
            if (board[i][col] != null) {
//                System.out.println(" " + i + " " + col + " 위치의 상어가 잡혔습니다.");
                // 상어의 크기를 answer 에 더하고
                answer += board[i][col].z;

                // 상어를 삭제한다.
                sharkList.remove(board[i][col]);

                // 가장 먼저 발견된 상어가 땅에서 제일 가까운 것이므로 break.
                break;
            }
        }
    }

    // 상어가 이동한다.
    static void moveShark() {

        for (Shark shark: sharkList) {

            int cx = shark.x;
            int cy = shark.y;
            int cs = shark.s;
            int cd = shark.d;

            // 상하, 좌우 로 변환이 되는 경우 -> 끝 지점에 도달했을때
            // 문제 예시를 확인해보면 경계에 도달하면 방향을 바꿔 한칸 나아간다.
            // R, C 의 2배에 - 2를 한 값 안에서 돌기 때문에 상어 속도와 나눈 나머지 만큼 전진하게 하고
            // 위치에 따라 방향을 바꾸면 된다.
            switch (cd) {
                case 0:
                case 1:
                    cs = cs % (R * 2 - 2);
                    for (int i = 0; i < cs; i++) {
                        if (cx == 1) {
                            cd = 1;
                        } else if (cx == R) {
                            cd = 0;
                        }
                        cx += dx[cd];
                    }
                    shark.x = cx;
                    shark.d = cd;
                    break;

                case 2:
                case 3:
                    cs = cs % (C * 2 - 2);
                    for (int i = 0; i < cs; i++) {
                        if (cy == 1) {
                            cd = 2;
                        } else if (cy == C) {
                            cd = 3;
                        }
                        cy += dy[cd];
                    }
                    shark.y = cy;
                    shark.d = cd;
                    break;
            }
        }

    }

    static void arrangeShark() {
        board = new Shark[R + 1][C + 1];
        // 상어 리스트를 돌면서 좌표 값에 놓여진 상어와 비교하여 크기가 큰 상어들로 교체한다.
        int size = sharkList.size();
        for (int i = size - 1; i >= 0; i--) {
            Shark shark = sharkList.get(i);

            int sX = shark.x;
            int sY = shark.y;

            // 해당 위치에 상어가 있다면
            if (board[sX][sY] != null) {
                // 해당 위치 상어와 크기를 비교한다.
                if (board[sX][sY].z < shark.z) {
                    // 해당 위치 상어가 작으면 해당 위치 상어를 상어 리스트에서 제거하고
                    // 현재 상어를 격자에 배치한다.
                    sharkList.remove(board[sX][sY]);
                    board[sX][sY] = shark;
                } else {
                    sharkList.remove(shark);
                }
            }
            // 상어가 없다면
            else {
                board[sX][sY] = shark;
            }
        }
    }

    static void input() throws Exception {
        br = new BufferedReader(new InputStreamReader(System.in));
        st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new Shark[R + 1][C + 1];

        // 상어 리스트
        sharkList = new ArrayList<>();

        // 상어 정보 격자에 입력
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken()) - 1;
            int z = Integer.parseInt(st.nextToken());

            Shark shark = new Shark(x, y, s, d, z);
            board[x][y] = shark;
            sharkList.add(shark);
        }

    }

    public static void main(String[] args) throws Exception {
        input();

        // 낚시왕이 격자를 빠져 나가면 종료
        for (int i = 1; i <= C; i++) {
            start(i);
        }

        System.out.println(answer);
    }

}

