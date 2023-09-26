import java.io.*;
import java.util.*;



public class 나무박멸_박승원 {
	static int N, M, K, C;
	static int[][] myMap, medMap;
	
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1};
	
	static int[] dkr = {-1, 1, 1, -1};
	static int[] dkc = {1, 1, -1, -1};
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		
		myMap = new int[N][N];
		medMap = new int[N][N];
		
		for (int n = 0; n < N; n++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < N; c++) {
				myMap[n][c] = Integer.parseInt(st.nextToken());
			}
		}
		
		int answer = 0;
		
		for (int m = 0; m < M; m++) {
			answer += simulation();
		}
		
		System.out.println(answer);

	}
	
	public static int simulation() {
		growTree();
		expandTree();
		return killTree();
		
	}

	private static void growTree() {
		// TODO Auto-generated method stub
		ArrayDeque<Tree> que = new ArrayDeque<Tree>();
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// 칸에 나무가 존재하면
				if (myMap[r][c] > 0) {
					// 주위 칸에 나무가 있는지 확인
					int cnt = 0;
					for (int i = 0; i < 4; i++) {
						int yr = r + dr[i];
						int xr = c + dc[i];
						if (0 <= yr && yr < N && 0 <= xr && xr < N && myMap[yr][xr] > 0) {
							cnt++;
						}
					}
					// 한번에 성장 시킬꺼라 que에 저장 (바로바로 하면 다음 성장에 영향을 주기 때문)
					que.add(new Tree(r, c, cnt));
				}
			}
		}
		
		while (!que.isEmpty()) {
			Tree t = que.pollFirst();
			myMap[t.r][t.c] += t.cnt;
		}
		
		return;	
		
	}

	private static void expandTree() {
		// TODO Auto-generated method stub
		ArrayDeque<Tree> que = new ArrayDeque<Tree>();
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// 나무가 존재하는 칸에 대해 번식 진행
				if (myMap[r][c] > 0) {
					// 번식이 동시에 진행되어야하므로 번식될 칸을 미리 탐색 후 cnt 로 나누어서 계산
					Tree[] temp = new Tree[4];
					int cnt = 0;
					
					for (int i = 0; i < 4; i++) {
						int yr = r + dr[i];
						int xr = c + dc[i];
						// 제초제도 없어야 함
						if (0 <= yr && yr < N && 0 <= xr && xr < N && myMap[yr][xr] == 0 && medMap[yr][xr] == 0) {
							// cnt 값에 일단 0 넣고 나중에 일괄적으로 myMap[r][c] // cnt 로 처리할 예정
							temp[cnt] = new Tree(yr, xr, 0);
							cnt++;
						}
					}
					
					for (int j = 0; j < cnt; j++) {
						que.add(new Tree(temp[j].r, temp[j].c, myMap[r][c] / cnt));
					}
				}
			}
		}
		
		while (!que.isEmpty()) {
			Tree t = que.pollFirst();
			myMap[t.r][t.c] += t.cnt;
		}
				
		
	}
	
	private static int killTree() {
		// TODO Auto-generated method stub
		// 가장 많은 나무를 죽이는 값과 그 좌표 저장
		Tree maxVal = new Tree(-1, -1, -1);
		
		// 제초제 1년 지났으므로 빼주기
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (medMap[r][c] > 0) {
					medMap[r][c]--;
				}
			}
		}
		
		// 가장 많은 나무를 죽일 수 있는 칸 찾기
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				// 벽에 제초제를 뿌릴순 없으니까
				if (myMap[r][c] >= 0) {
					// 초기 값 == 제초제를 뿌리는 칸의 값
					int temp = myMap[r][c];
					
					// 0이면 더이상 제초제가 진행되지 못함.
					if (myMap[r][c] != 0) {
						for (int i = 0; i < 4; i++) {
							// k는 제초제 확산 범위
							for (int k = 1; k < K+1; k++) {
								int yr = r + (dkr[i] * k);
								int xr = c + (dkc[i] * k);
								// 범위 밖 혹은 나무가 없거나 벽이라면 더이상 탐색할 필요 x
								if (yr < 0 || yr >= N || xr < 0 || xr >= N) break;
								if (myMap[yr][xr] <= 0) break;
								temp += myMap[yr][xr];
							}
						}
					}
					// 제초제 죽일수 있는 나무가 더 많다면 값 갱신
					if (temp > maxVal.cnt) {
						maxVal.cnt = temp;
						maxVal.r = r;
						maxVal.c = c;
					}
				}
			}
		}
		
		// 제초제 뿌릴 위치가 정해졌으니 이제 myMap과 medMap에 반영
		
		int mr = maxVal.r;
		int mc = maxVal.c;
		myMap[mr][mc] = 0;
		medMap[mr][mc] = C;
		
		for (int i = 0; i < 4; i++) {
			for (int k = 1; k < K+1; k++) {
				int yr = mr + dkr[i] * k;
				int xr = mc + dkc[i] * k;
				
				if (yr < 0 || yr >= N || xr < 0 || xr >= N) break;
				if (myMap[yr][xr] <= 0) {
					if (myMap[yr][xr] == 0) medMap[yr][xr] = C;
					break;
				}
				
				myMap[yr][xr] = 0;
				medMap[yr][xr] = C;
			}
		}
		
		
		return maxVal.cnt;
	}

}

class Tree {
	int r, c, cnt;
	
	Tree(int r, int c, int cnt) {
		this.r = r;
		this.c = c;
		this.cnt = cnt;
	}
}
