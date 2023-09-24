package SamsungAlgorithm_SecondHalf_23.SWEA.줄기세포배양;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * - 개체.
 *  1. 줄기세포
 *      - 가로 세로 크기 1인 정사각형. 하나의 그리드 셀 또한 가로 세로 1 크기의 정사각형
 *      - 초기 상태에는 비활성 상태
 *
 *      생명력
 *
 *      - 생명력 수치가 X 라면 X 시간 동안 비활성 상태이고 X 시간이 지나는 순간 활성 상태가 된다.
 *      - 활성 상태가 되면 X 시간 동안 살아있을 수 있고 X 시간이 지나면 세포는 죽는다.
 *      - 세포가 죽더라도 소멸되는 것이 아니라 죽응ㄴ 상태로 해당 그리드 셀을 차지하게 된다.
 *
 *      번식
 *
 *       - 활성화된 줄기 세포는 첫 1시간 동안 상 하 좌 우 4방으로 동시에 번식을 한다.
 *       - 분식된 줄기 세포는 비활성 상태
 *       - 번식하려는 방향에 이미 줄기 세포가 존재하는 경우 추가적으로 번식하지 않는다.
 *       - 두 개 이상의 줄기 세포가 하나의 그리드 셀에 동시 번식하려고 하면 생명력 수치가 높은 줄기 세포가 해당 셀을 혼자 차지한다.
 *
 *  2. 격자 크기는 무한하다고 가정한다.
 *
 *
 *  answer : 줄기 세포의 초기 상태 정보와 배양 시간 K 시간이 주어지면 K 시간 후 살아있는 줄기 세포 (비활성 상태 + 활성 상태)의 총 개수를 구하는 프로그램을 작성하라.
 *
 *
 *  [제약 사항]
 *  1. 초기 상태에서 줄기 세포가 분포된 영역의 넓이는 세로 N, 가로 M 이다. (1 <= N, M <= 50)
 *  2. 배양 시간은 K. (1 <= K <= 300)
 *  3. 배양 용기 크기는 무한. 따라서 줄기 세포가 용기 가장자리에 닿아 번식할 경우는 없다.
 *  4. 줄기 세포 생명력 X. (1 <= X <= 10)
 */

public class 줄기세포배양_서요셉 {

    static final int MAX_BOARD_CNT = 150 * 2 + 50;     // 최대 번식 횟수 -> 격자의 최대 크기 150 * 2 + 50 + 1 = 351

    // 세포 상태 상수
    static final int DEFAULT = 0;
    static final int INACTIVE = 1;
    static final int ACTIVE = 2;
    static final int DEAD = 3;

