package SamsungAlgorithm_SecondHalf_23.CodeTree.메이즈러너;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class 메이즈러너_손동우 {
    private static final int[] dy = {-1, 1, 0, 0};
    private static final int[] dx = {0, 0, -1, 1};
    private static int N, M, K;
    private static int[][] maze;
    private static List<Coord> runnerList = new LinkedList<>();
    private static Coord exit;

    public static void main(String[] args) throws Exception {
        init();
        int answer = solution();
        System.out.println(answer);
        System.out.println(exit.y + " " + exit.x);
    }

    private static void init() throws Exception {
        System.setIn(new FileInputStream("input.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        maze = new int[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                maze[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            runnerList.add(new Coord(y, x));
        }
        st = new StringTokenizer(br.readLine());
        int exitY = Integer.parseInt(st.nextToken());
        int exitX = Integer.parseInt(st.nextToken());
        exit = new Coord(exitY, exitX);
    }

    private static int solution() {
        int answer = 0;
        for (int i = 0; i < K; i++) {
            answer += moveAll();
            removeExitedRunners();
            if (runnerList.isEmpty()) {
                break;
            }
            rotate();
        }
        return answer;
    }

    private static int moveAll() {
        return runnerList.stream()
                .mapToInt(메이즈러너_손동우::move)
                .sum();
    }

    private static int move(Coord runner) {
        int moveCount = 0;
        for (int i = 0; i < 4; i++) {
            int ny = runner.y + dy[i];
            int nx = runner.x + dx[i];
            if (isInMaze(ny, nx) && isCloserToExit(runner.y, runner.x, ny, nx) && maze[ny][nx] == 0) {
                moveCount = 1;
                if (getDistanceFromExit(ny, nx) == 0) {
                    runner.y = Integer.MIN_VALUE;
                    runner.x = Integer.MIN_VALUE;
                } else {
                    runner.y = ny;
                    runner.x = nx;
                }
                break;
            }
        }
        return moveCount;
    }

    private static void removeExitedRunners() {
        runnerList.removeAll(runnerList.stream()
                .filter(runner -> runner.y == Integer.MIN_VALUE && runner.x == Integer.MIN_VALUE)
                .collect(Collectors.toList()));
    }

    private static boolean isInMaze(int ny, int nx) {
        return 0 < ny && ny <= N && 0 < nx && nx <= N;
    }

    private static boolean isCloserToExit(int y, int x, int ny, int nx) {
        return getDistanceFromExit(y, x) > getDistanceFromExit(ny, nx);
    }

    private static void rotate() {
        sortRunnerList();
        Coord[] squareCoord = findSquareCoord();
        rotateSquare(squareCoord[0], squareCoord[1]);
    }

    private static void sortRunnerList() {
        runnerList.sort((runner1, runner2) -> {
            if (runner1.y != runner2.y) {
                return runner1.y - runner2.y;
            } else {
                return runner1.x - runner2.x;
            }
        });
    }

    private static Coord[] findSquareCoord() {
        int minSideOfSquare = Integer.MAX_VALUE;
        int minSquareY = Integer.MAX_VALUE;
        int minSquareX = Integer.MAX_VALUE;
        for (Coord runner : runnerList) {
            int diffY = Math.abs(runner.y - exit.y) + 1;
            int diffX = Math.abs(runner.x - exit.x) + 1;
            int sideOfSquare = Math.max(diffY, diffX);
            if (minSideOfSquare >= sideOfSquare) {
                int maxCoordY = Math.max(exit.y, runner.y);
                int maxCoordX = Math.max(exit.x, runner.x);
                int minTempSquareY = maxCoordY - sideOfSquare + 1;
                while (minTempSquareY < 1) {
                    minTempSquareY++;
                }
                int minTempSquareX = maxCoordX - sideOfSquare + 1;
                while (minTempSquareX < 1) {
                    minTempSquareX++;
                }
                if (minSideOfSquare > sideOfSquare) {
                    minSquareY = minTempSquareY;
                    minSquareX = minTempSquareX;
                    minSideOfSquare = sideOfSquare;
                } else if (minSquareY > minTempSquareY) {
                    minSquareY = minTempSquareY;
                    minSquareX = minTempSquareX;
                } else if (minSquareY == minTempSquareY && minSquareX > minTempSquareX) {
                    minSquareX = minTempSquareX;
                }
            }
        }
        return new Coord[]{
                new Coord(minSquareY, minSquareX),
                new Coord(minSquareY + minSideOfSquare - 1, minSquareX + minSideOfSquare - 1)
        };
    }

    private static void rotateSquare(Coord squareTopLeft, Coord squareBottomRight) {
        int minY = squareTopLeft.y;
        int minX = squareTopLeft.x;
        int maxY = squareBottomRight.y;
        int maxX = squareBottomRight.x;
        int sideOfSquare = maxY - minY + 1;
        int[][] tempMaze = new int[sideOfSquare][sideOfSquare];
        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {
                tempMaze[j - minX][(sideOfSquare - 1) - (i - minY)] = Math.max(maze[i][j] - 1, 0);
            }
        }
        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {
                maze[i][j] = tempMaze[i - minY][j - minX];
            }
        }

        Coord tempExit = new Coord(exit.y, exit.x);
        exit.y = tempExit.x - minX + minY;
        exit.x = maxY - tempExit.y + minX;
        for (Coord runner : runnerList) {
            if (minY <= runner.y && runner.y <= maxY && minX <= runner.x && runner.x <= maxX) {
                Coord tempRunner = new Coord(runner.y, runner.x);
                runner.y = tempRunner.x - minX + minY;
                runner.x = maxY - tempRunner.y + minX;
            }
        }
    }

    private static int getDistanceFromExit(int y, int x) {
        return getDistance(exit.y, exit.x, y, x);
    }

    private static int getDistance(int y1, int x1, int y2, int x2) {
        return Math.abs(y1 - y2) + Math.abs(x1 - x2);
    }

    private static class Coord {

        private int y;
        private int x;

        public Coord(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }
}