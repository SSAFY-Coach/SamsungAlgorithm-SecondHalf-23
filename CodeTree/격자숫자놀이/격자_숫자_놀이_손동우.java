import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    private static final int MAX_LENGTH = 100;
    private static final int MAX_TIME = 100;
    private static int R, C, K;
    private static int[][][] map = new int[2][MAX_LENGTH + 1][MAX_LENGTH + 1];
    private static int time;

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        for (int i = 1; i <= 3; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= 3; j++) {
                map[0][i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }

    private static int solution() {
        while (time < MAX_TIME && !isSucceed()) {
            int maxColumnSize = getSize(true);
            int maxRowSize = getSize(false);

            //맵 두개를 왔다 갔다 하기 위해 다음 맵 초기화
            initMap();
            sort(maxColumnSize >= maxRowSize);

            time++;
        }
        return isSucceed() ? time : -1;
    }

    private static void sort(boolean isColumn) {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            List<Integer> numberList = new ArrayList<>();
            //0을 제외한 모든 숫자 list에 넣기
            for (int j = 1; j <= MAX_LENGTH; j++) {
                int number = map[time % 2][isColumn ? i : j][isColumn ? j : i];
                if (number != 0) {
                    numberList.add(number);
                }
            }

            if (!numberList.isEmpty()) {
                sortAndMove(numberList, isColumn ? i : -1, isColumn ? -1 : i);
            }
        }
    }

    private static void sortAndMove(List<Integer> numberList, int column, int row) {
        //빈도 작은 순으로, 빈도가 같다면 숫자가 작은 순으로 정렬
        int[] numberCount = countNumberList(numberList);
        for (int i = 1; i < MAX_LENGTH; i += 2) {
            int[] numberCountPair = getMinCountNumber(numberCount);
            if (numberCountPair[0] == 0 && numberCountPair[1] == 0) {
                break;
            }
            // 다음 맵에 현재 number 기록
            map[(time + 1) % 2][column == -1 ? i : column][row == -1 ? i : row] = numberCountPair[0];
            // 다음 맵에 현재 number의 빈도 기록
            map[(time + 1) % 2][column == -1 ? i + 1 : column][row == -1 ? i + 1 : row] = numberCountPair[1];
        }
    }

    private static int[] countNumberList(List<Integer> numberList) {
        int[] numberCount = new int[1001];
        for (int number : numberList) {
            numberCount[number]++;
        }
        return numberCount;
    }

    private static int[] getMinCountNumber(int[] numberCount) {
        int minNumber = Integer.MAX_VALUE;
        int minCount = Integer.MAX_VALUE;
        for (int i = 1; i < numberCount.length; i++) {
            if (numberCount[i] > 0 && minCount > numberCount[i]) {
                minCount = numberCount[i];
                minNumber = i;
            }
        }

        if (minNumber < Integer.MAX_VALUE) {
            numberCount[minNumber] = 0;
            return new int[]{minNumber, minCount};
        } else {
            return new int[]{0, 0};
        }
    }

    private static void initMap() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                map[(time + 1) % 2][i][j] = 0;
            }
        }
    }

    private static int getSize(boolean isColumn) {
        int maxSize = 0;
        for (int i = 1; i <= MAX_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                if (map[time % 2][isColumn ? j : i][isColumn ? i : j] != 0) {
                    if (maxSize < j) {
                        maxSize = j;
                    }
                }
            }
        }
        return maxSize;
    }

    private static boolean isSucceed() {
        return map[time % 2][R][C] == K;
    }
}