    static int ans;
    static int N, M, K;
    static Cell[][] board;
    static List<Cell> cellPool;

    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int T = Integer.parseInt(br.readLine());
        for (int t = 1; t <= T; t++) {
            st = new StringTokenizer(br.readLine());


            N = Integer.parseInt(st.nextToken());
            M = Integer.parseInt(st.nextToken());
            K = Integer.parseInt(st.nextToken());
            ans = 0;

            board = new Cell[MAX_BOARD_CNT + 1][MAX_BOARD_CNT + 1];         // 351 * 351 칸
            for (int i = 0; i < MAX_BOARD_CNT + 1; i++) {
                for (int j = 0; j < MAX_BOARD_CNT + 1; j++) {
                    board[i][j] = new Cell(i, j);
                }
            }
            cellPool = new ArrayList<>();       // 최초 배치, 번식 세포들을 관리할 세포 리스트

            // 최초 상태 입력
            for (int i = 0; i < N; i++) {
                st = new StringTokenizer(br.readLine());

                for (int j = 0; j < M; j++) {
                    int life = Integer.parseInt(st.nextToken());

                    if (life != 0) {
                        // i + 150, j + 150 좌표의 세포에 life, 생성시간을 부여
                        Cell cell = board[i + 150][j + 150];
                        cell.cellInit(life, 0);

                        // 세포 리스트에 추가
                        cellPool.add(cell);
                    }

                }
            }

            for (int time = 1; time <= K; time++) {

                int size = cellPool.size();
                // 세포리스트 순회하며
                for (int i = 0; i < size; i++) {
                    Cell cell = cellPool.get(i);
                    // 이미 죽은 세포면 continue
                    if (cell.status == DEAD) continue;

                    // time == 세포.번식시간 인 세포들에 대해 '번식' 진행
                    if (time == cell.divisionTime) {
                        division(cell, time);
                    }

                    // time == 세포.활성시간 인 세포들에 대해 '활성' 상태로 변경
                    if (time == cell.activeTime) {
                        cell.status = ACTIVE;
                    }

                    // time == 세포.죽는시간 인 세포들에 대해 '죽음' 상태로 변경
                    if (time == cell.deadTime) {
                        cell.status = DEAD;
                    }

                }
            }

            // K 시간이 끝나고 살아있는 줄기 세포 총 개수 확인
            findCell();

            System.out.println("#" + t + " " + ans);
        }

    }

    // 현재 시간에 번식이 일어나는 세포들의 번식을 진행
    static void division(Cell cell, int time) {
        int x = cell.x;
        int y = cell.y;
        // 세포 좌표 기준으로 4방 탐색
        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];

            // 만일 번식하려는 방향에 이미 다른 세포가 있다면 -> (status 가 Default 이면 세포가 없는 것)
            if (board[nx][ny].status != DEFAULT) {
                // 이미 죽은 세포거나 활성 상태면 continue
                if (board[nx][ny].status == DEAD || board[nx][ny].status == ACTIVE) continue;
                // 비활성 상태이고 생성시간이 현재 시간인 세포라면 생명력 비교하여 더 큰 세포를 배치시킨다.
                if (board[nx][ny].createTime == time) {
                    // 다음좌표의 세포와 현재 좌표의 세포와 생명력 크기를 비교
                    if (cell.life > board[nx][ny].life) {
                        // 더 큰 세포의 생명력을 부여하고 생성시간을 현재로 한다.
                        board[nx][ny].cellInit(cell.life, time);
                    }
                }
            }
            // 다른 세포가 없다면 새로운 세포 번식
            else {
                board[nx][ny].cellInit(cell.life, time);
                cellPool.add(board[nx][ny]);
            }

        }

    }

    static void findCell() {
        for (Cell c : cellPool) {
            if (c.status == ACTIVE || c.status == INACTIVE) {
                ans++;
            }
        }
    }

    static class Cell {
        int x, y;
        int life;
        int createTime, activeTime, divisionTime, deadTime;
        int status;     // 0 : default   1 : 비활성   2 : 활성   3: 죽음

        // 초기 생성
        public Cell (int x, int y) {
            this.x = x;
            this.y = y;
            this.status = DEFAULT;
        }

        // 번식하게 되는 경우 반드시 주어진 정보로 이 메소드를 실행시켜야 한다.
        public void cellInit(int life, int createTime) {
            this.life = life;
            this.createTime = createTime;
            this.activeTime = createTime + life;
            this.divisionTime = activeTime + 1;
            this.deadTime = activeTime + life;
            this.status = INACTIVE;
        }

        @Override
        public String toString() {
            String s = "";

            if (status == INACTIVE) s = "INACTIVE";
            else if (status == ACTIVE) s = "ACTIVE";
            else if (status == DEAD) s = "DEAD";
            else if (status == DEFAULT) s = "DEFAULT";

            return "  Cell{" +
                    "life=" + life +
                    ", createTime=" + createTime +
                    ", activeTime=" + activeTime +
                    ", divisionTime=" + divisionTime +
                    ", deadTime=" + deadTime +
                    ", status=" + s +
                    '}';
        }
    }

}
