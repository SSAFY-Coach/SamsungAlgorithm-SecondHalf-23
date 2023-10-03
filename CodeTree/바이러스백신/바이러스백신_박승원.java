import java.io.*;
import java.util.*;


public class CT_Virus {
	
	static int N, M, hCnt, answer;
	static int[][] orgMap;
	static ArrayList<Node> hLst;
	static boolean[] used;
	
	static final int[] dr = {-1, 1, 0, 0};
	static final int[] dc = {0, 0, -1, 1};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		answer = 10000;
		
		orgMap = new int[N][N];
		hLst = new ArrayList<Node>();
		hCnt = 0;
		
		for (int r = 0; r < N; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < N; c++) {
				orgMap[r][c] = Integer.parseInt(st.nextToken());
				
				if (orgMap[r][c] == 2) {
					hLst.add(new Node(r, c));
					hCnt++;
				}
			}
		}
		
		used = new boolean[hCnt];
		
		// dfs를 통해 병원 M개 만큼 선택하기
		// M개 병원 선택되면 BFS로 새로운 맵 채우기
		// 모든 바이러스를 잡았는지 검증
		// 모든 바이러스를 잡았다면 정답 갱신
		
		myDfs(0, -1);
		
		if (answer == 10000) System.out.println(-1);
		else System.out.println(answer);

	}

	private static void myDfs(int depth, int bfr) {
		// TODO Auto-generated method stub
		// M개를 선택했다면 BFS 돌리기
		if (depth == M) {
			myBFS();
		}
		
		for (int i = bfr+1; i < hCnt; i++) {
			// 이미 선택된 병원이라면 continue
			if (used[i]) continue;
			used[i] = true;
			myDfs(depth+1, i);
			used[i] = false;
		}
		
	}

	private static void myBFS() {
		// TODO Auto-generated method stub
		// used에 true로 된 병원들을 deque에 넣고 BFS 진행
		
		ArrayDeque<Node> que = new ArrayDeque<Node>();
		int[][] tempMap = new int[N][N];
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				tempMap[r][c] = 10000;
			}
		}
		
		for (int m = 0; m < hCnt; m++) {
			if (used[m]) {
				Node n = hLst.get(m);
				que.add(n);
				tempMap[n.y][n.x] = 0;
//				System.out.print(m + " ");
			}
		}
		
//		System.out.println();
		
		while (!que.isEmpty()) {
			Node now = que.pollFirst();
			for (int i = 0; i < 4; i++) {
				int yr = now.y + dr[i];
				int xr = now.x + dc[i];
				// 범위 밖 continue
				if (yr < 0 || yr >= N || xr < 0 || xr >= N) continue;
				// 벽이면 continue
				if (orgMap[yr][xr] == 1) continue;
				// 이미 채워진 곳에 대해서는 더 작으면 진행 ! 아니면 취소
				// 병원은 그냥 진행! 나중에 검증할때 병원은 제외해야함
				if (tempMap[yr][xr] < tempMap[now.y][now.x] + 1) continue;
				tempMap[yr][xr] = tempMap[now.y][now.x] + 1;
				que.add(new Node(yr, xr));
				
			}
		}
		
		
		// BFS로 채워진 배열에 대해 최댓값 찾기(maxVal)
		// 죽이지 못한 바이러스가 존재한다면 -1(flag)
		boolean flag = false;
		int maxVal = 0;
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// orgMap에서 바이러스(0), 벽(1), 병원(2)
				// tempMap에서 10000은 병원의 영향이 도달하지 못한곳
				if (orgMap[r][c] == 0) {
					if (tempMap[r][c] == 10000) {
						flag = true;
					} else {
						maxVal = Math.max(maxVal, tempMap[r][c]);
					}
				}
			}
		}
		
//		System.out.println("maxVal : " + maxVal);
		
		if (flag) return;
		answer = Math.min(answer, maxVal);
		return;
	}

}

class Node {
	int y, x;
	
	Node(int y, int x) {
		this.y = y;
		this.x = x;
	}
}
