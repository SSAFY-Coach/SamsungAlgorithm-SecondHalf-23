package SamsungAlgorithm_SecondHalf_23.CodeTree.테트리스블럭안의합최대화하기;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * n * m 크기의 이차원 영역의 각 위치에 자연수 하나가 적혀있다.
 * 이때 다섯가지 종류의 테트리스 블럭 중 한 개를 적당히 올려놓아
 * 블럭이 놓인 칸 안에 적힌 숫자의 합이 최대가 될 때의 결과를 출력하라.
 * (단, 테트리스 블럭은 자유롭게 회전하거나 뒤집을 수 있다.)
 *
 */

public class 테트리스_블럭_안의_합_최대화_하기_서요셉 {

    static int N, M, ans;            // 격자의 크기
    static int[][] board;       // 격자

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new int[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 1 ~ 5 가지 종류의 블록 놓기
        for (int i = 1; i <= 5; i++) {
            switch (i) {
                case 1:
                    setOne();
                    break;
                case 2:
                    setTwo();
                    break;
                case 3:
                    setThree();
                    break;
                case 4:
                    setFour();
                    break;
                case 5:
                    setFive();
                    break;
            }
        }

        System.out.println(ans);

    }

    // 직선 블록
    static void setOne() {

        int sum = 0;

        // 가로 모양
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= M - 4; j++) {
                sum = 0;
                for (int k = j; k < j + 4; k++) {
                    sum += board[i][k];
                }
                ans = Math.max(ans, sum);
            }
        }

        // 세로 모양
        for (int i = 0; i < M; i++) {
            for (int j = 0; j <= N - 4; j++) {
                sum = 0;

                for (int k = j; k < j + 4; k++) {
                    sum += board[k][i];
                }
                ans = Math.max(ans, sum);
            }
        }

    }

    // 사각형 블록
    static void setTwo() {
        int sum = 0;
        for (int i = 0; i <= N - 2; i++) {
            for (int j = 0; j <= M - 2; j++) {
                sum = 0;

                for (int k = i; k < i + 2; k++) {
                    for (int l = j; l < j + 2; l++) {
                        sum += board[k][l];
                    }
                }

                ans = Math.max(ans, sum);
            }
        }
    }

    // ㄴ 블록
    static void setThree() {
        int sum1 = 0;
        int sum2 = 0;

        // 1 && 7
        for (int i = 0; i <= N - 3; i++) {
            for (int j = 0; j < M - 1; j++) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 3; k++) {
                    sum1 += board[k][j];
                    sum2 += board[k][j];

                    if (k == i + 2) sum1 += board[k][j+1];
                    if (k == i) sum2 += board[k][j+1];
                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);
            }
        }

        // 2 && 6
        for (int i = 0; i <= N - 2; i++) {
            for (int j = 0; j < M - 2; j++) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 2; k++) {
                    sum1 += board[k][j];
                    sum2 += board[k][j];

                    if (k == i) {
                        sum1 += board[k][j + 1];
                        sum1 += board[k][j + 2];
                    }
                    if (k == i + 1) {
                        sum2 += board[k][j + 1];
                        sum2 += board[k][j + 2];
                    }
                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);
            }
        }

        // 3 && 5
        for (int i = 0; i <= N - 3; i++) {
            for (int j = M - 1; j >= 1; j--) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 3; k++) {
                    sum1 += board[k][j];
                    sum2 += board[k][j];

                    if (k == i) sum1 += board[k][j - 1];
                    if (k == i + 2) sum2 += board[k][j - 1];

                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);
            }
        }


        // 4 && 8
        for (int i = 0; i <= N - 2; i++) {
            for (int j = M - 1; j >= 2; j--) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 2; k++) {
                    sum1 += board[k][j];
                    sum2 += board[k][j];

                    if (k == i) {
                        sum1 += board[k][j - 1];
                        sum1 += board[k][j - 2];
                    }
                    if (k == i + 1) {
                        sum2 += board[k][j - 1];
                        sum2 += board[k][j - 2];
                    }
                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);
            }
        }
    }

    // 꽈배기 블록
    static void setFour() {
        int sum1 = 0;
        int sum2 = 0;

        // 1 && 3
        for (int i = 0; i <= N - 3; i++) {
            for (int j = 0; j < M - 1; j++) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 3; k++) {
                    if (k == i) {
                        sum1 += board[k][j];
                        sum2 += board[k][j + 1];
                    }
                    if (k == i + 1) {
                        sum1 += board[k][j];
                        sum1 += board[k][j + 1];
                        sum2 += board[k][j];
                        sum2 += board[k][j + 1];
                    }
                    if (k == i + 2) {
                        sum1 += board[k][j + 1];
                        sum2 += board[k][j];
                    }
                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);
            }
        }

        // 2 && 4
        for (int i = 0; i <= N - 2; i++) {
            for (int j = 0; j < M - 2; j++) {
                sum1 = 0;
                sum2 = 0;

                for (int k = i; k < i + 2; k++) {
                    if (k == i) {
                        // 2
                        sum1 += board[k][j + 1];
                        sum1 += board[k][j + 2];

                        // 4
                        sum2 += board[k][j];
                        sum2 += board[k][j + 1];
                    }
                    if (k == i + 1) {
                        // 2
                        sum1 += board[k][j];
                        sum1 += board[k][j + 1];

                        // 4
                        sum2 += board[k][j + 1];
                        sum2 += board[k][j + 2];
                    }
                }

                ans = Math.max(ans, sum1);
                ans = Math.max(ans, sum2);

            }
        }

    }

    // 철 블록
    static void setFive() {
        int sum = 0;

        // 1
        for (int i = 0; i <= N - 3; i++) {
            for (int j = 0; j < M - 1; j++) {
                sum = 0;

                for (int k = i; k < i + 3; k++) {
                    sum += board[k][j];

                    if (k == i + 1) {
                        sum += board[k][j + 1];
                    }
                }

                ans = Math.max(ans, sum);
            }
        }

        // 3
        for (int i = 0; i <= N - 3; i++) {
            for (int j = M - 1; j >= 1; j--) {
                sum = 0;

                for (int k = i; k < i + 3; k++) {
                    sum += board[k][j];

                    if (k == i + 1) {
                        sum += board[k][j - 1];
                    }
                }

                ans = Math.max(ans, sum);
            }
        }

        // 2
        for (int i = 0; i <= N - 2; i++) {
            for (int j = 0; j < M - 2; j++) {
                sum = 0;

                for (int k = i; k < i + 2; k++) {
                    if (k == i + 1) {
                        sum += board[k][j + 1];
                    } else {
                        sum += board[k][j];
                        sum += board[k][j + 1];
                        sum += board[k][j + 2];
                    }
                }

                ans = Math.max(ans, sum);
            }
        }

        // 4
        for (int i = 0; i <= N - 2; i++) {
            for (int j = 0; j < M - 2; j++) {
                sum = 0;

                for (int k = i; k < i + 2; k++) {
                    if (k == i + 1) {
                        sum += board[k][j];
                        sum += board[k][j + 1];
                        sum += board[k][j + 2];
                    } else {
                        sum += board[k][j + 1];
                    }
                }

                ans = Math.max(ans, sum);
            }
        }
    }

    // 격자 상태 출력 테스트
    static void printBoard(int[][] b) {
        System.out.println("---------------");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.print(b[i][j] + " ");
            }
            System.out.println();
        }

    }

}
