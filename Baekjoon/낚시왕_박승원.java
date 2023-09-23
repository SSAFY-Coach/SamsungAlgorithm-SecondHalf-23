import java.io.*;
import java.util.*;


public class BOJ_17143 {
	static int R, C, M, king, result;
	static int[][] myMap, nextMap;
	static Shark[] sharks;
	
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		// 낚시왕의 위치
		king = -1;
		result = 0;
		
		myMap = new int[R][C];
		nextMap = new int[R][C];
		sharks = new Shark[M+1];
		
		for (int m = 1; m <= M; m++) {
			st = new StringTokenizer(br.readLine());
			
//			r, c: 좌표
//		    s   : 속력
//		    d   : 방향 [1~4 >> 상 하 우 좌] >> 0~3 >> 상 우 하 좌
//		    z   : 크기
			
			int r = Integer.parseInt(st.nextToken())-1;
			int c = Integer.parseInt(st.nextToken())-1;
			int s = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int z = Integer.parseInt(st.nextToken());
			
			switch (d) {
				case 1:
					d = 0;
					break;
				case 2:
					d = 2;
					break;
				case 3:
					d = 1;
					break;
				case 4:
					d = 3;
					break;
				default:
					break;
			}
			sharks[m] = new Shark(r, c, s, d, z);
			nextMap[r][c] = m;
		}
		
		
		// 낚시왕이 열 끝까지 갈때까지 실행
		while (king < C-1) {
			king++;
			// 변경된 myMap 상황 반영 (복사)
			for (int r = 0; r < R; r++) {
				for (int c = 0; c < C; c++) {
					myMap[r][c] = nextMap[r][c];
				}
			}
			
			nextMap = new int[R][C];
			
			// 현재 낚시왕이 있는 열에서 가장 가까운 상어 제거
			for (int y = 0; y < R; y++) {
				int temp = myMap[y][king];
				// temp가 0보다 크다 >> 해당 상어 사망
				if (temp > 0) {
//					System.out.println(king + " " + sharks[temp].z);
					result += sharks[temp].z;
					sharks[temp].z = 0;
					myMap[y][king] = 0;
					break;
				}
			}
			
			// 상어 이동
			for (int m = 1; m <= M; m++) {
				// 이미 죽은 상어
				if (sharks[m].z == 0) continue;
				
				Shark shrk = sharks[m];
				
				if (shrk.s > 0) {
					shrk = moveShark(shrk);
				}
				
				// 이동한 위치에 먼저 자리 잡은 상어가 있다면?
				if (nextMap[shrk.r][shrk.c] > 0) {
					// 먼저 자리 잡은 상어(nextMap[shrk.r][shrk.c])가 크다면 이동한 상어 죽이고 끝내기
					if (sharks[nextMap[shrk.r][shrk.c]].z > shrk.z) {
						sharks[m].z = 0;
						continue;
					}
					
					// shrk가 크다면 기존 상어 죽이기
					// 그리고 새로운 상어로 교체하기
					else {
						sharks[nextMap[shrk.r][shrk.c]].z = 0;
					}
				}
				sharks[m] = shrk;
				nextMap[shrk.r][shrk.c] = m;
			}
		}
		
		System.out.println(result);

	}
	
	public static Shark moveShark(Shark shrk) {
		int speed = shrk.s;
		int y = shrk.r;
		int x = shrk.c;
		while (true) {
			int yr = y + dr[shrk.d] * speed;
			int xr = x + dc[shrk.d] * speed;
			
			// 밖으로 나가는 경우
			if (yr < 0) {
				speed -= y;
				y = 0;
			}
			else if (yr >= R) {
				speed -= (R-1) - y;
				y = R-1;
			}
			else if (xr < 0) {
				speed -= x;
				x = 0;
			}
			else if (xr >= C) {
				speed -= (C-1) - x;
				x = C-1;
			}
			
			else {
				shrk.r = yr;
				shrk.c = xr;
				return shrk;
			}
			// 방향 반대로 전환 상 >> 하, 좌 >> 우
			shrk.d = (shrk.d + 2) % 4;
		}
	}

}

// 죽은 상어는 z = 0

class Shark{
	int r;
	int c;
	int s;
	int d;
	int z;
	
	Shark(int r, int c, int s, int d, int z) {
		this.r = r;
		this.c = c;
		this.s = s;
		this.d = d;
		this.z = z;
	}
}
