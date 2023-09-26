package SamsungAlgorithm_SecondHalf_23.Baekjoon.낚시왕;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class 낚시왕_손동우 {
    private static final int[] dy = {0, -1, 1, 0, 0};
    private static final int[] dx = {0, 0, 0, 1, -1};
    private static final int[] nextDirection = {0, 2, 1, 4, 3};
    private static int R, C, M;
    private static List<Shark> sharkList = new LinkedList<>();
    private static int[][] sharksCountMap;
    private static int fishermanX = 0;

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
        M = Integer.parseInt(st.nextToken());
        sharksCountMap = new int[R + 1][C + 1];
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int speed = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());
            int size = Integer.parseInt(st.nextToken());
            sharkList.add(new Shark(y, x, speed, direction, size));
        }
    }

    private static int solution() {
        int answer = 0;
        while (fishermanX <= C) {
            fishermanX++;
            if (fishermanX > C) {
                break;
            }
            answer += catchClosestShark();
            moveAllSharks();
        }
        return answer;
    }

    private static int catchClosestShark() {
        Optional<Shark> caughtShark = sharkList.stream()
                .filter(shark -> shark.x == fishermanX)
                .min(Comparator.comparingInt(shark -> shark.y));

        if (caughtShark.isPresent()) {
            sharkList.remove(caughtShark.get());
            return caughtShark.get().size;
        } else {
            return 0;
        }
    }

    private static void moveAllSharks() {
        for (int[] ints : sharksCountMap) {
            Arrays.fill(ints, 0);
        }
        sharkList.forEach(낚시왕_손동우::moveShark);
        eatSharks();
    }

    private static void moveShark(Shark shark) {
        int ny = shark.y + dy[shark.direction] * shark.speed;
        int nx = shark.x + dx[shark.direction] * shark.speed;

        while (ny <= 0 || R < ny) {
            if (ny > R) {
                ny = R - (ny - R);
            } else {
                ny = -ny + 2;
            }
            shark.direction = nextDirection[shark.direction];
        }

        while (nx <= 0 || nx > C) {
            if (nx > C) {
                nx = C - (nx - C);
            } else {
                nx = -nx + 2;
            }
            shark.direction = nextDirection[shark.direction];
        }

        shark.y = ny;
        shark.x = nx;
        sharksCountMap[shark.y][shark.x]++;
    }

    private static void eatSharks() {
        List<Shark> eatenSharkList = new LinkedList<>();
        for (int i = 1; i <= R; i++) {
            for (int j = 1; j <= C; j++) {
                if (sharksCountMap[i][j] > 1) {
                    int finalI = i;
                    int finalJ = j;
                    List<Shark> sameCellSharkList = sharkList.stream()
                            .filter(shark -> shark.y == finalI && shark.x == finalJ)
                            .collect(Collectors.toList());
                    int maxSize = sameCellSharkList.stream()
                            .max(Comparator.comparingInt(shark -> shark.size))
                            .orElseThrow()
                            .size;
                    eatenSharkList.addAll(sameCellSharkList.stream()
                            .filter(shark -> shark.size != maxSize)
                            .collect(Collectors.toList()));
                }
            }
        }
        sharkList.removeAll(eatenSharkList);
    }

    private static boolean isSharksInSameCell(Shark shark1, Shark shark2) {
        return shark1.y == shark2.y && shark1.x == shark2.x;
    }

    private static class Shark {
        private int y;
        private int x;
        private int speed;
        private int direction;
        private int size;

        public Shark(int y, int x, int speed, int direction, int size) {
            this.y = y;
            this.x = x;
            this.speed = speed;
            this.direction = direction;
            this.size = size;
        }
    }
}