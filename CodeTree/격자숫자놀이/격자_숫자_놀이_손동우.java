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
//        printMap();
        while (time < MAX_TIME && !isSucceed()) {
            int maxColumnSize = getSize(true);
            int maxRowSize = getSize(false);
//            System.out.print("maxColumnSize = " + maxColumnSize);
//            System.out.println(", maxRowSize = " + maxRowSize);

            //맵 두개를 왔다 갔다 하기 위해 다음 맵 초기화
            initMap();
            sort(maxColumnSize >= maxRowSize);

            time++;
//            System.out.println();
//            System.out.println("time = " + time);
//            printMap();
//            System.out.println("================================");
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
        numberList.sort((number1, number2) -> {
            int count1 = (int) numberList.stream().filter(n -> n.intValue() == number1).count();
            int count2 = (int) numberList.stream().filter(n -> n.intValue() == number2).count();
            if (count1 == count2) {
                return number1 - number2;
            } else {
                return count1 - count2;
            }
        });
//        System.out.println("numberList");
//        numberList.forEach(number -> System.out.print(number + "\t"));
//        System.out.println();


        int beforeIndex = 0;
        int index = 0;
        for (int i = 1; i < MAX_LENGTH && index < numberList.size(); i += 2) {
            int number = numberList.get(index);
            // 다음 맵에 현재 number 기록
            map[(time + 1) % 2][column == -1 ? i : column][row == -1 ? i : row] = number;
            while (index < numberList.size() && numberList.get(index) == number) {
                index++;
            }
            // 다음 맵에 현재 number의 빈도 기록
            map[(time + 1) % 2][column == -1 ? i + 1 : column][row == -1 ? i + 1 : row] = index - beforeIndex;
//            System.out.print("number = " + number);
//            System.out.print(", beforeIndex = " + beforeIndex);
//            System.out.println(", index = " + index);
            beforeIndex = index;
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

    private static void printMap() {
        for (int i = 1; i <= getSize(true); i++) {
            for (int j = 1; j <= getSize(false); j++) {
                System.out.print(map[time % 2][i][j] + "\t");
            }
            System.out.println();
        }
    }
}