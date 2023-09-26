import java.io.*;
import java.util.*;

public class Main {
	// 특수 영양제는 초기 좌측 하단 2x2 위치에 있다.
	// 특수 영양제는 이동한다, 이동시에는 좌표(좌<>우, 상<>하)는 이어진다(이동 시에는 밖으로 나가면 %로 처리해주기)
	// 이동 방향 우, 우상, 상, 좌상, 좌, 좌하, 하, 우하
	// 이동 후, 특수 영양제가 있는 땅 +1
	// 특수 영양제가 있는 땅 대각선으로 1이상의 리브로수 개수만큼 높이 증가
	
	// 특수 영양제가 없는 땅 중 높이가 2 이상인 나무 높이 2만큼 감소(-2) 그 위에 영양제 올려줌
	
	// 영양제 이동 방향 배열
	static final int[] medDr = {100, 0, -1, -1, -1, 0, 1, 1, 1};
	static final int[] medDc = {100, 1, 1, 0, -1, -1, -1, 0, 1};
	
	// 나무 성장 확인 대각선 방향 배열 우상 좌상 우하 좌하
	static final int[] crsDr = {-1, -1, 1, 1};
	static final int[] crsDc = {1, -1, 1, -1};
	
	// N 격자 크기
	// M 진행 년수
	// D 해당 년 영양제 이동 방향
	// P 해당 년 영양제 이동 칸수
	static int N, M, D, P;
	
	// 나무의 크기를 저장할 맵
	static int[][] treeMap;
	
	// 영양제가 뿌려진 땅인지 체크할 맵
	static boolean[][] used;
	
	static ArrayDeque<Point> medQue, nextQue;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		treeMap = new int[N][N];
		used = new boolean[N][N];
		medQue = new ArrayDeque<Point>();
		nextQue = new ArrayDeque<Point>();
		
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				treeMap[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		// 초기 좌측 하단 영양제 위치 시키기		
		initMed();
		
		for (int m = 0; m < M; m++) {
			st = new StringTokenizer(br.readLine());
			D = Integer.parseInt(st.nextToken());
			P = Integer.parseInt(st.nextToken());
			
			// 영양제의 이동
			moveMeds();
			
			// 영양제가 도착한 좌표에서 대각선 방향의 나무 개수 만큼 + 해주기
			growTree();
			
			
			// 새로운 영양제 뿌리기
			spreadMed();
//			printMap(used);
		}
		
		int answer = 0;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				answer += treeMap[r][c];
			}
		}
		
		System.out.println(answer);
		
		
	}

	private static void printMap(boolean[][] a) {
		// TODO Auto-generated method stub
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				System.out.print(a[r][c] + " ");
			}
			System.out.println();
		}
	}

	private static void spreadMed() {
		// TODO Auto-generated method stub
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// 이전에 영양제가 뿌려지지 않았으면서
				// 길이가 2 이상인 곳
				if (!used[r][c] && treeMap[r][c] >= 2) {
					treeMap[r][c] -= 2;
//					used[r][c] = true;
					medQue.add(new Point(r, c));
				}
			}
		}
		
	}

	private static void growTree() {
		// TODO Auto-generated method stub
		
		// nextQue에서 하나씩 빼서 대각선 체크
		// 개수 만큼 +
		
		// 시작전에 used 초기화 해주기
		initUsed();
		// 초기화 해줄 필요가 없네? 다시 false로 바꿔주면댐
		
		while (!nextQue.isEmpty()) {
			Point med = nextQue.pollFirst();
			int cnt = 0;
			
			for (int i = 0; i < 4; i++) {
				int yr = med.y + crsDr[i];
				int xr = med.x + crsDc[i];
				
				if (yr < 0 || yr >= N || xr < 0 || xr >= N) continue;
				else if (treeMap[yr][xr] > 0) cnt++;
			}
			
			// 다음 영양제가 뿌려질 땅에 겹치지 않기 위해 체크
			used[med.y][med.x] = true;
			treeMap[med.y][med.x] += cnt;
		}
		
	}

	private static void initUsed() {
		// TODO Auto-generated method stub
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				used[r][c] = false;
			}
		}
		
	}

	private static void initMed() {
		// TODO Auto-generated method stub
		medQue.add(new Point(N-1, 0));
		medQue.add(new Point(N-2, 0));
		medQue.add(new Point(N-1, 1));
		medQue.add(new Point(N-2, 1));
		
	}

	private static void moveMeds() {
		// TODO Auto-generated method stub
		// 영양제를 D와 P에 따라 medQue에서 하나씩 꺼내서 이동시킨뒤 nextQue에 넣음
		// nextQue에 넣기 전에 +1 씩 해서 성장 시켜주기
		
		while (!medQue.isEmpty()) {
			Point med = medQue.pollFirst();
			
			// med 이동 좌표 구하기
			int yr = (med.y + medDr[D] * P) % N;
			int xr = (med.x + medDc[D] * P) % N;
			// 이동 후 -좌표에 대해 +=N
			if (yr < 0) yr += N;
			if (xr < 0) xr += N;
			
			// 해당 좌표의 treeMap값 +1
			treeMap[yr][xr]++;
			
			// 이후 크로스 체크 후 증가를 위해 nextQue에 추가
			nextQue.add(new Point(yr, xr));
		}
		
	}

}

class Point{
	int y, x;
	Point(int y, int x) {
		this.y = y;
		this.x = x;
	}
}