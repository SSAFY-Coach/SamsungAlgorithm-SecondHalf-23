// 메모리초과..

import java.io.*;
import java.util.*;


class Main {
	// 맵은 무한정 가로 세로 50, 최대 퍼져나갈수 있는 길이 300, 650? 만 해도 충분하겠지만 초기 맵 1000으로 초기화
	// 이러면 초기 좌표 잡는게 필요할듯
	// for (int r = R/2 - R; r < R; r++)
	
	// 세포를 class Cell로 정의해서 저장한다
	// 필요한 값
	// 생성된 시간 createdTime(cTm) : int
	// 생명력 lifePower(lPwr) : int
	
	// Deque와 PriorityQueue 2개를 쓴다? ( 생명력 수치가 높은 줄기 세포를 우선적으로 하려공 )
	// 1. 세포를 Deque에 저장한다, myMap에 true를 저장한다(세포가 공간을 차지하고 있음을 의미)
	// 2. 매 시간 Deque를 순회하며 현재 시간 기준 활성화 된 친구를 PriorityQueue에 넣는다?
	// 	2-1. pq에 넣는 조건은 cTm+lPwr > 현재시간 (커야함! 이상이 아님) >> 처음에 0을 넣기때문에 이상으로 수정 >> 시간을 0부터 시작하기 때문에 다시 초과로 수정
	// 3. PQ에 있는 친구들을 번식시킨다
	// 4. 번식된 친구를 Deque에 저장한다.(다시 1 반복)
	
	static int R, C, K, ans;
	static boolean[][] myMap = new boolean[701][701];
	static ArrayDeque<Cell> que, aliveQue;
	static PriorityQueue<Cell> pq;
	
	static final int N = 350;
	static final int[] dr = {-1, 1, 0, 0};
	static final int[] dc = {0, 0, -1, 1};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
//		que = new ArrayDeque<Cell>();
//		pq = new PriorityQueue<Cell>();
		
		int TC = Integer.parseInt(br.readLine());

		for (int tc = 1; tc <= TC; tc++) {
			init();
			ans = 0;
			
			st = new StringTokenizer(br.readLine());
			
			R = Integer.parseInt(st.nextToken());
			C = Integer.parseInt(st.nextToken());
			K = Integer.parseInt(st.nextToken());
			
			for (int r = R/2 - R; r < R/2; r++) {
				st = new StringTokenizer(br.readLine());
				for (int c = C/2 - C; c < C / 2; c++) {
					int num = Integer.parseInt(st.nextToken());
					if (num == 0) continue;
					myMap[r+N][c+N] = true;
					que.add(new Cell(r+N, c+N, 0, num));
					ans++;
				}
			}
			
//			System.out.println("ans : " + ans);
			
			for (int k = 1; k <= K; k++) {
//				System.out.println("-------------------------------------");
//				System.out.println("k : " + k);
				
//				int ded = -1;
				// 세포 번식
				expandCell(k);
				
//				System.out.println("ans : " + ans);
				
				// 활성 세포 중 번식 후 살아있는 친구 확인 및 확킬 찍기
				killCell();
				
//				int de = -1;
				
				// 활성화된 세포 찾기
				findCell(k);
				
//				System.out.println("ans : " + ans);
				
			}
			
//			expandCell(K-1);
			
			ans += aliveQue.size();
			
			System.out.println("#" + tc + " " + ans);
		}
	}

	private static void killCell() {
		// TODO Auto-generated method stub
		int sz = aliveQue.size();
		for (int i = 0; i < sz; i++) {
			Cell cell = aliveQue.pollFirst();
			if (cell.lifePower - 1 <= 0) continue;
			aliveQue.addLast(new Cell(cell.y, cell.x, cell.createdTime, cell.lifePower-1));
		}
		
	}

	private static void expandCell(int time) {
		// TODO Auto-generated method stub
		ans -= pq.size();
		
		while (!pq.isEmpty()) {
			Cell cell = pq.poll();
			for (int i = 0; i < 4; i++) {
				int yr = cell.y + dr[i];
				int xr = cell.x + dc[i];
				// 이미 세포가 자리잡고 있다면 continue;
				if (myMap[yr][xr]) continue;
				que.add(new Cell(yr, xr, time, cell.lifePower));
				myMap[yr][xr] = true;
				ans++;
			}
			aliveQue.add(cell);
		}
		
	}

	private static void findCell(int time) {
		// TODO Auto-generated method stub
//		int total = 0;
		
		for (int cnt = 0; cnt < ans; cnt++) {
			Cell cell = que.pollFirst();
			if (time >= cell.createdTime + cell.lifePower) {
				pq.add(cell);
//				total++;
			} else que.addLast(cell);
		}
		
//		ans -= total;
		
	}

	private static void init() {
		// TODO Auto-generated method stub
		que = new ArrayDeque<Cell>();
		aliveQue = new ArrayDeque<Cell>();
		pq = new PriorityQueue<Cell>();
		
		for (int r = 0; r < 701; r++) {
			for (int c = 0; c < 701; c++) {
				myMap[r][c] = false;
			}
		}
		
//		while (!que.isEmpty()) {
//			que.poll();
//		}
//		
//		while (!pq.isEmpty()) {
//			pq.poll();
//		}
	}

}

class Cell implements Comparable<Cell>{
	int createdTime;
	int lifePower;
	int y;
	int x;
	
	Cell(int y, int x, int c, int l) {
		this.y = y;
		this.x = x;
		this.createdTime = c;
		this.lifePower = l;
	}
	
	@Override
	public int compareTo(Cell o){
		return o.lifePower - this.lifePower;
	}
}
