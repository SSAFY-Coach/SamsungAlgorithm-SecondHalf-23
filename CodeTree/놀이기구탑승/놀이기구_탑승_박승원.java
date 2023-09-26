import java.io.*;
import java.util.*;

public class Main {
	// 놀이기구 NxN 격자에 친구들 배치
	// 애들이 속이 요~~만해서 친구 옆에 앉는거 좋아함
	// 현재 상황에서 항상 최선의 선택을 해주기 위해 빈칸은 +1, 좋아하는 친구는 +10으로 계산 (빈칸이 아무리 많아도 친구 1명을 이길 수 없게 하기 위해 가점 측정)
	// 완전 탐색으로 매순간 격자판에 점수를 매기고 가장 높은 점수에 배치
	// 행과 열은 위에서부터 차례로 보면 신경쓰지 않아도 되는 조건임
	
	static int N, num;
	static int[][] likeFriends;
	static int[][] myMap, scores;
	
	static final int[] dr = {-1, 1, 0, 0};
	static final int[] dc = {0, 0, -1, 1};	
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		N = Integer.parseInt(br.readLine());
		myMap = new int[N][N];
//		scores = new int[N][N];
		likeFriends = new int[N*N+1][4];
		
		for (int i = 0; i < N*N; i++) {
			st = new StringTokenizer(br.readLine());
			num = Integer.parseInt(st.nextToken());
			
			for (int j = 0; j < 4; j++) {
				likeFriends[num][j] = Integer.parseInt(st.nextToken());
			}
			// 각 칸에 점수 매기기
			// 그리고 자리 정해지면 앉히기
			getScores(num);
		}
		
		int answer = 0;
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				answer += getScore(r, c);
			}
		}
		
		System.out.println(answer);

	}


	private static int getScore(int r, int c) {
		// TODO Auto-generated method stub
		int idx = myMap[r][c];
		int cnt = 0;
		
		for (int i = 0; i < 4; i++) {
			int yr = r + dr[i];
			int xr = c + dc[i];
			if (yr < 0 || yr >= N || xr < 0 || xr >= N) continue;
			
			for (int j = 0; j < 4; j++) {
				if (myMap[yr][xr] == likeFriends[idx][j]) cnt++;
			}
		}
		
		switch (cnt) {
			case 1:
				return 1;
			case 2:
				return 10;
			case 3:
				return 100;
			case 4:
				return 1000;
			default:
				return 0;
		}

	}


	private static void getScores(int num) {
		// TODO Auto-generated method stub
		int maxScore = -1;
		int maxR = -1;
		int maxC = -1;
		
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				int score = 0;
				// 이미 누가 앉은 칸은 continue >> 자동 0점
				if (myMap[r][c] > 0) continue;
				for (int i = 0; i < 4; i++) {
					int yr = r + dr[i];
					int xr = c + dc[i];
					
					// 격자밖은 빈칸이 아님
					if (yr < 0 || yr >= N || xr < 0 || xr >= N) continue;
					// 빈칸이면 +1
					else if (myMap[yr][xr] == 0) {
						score++;
						continue;
					}
					
					// 좋아하는 친구가 앉아있다면 +10
					for (int j = 0; j < 4; j++) {
						if (likeFriends[num][j] == myMap[yr][xr]) {
							score += 10;
							break;
						}
					}
				}
				
				// 현재 최대 스코어보다 지금 score가 더 크다면 갱신
				if (maxScore < score) {
					maxScore = score;
					maxR = r;
					maxC = c;
				}
				
			}
		}
		myMap[maxR][maxC] = num;
		
	}

}