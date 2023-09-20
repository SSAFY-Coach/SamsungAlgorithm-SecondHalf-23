package SamsungAlgorithm_SecondHalf_23.CodeTree.원자_충돌;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - N * N 크기
 *      - M 개의 원자를 두고 충돌 실험을 진행
 *
 *  2. 원자
 *      - 총 M 개
 *      - 질량, 방향, 속력, 초기 위치를 가지고 있음
 *      - 방향 : 상하좌우, 대각선 (8방)
 *      - 1 <= x, y <= N 이하의 자연수 -> 좌상단 (1, 1) 에서 시작
 *      - 모든 행, 열은 끝과 끝이 연결되어 있음 (n 의 다음칸은 1)
 *
 * - 실험 과정
 *  1. 원자 이동
 *      - 모든 원자는 1초가 지나면 자신의 방향으로 자신의 속력만큼 이동
 *
 *  2. 원자 합성 : 이동이 모두 끝난 뒤에 하나의 칸에 2개 이상의 원자가 있는 경우에는 다음과 같은 합성이 일어난다.
 *      - 같은 칸에 있는 원자들은 각각의 질량과 속력을 모두 합한 하나의 원자로 합쳐진다.
 *      - 이후 합성된 원자는 4개의 원자로 나눠진다.
 *      - 나누어진 원자들은 모두 해당 칸에 위치하고 질량과 속려그 방향은 다음 기준을 따라 결정됨.
 *          a. 질량은 합쳐진 원자의 질량에 5를 나눈 값
 *          b. 속력은 합쳐진 원자의 속력에 합쳐진 원자의 개수를 나눈 값
 *          c. 방향은 합쳐지는 원자의 방향이 모두 상하좌우 중 하나이거나 대각선 중에 하나면 각각 상하좌우 방향을 가지며 그렇지 않다면 대각선 네 방향의 값을 가진다.
 *      - 편의상 나눗삼 과정에서 생기는 소숫점 아래의 수는 버린다.
 *      - 질량이 0인 원소는 소멸된다.
 *
 *  3. 이동 과정 중에 만나는 경우는 합성으로 고려하지 않는다.
 *
 *  answer : k초일 때, 남아있는 원자 질량 합을 구하라.
 *
 */
public class 원자_충돌_서요셉 {

    static class Atom {
        int x, y, m, s, d;

        public Atom(int x, int y, int m, int s, int d) {
            this.x = x;
            this.y = y;
            this.m = m;
            this.s = s;
            this.d = d;
        }
    }

    static int N, M, K, ans;
    static List<Atom>[][] atomBoard;
    static List<Atom> atomPool;

    // 상 우상 우 우하 하 좌하 좌 좌상
    static int[] dx = {-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        InitAtomBoard();
        atomPool = new ArrayList<>();

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());

            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int m = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

            Atom atom = new Atom(x, y, m, s, d);

            atomBoard[x][y].add(atom);
            atomPool.add(atom);
        }

    }

    public static void main(String[] args) throws Exception {
        input();

//        System.out.println(" ** 최초 격자 상태 **");
//        testPrint(atomBoard);
//        System.out.println();

        ans = 0;
        for (int i = 0; i < K; i++) {

            // 원자 이동
            moveAtom();

//            System.out.println("  원자 이동 후 격자 상태");
//            testPrint(atomBoard);
//            System.out.println();

            // 한칸에 원자가 2개 이상인 경우 원자 합성
            atomComposition();

//            System.out.println("  원자 합성 후 격자 상태");
//            testPrint(atomBoard);
//            System.out.println();
        }

        // 격자 순회하며 원자 질량 합 구하기
        findAtom();
        System.out.println(ans);
    }

    static void moveAtom() {
//        System.out.println("1. 원자 이동");
        InitAtomBoard();

        // atomPool 에서 atom 자체의 좌표를 변경시킨 다음 초기화 시킨 atomBoard 에 삽입한다.
        for (Atom a : atomPool) {
            int nx = a.x;
            int ny = a.y;

//            System.out.println("이동 전 : " + a.x + " " + a.y);
//            System.out.println("이동방향 : " + a.d + "  속력 : " + a.s);

            for (int i = 0; i < a.s; i++) {
                nx += dx[a.d];
                ny += dy[a.d];

                if (nx < 1) nx = N;
                else if (nx > N) nx = 1;

                if (ny < 1) ny = N;
                else if (ny > N) ny = 1;
            }

//            System.out.println("이동 후 : " + nx + " " + ny);
//            System.out.println();

            a.x = nx;
            a.y = ny;

            atomBoard[a.x][a.y].add(a);
        }

    }

    static void atomComposition() {
//        System.out.println("2. 원자 합성");
        // 격자 순회 하며 한칸에 2개 이상인 좌표는 원자 충돌 발생
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {

                // 원자 2개 이상인 경우만 진행
                if (atomBoard[i][j].size() >= 2) {
                    int mSum = 0;
                    int sSum = 0;
                    int aCnt = atomBoard[i][j].size();

                    boolean isRowCol = false;
                    boolean isCross = false;

                    for (Atom a : atomBoard[i][j]) {
                        mSum += a.m;
                        sSum += a.s;

                        if (a.d % 2 == 0) isRowCol = true;
                        if (a.d % 2 == 1) isCross = true;
                    }

                    int newM = mSum / 5;
                    int newS = sSum / aCnt;

                    // isRowCol, isCross 둘다 true 인 경우 -> 대각선
                    // 그 외의 경우 -> 상하좌우
                    int newD = -1;

                    if (isRowCol && isCross) newD = 1;
                    else newD = 0;

                    // 모든 원자 삭제
                    for (Atom a : atomBoard[i][j]) {
                        atomPool.remove(a);
                    }
                    atomBoard[i][j].clear();

                    // 질량이 0 이면 새로운 원자로 분리 되지 않는다.
                    if (newM == 0) continue;

                    for (int k = 0; k < 4; k++) {
                        Atom newAtom = new Atom(i, j, newM, newS, newD);

                        atomBoard[i][j].add(newAtom);
                        atomPool.add(newAtom);
                        newD += 2;
                    }

                }


            }
        }

    }

    static void findAtom() {
        for (Atom atom : atomPool) {
            ans += atom.m;
        }
    }

    static void InitAtomBoard() {
        atomBoard = new List[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                atomBoard[i][j] = new ArrayList<>();
            }
        }
    }

    static void testPrint(List<Atom>[][] b) {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (b[i][j].isEmpty()) System.out.print(0 + " ");
                else System.out.print(b[i][j].size() + " ");
            }
            System.out.println();
        }
    }
}
