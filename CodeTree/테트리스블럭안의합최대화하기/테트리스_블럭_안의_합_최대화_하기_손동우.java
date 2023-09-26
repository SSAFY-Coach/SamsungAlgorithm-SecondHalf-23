package SamsungAlgorithm_SecondHalf_23.CodeTree.테트리스블럭안의합최대화하기;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class 테트리스_블럭_안의_합_최대화_하기_손동우 {
    private static int N, M;
    private static int[][] map;

    /*
    delta arrya
    0 ~ 1: 긴 작대기
    2: 네모
    3 ~ 6: 2번 꺾인거
    7 ~ 14: ㄱ자
    14 ~ 17: ㅗ
     */
    private static int[][] dy = {
            {0, 0, 0, 0}, {0, 1, 2, 3},
            {0, 0, 1, 1},
            {0, 0, -1, -1}, {0, 0, 1, 1}, {0, 1, 1, 2}, {0, 1, 1, 2},
            {0, 0, 0, -1}, {0, 0, 0, 1}, {-1, 0, 0, 0}, {1, 0, 0, 0}, {0, 1, 2, 2}, {0, 1, 2, 2}, {0, 0, 1, 2}, {0, 0, 1, 2},
            {0, 0, 0, 1}, {0, 0, 0, -1}, {0, 1, 2, 1}, {0, 1, 2, 1}
    };

    private static int[][] dx = {
            {0, 1, 2, 3}, {0, 0, 0, 0},
            {0, 1, 0, 1},
            {0, 1, 1, 2}, {0, 1, 1, 2}, {0, 0, 1, 1}, {0, 0, -1, -1},
            {0, 1, 2, 2}, {0, 1, 2, 2}, {0, 0, 1, 2}, {0, 0, 1, 2}, {0, 0, 0, 1}, {0, 0, 0, -1}, {1, 0, 0, 0}, {-1, 0, 0, 0},
            {0, 1, 2, 1}, {0, 1, 2, 1}, {0, 0, 0, 1}, {0, 0, 0, -1}
    };

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }

    private static int solution() {
        int answer = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                for (int i2 = 0; i2 < dy.length; i2++) {
                    int sum = 0;
                    boolean isAllInMap = true;
                    for (int j2 = 0; j2 < dy[i2].length; j2++) {
                        int ny = i + dy[i2][j2];
                        int nx = j + dx[i2][j2];
                        if (isInMap(ny, nx)) {
                            sum += map[ny][nx];
                        } else {
                            isAllInMap = false;
                            break;
                        }
                    }
                    if (isAllInMap) {
                        answer = Math.max(answer, sum);
                    }
                }
            }
        }
        return answer;
    }

    private static boolean isInMap(int y, int x) {
        return 0 <= y && y < N && 0 <= x && x < M;
    }
}