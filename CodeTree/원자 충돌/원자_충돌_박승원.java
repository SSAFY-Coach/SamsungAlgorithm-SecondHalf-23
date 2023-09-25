import java.io.*;
import java.util.*;


public class atomicAccident {
	// 원자는 질량(m) 방향(d) 속력(s) 초기위치 (y, x)를 가짐
	// 방향은 8방향 (상, 우상, 우, 우하, 하, 좌하, 좌, 좌상) 0~7
	// 초기 위치(y, x)는 1~n
	
	static int N, M, K;
	
	// 이렇게 하면 합성을 처리할 때,,, 좀 빡셀듯??
//	static Atomic[] atomics;
	static ArrayList<Atomic>[][] myMap;
	static ArrayList<Atomic>[][] nextMap;
	
	static final int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
	static final int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
//		atomics = new Atomic[M+1];
		myMap = new ArrayList[N][N];
		nextMap = new ArrayList[N][N];
		
//		for (int r = 0; r < N; r++) {
//			for (int c = 0; c < N; c++) {
//				myMap[r][c] = new ArrayList<Atomic>();
//				nextMap[r][c] = new ArrayList<Atomic>();
//			}
//		}
		
		initMap(myMap);
		initMap(nextMap);
		
		
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int y = Integer.parseInt(st.nextToken())-1;
			int x = Integer.parseInt(st.nextToken())-1;
			int m = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			myMap[y][x].add(new Atomic(m, d, s));
//			atomics[i] = new Atomic(m, d, s, y, x);
		}
		
		for (int k = 0; k < K; k++) {
			simulation();
		}
		
		int answer = 0;
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				for (int i = 0; i < myMap[r][c].size(); i++) {
					answer += myMap[r][c].get(i).m;
				}
			}
		}
		
		System.out.println(answer);

	}
	
	private static void initMap(ArrayList[][] a) {
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				a[r][c] = new ArrayList<Atomic>();
			}
		}
	}


	private static void simulation() {
		// TODO Auto-generated method stub
		// 원자의 이동
		moveAtomics();
		// 원자의 합성
		fusionAtomics();
		
	}


	private static void fusionAtomics() {
		// TODO Auto-generated method stub
		// nextMap을 탐색하며 size가 1이면 합성이 이루어지지 않음
		// size가 1보다 크면 합성과 분해가 이루어짐
		// 그 결과를 다시 myMap에 옮겨줌
		
		// myMap 먼저 초기화
		initMap(myMap);
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (nextMap[r][c].size() > 1) {
					fusionAndSplit(r, c);
				}
				else if (nextMap[r][c].size() == 1) {
					int atomicM = nextMap[r][c].get(0).m;
					int atomicD = nextMap[r][c].get(0).d;
					int atomicS = nextMap[r][c].get(0).s;
					myMap[r][c].add(new Atomic(atomicM, atomicD, atomicS));
				}
			}
		}
		
		
	}

	private static void fusionAndSplit(int r, int c) {
		// TODO Auto-generated method stub
		int totalM = 0;
		int totalS = 0;
		int[] arrD = new int[2];
		
		int isCross = 1;
		
		for (int i = 0; i < nextMap[r][c].size(); i++) {
			Atomic a = nextMap[r][c].get(i);
			totalM += a.m;
			totalS += a.s;
			arrD[a.d % 2]++;
		}
		// 둘중 하나가 0이다? 상하좌우 or 모두 대각선이다.
		if (arrD[0] == 0 || arrD[1] == 0) isCross = 0;
		
		int aM = totalM / 5;
		int aS = totalS / nextMap[r][c].size();
		
		if (aM > 0) {			
			for (int i = 0; i < 8; i += 2) {
				myMap[r][c].add(new Atomic(aM, i+isCross, aS));
			}
		}
		
		return;
	}

	private static void moveAtomics() {
		// TODO Auto-generated method stub
		
		// 격자판을 탐색한다.
		// 원자가 존재하는 격자에서 (size >= 1)
		// 하나씩 이동 시켜준다.
		// 이동시킨 녀석들은 nextMap에 저장
		
		// 시작전에 nextMap 초기화
		
		initMap(nextMap);
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// 해당 격자판 ArrayList에서 하나씩 뽑는다
				for (int i = 0; i < myMap[r][c].size(); i++) {
					Atomic a = myMap[r][c].get(i);
					moveAtomic(r, c, a.m, a.d, a.s);
				}
			}
		}
	}


	private static void moveAtomic(int r, int c, int m, int d, int s) {
		// TODO Auto-generated method stub
		int yr = (r + dr[d] * s) % N;
		int xr = (c + dc[d] * s) % N;
		
		if (yr < 0) yr += N;
		if (xr < 0) xr += N;
		
		nextMap[yr][xr].add(new Atomic(m, d, s));
		
	}

}

class Atomic{
	int m, d, s;
	Atomic(int m, int d, int s) {
		this.m = m;
		this.d = d;
		this.s = s;
	}
}
