import java.io.*;
import java.util.*;


public class BOJ_15685 {
	// 주석 달 시간 없어서 물어보면 바로 답변해드림
	
	static int N;
	static int[][] myMap = new int[101][101];
	static ArrayList<Integer> direction = new ArrayList<>();
	static ArrayDeque<Integer> que = new ArrayDeque<>();
	
	// 우 상 좌 하
	static final int[] dr = {0, -1, 0, 1};
	static final int[] dc = {1, 0, -1, 0};
	
	// 사각형 체크 방향 배열 : 우 하 우하
	static final int[] drf = {0, 1, 1};
	static final int[] dcf = {1, 0, 1};
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		N = Integer.parseInt(br.readLine());
		
		int answer = 0;
		
		for (int n = 0; n < N; n++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int g = Integer.parseInt(st.nextToken());
			
			myMap[y][x] = 1;
			direction.clear();
			
			int yIdx = y + dr[d];
			int xIdx = x + dc[d];
			
			myMap[yIdx][xIdx] = 1;
			direction.add(d);
			
			for (int gIdx = 0; gIdx < g; gIdx++) {
				que.clear();
				for (int i = 0; i < direction.size(); i++) {
					que.add(direction.get(i));
				}
				
				while (!que.isEmpty()) {
					int now = que.pollLast();
					int dIdx = (now + 1) % 4;
					yIdx += dr[dIdx];
					xIdx += dc[dIdx];
					myMap[yIdx][xIdx] = 1;
					direction.add(dIdx);
				}
			}
		}
		
		for (int r = 0; r < 101; r++) {
			for (int c = 0; c < 101; c++) {
				if (myMap[r][c] == 1) {
					if (checkNemo(r, c)) answer++;
				}
			}
		}
		
		System.out.println(answer);

	}

	private static boolean checkNemo(int r, int c) {
		// TODO Auto-generated method stub
		int ret = 0;
		
		for (int i = 0; i < 3; i++) {
			int yr = r + drf[i];
			int xr = c + dcf[i];
			if (yr < 0 || yr > 100 || xr < 0 || xr > 100) return false;
			if (myMap[yr][xr] == 0) return false;
			ret++;
		}
		
		if (ret == 3) return true;
		return false;
	}

}
