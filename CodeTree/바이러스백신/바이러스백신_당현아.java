import java.io.*;
import java.util.*;

public class Main {

    // 지도 상세 정보 값
    public static final int VIRUS = 0;
    public static final int WALL = 1;
    public static final int HOSPITAL = 2;
    public static final int INF = Integer.MAX_VALUE;

    public static int N;                    // 지도의 한 변의 길이 (3 <= N <= 50)
    public static int M;                    // 선택해야하는 병원 개수 (1 <= M <= 10)
    public static int MIN;                  // 정답 : 바이러스를 없애는 최소 시간 (불가능하다면, -1)
    public static int COUNT;                // 없애야하는 바이러스 개수
    public static int[][] MAP;              // 지도의 정보
    public static List<Point> HOSPITAL_LIST;// 병원 정보 (M <= SIZE <= 10)
    public static void main(String[] args) throws Exception {
        init();

        if (COUNT == 0) {
            // 바이러스가 없는 경우는 계산하지 않음
            MIN = 0;
        } else {
            Queue<String> cases = getCase();
            while (!cases.isEmpty())
                MIN = Math.min(MIN, findTime(cases.poll()));
        }

        System.out.println(MIN == INF ? -1 : MIN);
    }

    /**
     * 경우의 케이스에 따라 만들 수 있는 시간을 계산하는 함수
     * @param bit   : 사용여부를 기록한 문자열 (0 : 미선택, 1 : 선택)
     * @return      : 해당 경우의 최소 시간 (단, 해결할 수 없는 경우 INF)
     */
    public static int D = 4;
    public static int[][] DELTA = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    public static int findTime(String bit) {
        boolean[][] isVisited = new boolean[N][N];
        Queue<Point> queue = new LinkedList<>();

        // 선택된 병원을 큐에 담는다.
        for (int i = 0;i < bit.length();i++) {
            int idx = bit.charAt(i) - '0';
            if (idx == 0) continue;
            Point point = HOSPITAL_LIST.get(i);
            queue.offer(point);
            isVisited[point.x][point.y] = true;
        }

        // 큐에 처음 들어간 값은 병원의 위치기 때문에 시작을 -1로 시작한다.
        // 바이러스가 모두 소멸된다면, 추가적인 검사가 필요없기에 remid로 체크한다.
        int step = -1, remind = COUNT;

        while (!queue.isEmpty()) {
            if (remind == 0) break;

            step++;
            int SIZE = queue.size();
            
            for (int size = 0;size < SIZE;size++) {
                Point point = queue.poll();
                if (MAP[point.x][point.y] == VIRUS) remind--;               // 해당 위치가 바이러스가 소멸된 위치라는 의미
                if (remind == 0) break;                                     // 바이러스가 모두 소멸되었다면 추가 확인을 하지 않음

                for (int d = 0;d < D;d++) {
                    int nx = point.x + DELTA[d][0];
                    int ny = point.y + DELTA[d][1];

                    if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;   // 맵을 넘어가면 확인하지 않는다.
                    if (MAP[nx][ny] == WALL) continue;                      // 벽인 경우는 진행할 수 없다.
                    if (isVisited[nx][ny]) continue;                        // 이미 처리된 곳이면 진행하지 않는다.

                    queue.offer(new Point(nx, ny));
                    isVisited[nx][ny] = true;
                }
            }
        }

        return remind == 0 ? step : INF; // 모든 검사를 마쳤음에도 바이러스가 남아있다면 불가능한 경우
    }

    /**
     * 만들 수 있는 선택지를 확인해 올바른 선택지만 반환하는 함수
     * 올바름의 기준은 병원의 개수 중 선택은 M개를 해야한다.
     * 비트의 길이가 병원의 개수와 차이가 있는 경우, 비트의 앞을 0으로 채운다.
     * @return  : 올바른 선택의 경우를 담은 문자열 큐
     */
    public static Queue<String> getCase() {
        Queue<String> result = new LinkedList<>();
        int SIZE = HOSPITAL_LIST.size(); // 만들고자 하는 비트의 길이

        for (int i = 1;i < (1 << SIZE);i++) {
            StringBuffer sb = new StringBuffer();
            String bit = Integer.toBinaryString(i);

            // 비트의 길이가 부족한 경우 앞을 0으로 채워준다.
            for (int add = 0;add < SIZE - bit.length();add++)
                sb.append("0");
            sb.append(bit);

            String str = sb.toString();
            if (isCorrect(str))
                result.offer(str);
        }
        return result;
    }

    /**
     * 올바른 선택의 경우인지 확인하는 함수
     * @param bit   : 만들어진 비트열
     * @return      : 1이 M개 존재하는지 여부
     */
    public static boolean isCorrect(String bit) {
        int count = 0;
        for (char ch : bit.toCharArray())
            if (ch == '1')
                count++;
        return count == M;
    }
    
    public static void init() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        MAP = new int[N][N];
        HOSPITAL_LIST = new ArrayList<>();

        for (int n = 0;n < N;n++) {
            st = new StringTokenizer(br.readLine());
            for (int m = 0;m < N;m++) {
                int num = Integer.parseInt(st.nextToken());
                MAP[n][m] = num;
                if (num == HOSPITAL) HOSPITAL_LIST.add(new Point(n, m));
                else if (num == VIRUS) COUNT++;
            }
        }

        MIN = INF;
    }

    public static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
