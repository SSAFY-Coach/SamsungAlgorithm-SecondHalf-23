package SamsungAlgorithm_SecondHalf_23.CodeTree.나무박멸;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <자료구조>
 * 1. 나무, 벽 격자 (n * n 크기)
 *      - 나무 그루수, 벽 정보
 *
 * 2. 제초제
 *      - k 범위 만큼 대각선으로 퍼짐
 *      - 벽이 있는 경우 가로막혀 퍼지지 않음.
 *
 * <기능>
 * 1년 동안 일어나는 일
 *      1. 나무 성장.
 *          - 모든 나무에게 동시에 일어남.
 *          - 4방의 인접한 칸의 나무 개수 만큼 나무 그루수가 증가함.
 *          - 기존에 있었던 나무들은 인접한 4개의 칸 중에 벽, 다른 나무, 제초제 없는 칸에 번식을 진행한다.
 *
 *      2. 나무 번식
 *          - 각 칸의 '나무 그루 수'에서 4방향 중 총 '번식이 가능한 칸의 개수' 만큼 나누어진 그루 수 만큼 번식. 나눌때 생기는 나머지는 버린다.
 *          - 번식은 모든 나무에서 동시에 일어남.
 *
 *      3. 제초제 뿌리기
 *          - 제초제를 뿌릴때 나무가 가장 많이 박멸되는 칸에 제초제를 뿌린다.
 *          - 나무가 없는 칸에 제초제를 뿌리면 박멸되는 나무가 전혀 없는 상태로 끝나지만,
 *          - 나무가 있는 칸에 제초재를 뿌리면 4개의 대각선 방향으로 k 칸만큼 전파된다.
 *          - 단, 전파되는 도중 벽이 있거나 나무가 아예 없는 칸이 있으면 그 칸까지는 뿌려지며 그 이후의 칸으로는 전파되지 않는다.
 *          - 제초제가 뿌려진 칸에는 c 년만큼 제초제가 남아있다가 c+1 년째 사라진다.
 *          - 제초제가 뿌려진 곳에 다시 제초제가 뿌려진 경우 새로 뿌려진 해로부터 c 년 동안 제초제가 유지된다.
 *
 *  answer : m 년동안 총 박멸한 나무의 그루 수를 구하라.
 */
public class 나무박멸_서요셉 {

    // 격자의 크기 n , 박멸 진행 년수 m , 제초제 확산 범위 k,  제초제 유효 년수 c
    // (5 <= n <= 20) || (1 <= m <= 1,000) || (1 <= k <= 20) || (1 <= c <= 10)
    static int n, m, k, c, answer;

    static int[][] tree;                    // 0 : 빈칸, -1 : 벽, 1 ~ 100 : 나무 수
    static int[][] jecho;                   // 제초제 뿌려진 위치, 각 위치값 = 제초제 유효기간
    static int[][] addTree;

    // 나무 성장 시 델타 배열
    static int[] tDx = {-1, 0, 1, 0};
    static int[] tDy = {0, -1, 0, 1};

    // 제초제 확산 시 델타 배열
    static int[] zDx = {-1, 1, -1, 1};
    static int[] zDy = {-1, -1, 1, 1};

    public static void main(String[] args) throws Exception {
        input();

        // m 년 동안 진행
        for (int i = 0; i < m; i++) {
            start();
        }

        System.out.println(answer);
    }

    static void start() {
        // 나무 성장
        growTree();

        // 나무 번식
        spreadTree();

        // 제초제 뿌리기
        spreadJecho();
    }

    // 나무 성장
    static void growTree() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                // 벽이거나 빈칸이면 continue
                if (tree[i][j] <= 0) continue;

                int cnt = 0;
                for (int d = 0; d < 4; d++) {
                    int nx = i + tDx[d];
                    int ny = j + tDy[d];

                    if (isNotBoundary(nx, ny)) continue;
                    if (tree[nx][ny] > 0) cnt++;
                }

                tree[i][j] += cnt;
            }
        }

    }
    static boolean isNotBoundary(int x, int y) {
        return !(0 <= x && x < n && 0 <= y && y < n);
    }

    // 나무 번식
    static void spreadTree() {
        // "원래 있던 나무들 대상" 으로 번식 진행
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                addTree[i][j] = 0;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 벽이거나 빈칸이면 continue
                if (tree[i][j] <= 0) continue;

                int cnt = 0;        // 주변 번식 가능한 칸 수 cnt (벽이 아니고 나무가 없어야하고 제초제가 없어야함)

                for (int d = 0; d < 4; d++) {
                    int nx = i + tDx[d];
                    int ny = j + tDy[d];

                    if (isNotBoundary(nx, ny)) continue;
                    if (jecho[nx][ny] > 0) continue;
                    if (tree[nx][ny] == 0) cnt++;
                }

                for (int d = 0; d < 4; d++) {
                    int nx = i + tDx[d];
                    int ny = j + tDy[d];

                    if (isNotBoundary(nx, ny)) continue;
                    if (jecho[nx][ny] > 0) continue;
                    if (tree[nx][ny] == 0) {
                        addTree[nx][ny] += tree[i][j] / cnt;
                    }
                }
            }
        }

        // 원래 트리 격자에 번식한 나무 정보 추가
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tree[i][j] += addTree[i][j];
            }
        }
    }

    // 제초제 뿌리기
    static void spreadJecho() {
        // 1년마다 제초제 감소
        decreaseJecho();

        int max = 0;
        int maxI = 0;
        int maxJ = 0;

        // 나무 격자 순회, 나무가 있는 좌표에서 k 범위 만큼 4방 대각선으로 그루수 합(sum) 구하기
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 나무가 있는 곳에서만 제초제 뿌릴 여부 검사
                if (tree[i][j] <= 0) continue;

                int cnt = tree[i][j];       // 박멸할 나무 수 cnt

                for (int d = 0; d < 4; d++) {
                    int nx = i;
                    int ny = j;

                    for (int l = 0; l < k; l++) {
                        nx = nx + zDx[d];
                        ny = ny + zDy[d];

                        // 경계선을 넘거나 빈칸이거나 벽이면 break; (다음으로는 퍼지지 않으므로)
                        if (isNotBoundary(nx, ny)) break;
                        if (tree[nx][ny] <= 0) break;

                        cnt += tree[nx][ny];
                    }
                }

                if (max < cnt) {
                    max = cnt;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        answer += max;

        if (tree[maxI][maxJ] > 0) {
            jecho[maxI][maxJ] = c;
            tree[maxI][maxJ] = 0;

            for (int d = 0; d < 4; d++) {
                int nx = maxI;
                int ny = maxJ;

                for (int i = 0; i < k; i++) {
                    nx = nx + zDx[d];
                    ny = ny + zDy[d];

                    // 경계선을 넘거나 벽이면 break
                    // 빈칸인 경우는 제초제만 뿌리고 break
                    if (isNotBoundary(nx, ny)) break;
                    if (tree[nx][ny] < 0) break;
                    if (tree[nx][ny] == 0) {
                        jecho[nx][ny] = c;
                        break;
                    }

                    jecho[nx][ny] = c;
                    tree[nx][ny] = 0;
                }
            }
        }

    }


    static void decreaseJecho() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 제초제가 0보다 클 경우에만 1 감소
                if (jecho[i][j] > 0) jecho[i][j]--;
            }
        }
    }

    static void input() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());

        tree = new int[n][n];
        addTree = new int[n][n];
        jecho = new int[n][n];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                tree[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }

    static void printBoard(int[][] board) {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
