package SamsungAlgorithm_SecondHalf_23.SWEA.원자소멸시뮬레이션;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * - 개체
 *  1. 격자
 *      - 원자의 특성에 따라 사이즈가 결정된다.
 *      - 원자는 최초 (-1,000 ~ 1,000) 범위에 존재한다.
 *      - 예시를 확인하면 0.5초에도 충돌이 일어난다.
 *      - 즉 좌표 값들에 2를 곱하여 0.5 도 나타낼 수 있도록 보정한다.
 *      - 0 ~ 4,001 범위의 격자가 필요하다
 *
 *  2. 원자
 *      - 원자 개수는 (1 <= N <= 1,000)
 *      - 주어진 좌표값에 보정이 필요하다 (x, y) -> (x + 2,000, y + 2,000)
 *      - 1 초에 이동방향으로 1씩 이동한다.
 *      - 0 1 2 3 순서로 상하좌우로 이동방향이 주어진다.
 *      - 보유 에너지 K 는 (1 <= K <= 100)
 *      - 원자들의 최초 위치는 중복되지 않는다.
 *      - 원자들이 2개 이상 충돌할 경우 보유한 에너지를 방출하며 바로 소멸된다.
 *      - 원자 이동방향은 처음에 주어진 방향에서 바뀌지 않는다.
 *      - 원자들이 충돌하여 소멸되며 방출되는 에너지는 다른 원자의 위치나 방향에 영향을 주지 않는다.
 *
 * - answer : 원자들이 소멸되며 방출하는 에너지 총량.
 */
public class 원자소멸시뮬레이션_서요셉 {

    static int answer;
    static int N;       // 원자 개수 (1 <= N <= 1,000)

    static ArrayList<Atom> atomList = new ArrayList<>();
    static int[][] board = new int[4_001][4_001];

    // 상 하 좌 우
    static int[] dx = {1, -1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int T = Integer.parseInt(br.readLine());
        for (int t = 1; t <= T; t++) {
            N = Integer.parseInt(br.readLine());
            answer = 0;

            for (int i = 0; i < N; i++) {
                st = new StringTokenizer(br.readLine());

                int y = (Integer.parseInt(st.nextToken()) + 1_000) * 2;
                int x = (Integer.parseInt(st.nextToken()) + 1_000) * 2;
                int d = Integer.parseInt(st.nextToken());
                int k = Integer.parseInt(st.nextToken());

                Atom atom = new Atom(x, y, d, k);
                atomList.add(atom);
                board[x][y] = k;
            }

            moveAtom();
            System.out.println("#" + t + " " + answer);
        }
    }

    static void moveAtom() {
        int sum = 0;

        while (!atomList.isEmpty()) {
            for (int i = 0; i < atomList.size(); i++) {
                Atom atom = atomList.get(i);

                // 원래 좌표 0 으로 남긴다.
                board[atom.x][atom.y] = 0;

                atom.x += dx[atom.d];
                atom.y += dy[atom.d];

                // 경계선을 나갈 경우 list 에서 삭제
                if (isNotBoundary(atom.x, atom.y)) {
                    atomList.remove(i);
                    i--;
                    continue;
                }

                // 새 좌표에 에너지를 추가해준다.
                // 겹쳐지면 충돌이 일어나면서 원자는 소멸되고 방출 에너지를 구해야하므로 더해나간다.
                board[atom.x][atom.y] += atom.k;
            }

            for (int i = 0; i < atomList.size(); i++) {
                Atom atom = atomList.get(i);

                // 같지 않으면 다른 원자가 함께 있는 것
                if (board[atom.x][atom.y] != atom.k) {
                    sum += board[atom.x][atom.y];
                    board[atom.x][atom.y] = 0;
                    atomList.remove(i);
                    i--;
                }
            }
        }

        answer = sum;
    }

    static boolean isNotBoundary(int x, int y) {
        return !(0 <= x && x <= 4_000 && 0 <= y && y <= 4_000);
    }

    static class Atom {
        int x, y, d, k;

        public Atom(int x, int y, int d, int k) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.k = k;
        }
    }
}
