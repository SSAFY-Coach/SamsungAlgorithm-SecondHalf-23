package SamsungAlgorithm_SecondHalf_23.CodeTree.메이즈러너;

import java.io.*;
import java.util.*;

public class 메이즈_러너_박승원 {
	static int N, M, K, result;
	static Point ext;
	static int[][] myMap;
	static Point[] players;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		players = new Point[M+1];
		myMap = new int[N][N];
		
		for (int r = 0; r < N; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < N; c++) {
				myMap[r][c] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			players[i] = new Point(r-1, c-1);
		}
		
		st = new StringTokenizer(br.readLine());
		int er = Integer.parseInt(st.nextToken());
		int ec = Integer.parseInt(st.nextToken());
		ext = new Point(er-1, ec-1);

		for (int k = 0; k < K; k++) {
			playerMove();
			rotateMaze();
		}
		
		System.out.println(result);
		System.out.println(ext.y + " " + ext.x);
	}
	
	private static void rotateMaze() {
		// TODO Auto-generated method stub
		// 참가자와 출구를 포함하는 가장 작은 사각형 찾기
		// 해당 사각형 회전
		findSquare();
	}

	private static void findSquare() {
		// TODO Auto-generated method stub
		int er = ext.y;
		int ec = ext.x;
		
		// 사각형 한 변의 길이는 최소 2부터
		for (int len = 2; len <= N; len++) {
			for (int r = 0; r < N-len+1; r++) {
				for (int c = 0; c < N-len+1; c++) {
					int re = r + len - 1;
					int ce = c + len - 1;
					// 출구가 범위내에 없다면 continue;
					if (!isInRange(r, re, c, ce, er, ec)) continue;
					
					// 범위내에 참가자가 있는지?
					for (int m = 0; m < M; m++) {
						// 출구에 있는 참가자는 제외
						Point p = players[m];
						if (p.y == er && p.x == ec) continue;
						if (isInRange(r, re, c, ce, p.y, p.x)) {
							// 범위내에 참가자가 존재하면 해당 사각형 회전 후 return
							rotateSquare(r, re, c, ce, len);
							// 범위내 참가자와 출구 위치 변경
							rotatePE(r, re, c, ce, len);
							return;
						}
					}
				}
			}
		}
	}

	private static void rotatePE(int sr, int er, int sc, int ec, int len) {
		// TODO Auto-generated method stub
		for (int m = 0; m < M; m++) {
			Point p = players[m];
			// 이미 나간 참가자여도 바뀌는 출구위치로 이동시키기 위해 여기서는 제외하지 않음
			if (isInRange(sr, er, sc, ec, p.y, p.x)) {
				// 상하 반전
				p.y = er - p.y + sr;
				// 대각선 반전
				int temp = p.y;
				p.y = p.x;
				p.x = temp;
			}
		}
		
		int ey = ext.y;
		int ex = ext.x;
		
		ey = er - ey + sr;
		
		int temp = ey;
		ey = ex;
		ex = ey;
		
		ext.y = ey;
		ext.x = ex;
		
		return;
		
	}

	private static void rotateSquare(int sr, int er, int sc, int ec, int len) {
		// TODO Auto-generated method stub
		// 일단 범위내 벽들은 -1씩 해주기
		for (int r = sr; r <= er; r++) {
			for (int c = sc; c <= ec; c++) {
				if (myMap[r][c] > 0) myMap[r][c]--;
			}
		}
		
		
		// 상하 반전
		for (int r = sr; r < sr + len / 2; r++) {
			for (int c = sc; c <= ec; c++) {
				int temp = myMap[r][c];
				myMap[r][c] = myMap[er-r+sr][c];
				myMap[er-r+sr][c] = temp;
			}
		}
		
		// 대각선 반전
		for (int r = sr; r <= er; r++) {
			for (int c = sc + (r - sr); c <= ec; c++) {
				int temp = myMap[r][c];
				myMap[r][c] = myMap[c][r];
				myMap[c][r] = temp;
			}
		}
		
		
		
	}

	private static boolean isInRange(int sr, int er, int sc, int ec, int fr, int fc) {
		// TODO Auto-generated method stub
		if (fr >= sr && fr <= er && fc >= sc && fc <= ec) return true;
		return false;
	}

	public static void playerMove() {
		int er = ext.y;
		int ec = ext.x;
		
		for (int m = 0; m < M; m++) {
			Point p = players[m];
			
			// 이미 나간(ext) 경우? 는 continue
			if (p.y == er && p.x == ec) continue;
			
			//플레이어를 이동할 때 먼저 r 값을 비교한다
			// 차이가 있을 경우 이동시킨다.
			// -1 또는 1로 이동시키며 이동시키는 myMap의 칸이 0이하여야 한다.
			if (p.y != er) {
				int yr = p.y;
				if (er > yr) yr++;
				else yr--;
				
				// 벽이라면 이동 불가능
				if (myMap[yr][p.x] == 0) {
					players[m].y = yr;
					// 이동함으로 result ++;
					result++;
					// r값에서 이동이 있을 경우 continue;
					continue;
				}
			}
			
			// c값의 차이가 있을 경우 이동시킨다.
			if (p.x != ec) {
				int xr = p.x;
				if (ec > xr) xr++;
				else xr--;
				
				// 벽이라면 이동 불가능
				if (myMap[p.y][xr] == 0) {
					players[m].x = xr;
					// 이동함으로 result ++;
					result++;
					// c값에서 이동이 있을 경우 continue;
					continue;
				}
			}
			
			
		}
	}

}

class Point{
	int y;
	int x;
	
	Point(int y, int x) {
		this.y = y;
		this.x = x;
	}
}
