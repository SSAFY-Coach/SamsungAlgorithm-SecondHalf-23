import java.io.*;
import java.util.*;


public class SWEA_4013 {
	
	static int[][] gearNS;
	static Gear[] gears;
	static boolean[] used;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		gearNS = new int[4][8];
		used = new boolean[4];
		gears = new Gear[4];
		
		int T = Integer.parseInt(br.readLine());
		
		for (int tc = 1; tc <= T; tc++) {
			int K = Integer.parseInt(br.readLine());
			
			for (int r = 0; r < 4; r++) {
				st = new StringTokenizer(br.readLine());
				for (int c = 0; c < 8; c++) {
					gearNS[r][c] = Integer.parseInt(st.nextToken());
				}
			}
			
			for (int i = 0; i < 4; i++) {
				gears[i] = new Gear(6, 2);
			}
			
			for (int k = 0; k < K; k++) {
				st = new StringTokenizer(br.readLine());
				// used 초기화
				initUsed();
				
				int idx = Integer.parseInt(st.nextToken())-1;
				int d = Integer.parseInt(st.nextToken()) * (-1);
				
				// 톱니 바퀴 회전 전 양쪽 톱니바퀴 확인
				checkGear(idx, d);
			}
			
			int answer = 0;
			int value = 1;
			
			for (int i = 0; i < 4; i++) {
				int idx = (gears[i].right - 2) % 8;
				if (idx < 0) idx += 8;
				answer += value * gearNS[i][idx];
				value *= 2;
			}
			
			System.out.println("#" + tc + " " + answer);
		}

	}

	private static void checkGear(int idx, int d) {
		// TODO Auto-generated method stub
		// 중복해서 회전시키지 않도록 true 체크
		used[idx] = true;
		
		int leftIdx = idx-1;
		int rightIdx = idx+1;
		Gear cGear = gears[idx];
		
		// 좌측 먼저 확인
		// 좌측 값이 0보다 작을 경우 왼쪽이 없으므로 pass
		// 이전에 검측되지 않은 Gear만 가능
		
		if (leftIdx >= 0 && !used[leftIdx]) {
			Gear lGear = gears[leftIdx];
			// 좌측 기어의 오른쪽 값과 가운데 기어의 왼쪽 값이 서로 다를경우  
			if (gearNS[leftIdx][lGear.right] != gearNS[idx][cGear.left]) {
				// 좌측 기어가 반대방향으로 회전한다
				checkGear(leftIdx, d * -1);
			}
		}
		
		// 우측 값 확인
		// 우측 값이 4 이상일 경우 오른쪽이 없으므로 pass
		// 이전에 검측되지 않은 Gear만 가능
		if (rightIdx < 4 && !used[rightIdx]) {
			Gear rGear = gears[rightIdx];
			// 우측 기어의 좌측 값과 가운데 기어의 오른쪽 값이 서로 다를경우 
			if (gearNS[rightIdx][rGear.left] != gearNS[idx][cGear.right]) {
				// 우측 기어가 반대방향으로 회전한다.
				checkGear(rightIdx, d * -1);
			}
		}
		
		// Gear 회전
		cGear.left = (cGear.left + d) % 8;
		cGear.right = (cGear.right + d) % 8;
		if (cGear.left < 0) cGear.left += 8;
		if (cGear.right < 0) cGear.right += 8;
		gears[idx] = cGear;
		
	}

	private static void initUsed() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 4; i++) {
			used[i] = false;
		}
	}

}

class Gear {
	int left, right;
	
	Gear(int l, int r) {
		this.left = l;
		this.right = r;
	}
}
