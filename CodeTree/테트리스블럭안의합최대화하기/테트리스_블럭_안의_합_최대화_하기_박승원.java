package SamsungAlgorithm_SecondHalf_23.CodeTree.테트리스블럭안의합최대화하기;

import java.io.*;
import java.util.*;

public class 테트리스_블럭_안의_합_최대화_하기_박승원 {
	static int answer = -1;
	static int R, C;
	static int[][] myMap;
	static boolean[][] used;
	
	static int[][] dr = {
			{0, -1, -1, 0, 1, 1},
			{-1, 0, 1, 2, 1, 0}
	};
	
	static int[][] dc = {
			{-1, 0, 1, 2, 1, 0},
			{0, 1, 1, 0, -1, -1}
	};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		
		myMap = new int[R][C];
		used = new boolean[R][C];
		
		for (int r = 0; r < R; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < C; c++) {
				myMap[r][c] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int r = 0; r < R; r++) {
			for (int c = 0; c < C; c++) {
				used[r][c] = true;
				blockSelect(r, c);
				used[r][c] = false;
			}
		}
		
		System.out.println(answer);

	}
	
	public static void myFunc(int r, int c, int total, int flag) {
		// r, c 블럭 기준 주변의 6개의 블럭 중 2개를 고르는 조합을 반복문을 이용해 중복없이 선택
    // 해당 블럭의 합으로 answer 갱신
		for (int i = 0; i < 6; i++) {
			int ir = r + dr[flag][i];
			int ic = c + dc[flag][i];
			if (!rangeMap(ir, ic)) continue;
			
			for (int j = i+1; j < 6; j++) {
				int jr = r + dr[flag][j];
				int jc = c + dc[flag][j];
				
				if (!rangeMap(jr, jc)) continue;
				
				if (answer < total + myMap[ir][ic] + myMap[jr][jc]) {
					answer = total + myMap[ir][ic] + myMap[jr][jc];
				}
			}
		}
		
		return;
	}
	
	public static void blockSelect(int r, int c) {
		int total = myMap[r][c];
		// 가로 모양 ㅁㅁ
		// 가로 모양은 c의 값이 C-1이면 못함
		if (c < C-1) {
			myFunc(r, c, total + myMap[r][c+1], 0);
		}
		
		if (r < R-1) {
			myFunc(r, c, total + myMap[r+1][c], 1);
		}
		
		// 세로 모양 日
		// 세로 모양은 r의 값이 R-1이면 못함
		return;
	}
	
  // 선택된 블럭이 myMap 범위 내의 블럭인지 확인
	public static boolean rangeMap(int y, int x) {
		if (x < 0 || x >= C || y < 0 || y >= R) return false;
		return true;
	}

}