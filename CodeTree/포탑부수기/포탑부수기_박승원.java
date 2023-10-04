import java.io.*;
import java.util.*;


public class CT_turret {
	/**
	 * 0. 포탑의 공격력이 0이 된다면 부서진 포탑이 되어 더이상 공격할 수 없음
	 * 
	 * 1. 공격자 선정
	 * 	1) 부서지지 않은 포탑 중 가장 약한 포탑이 공격자로 선정 
	 * 	2) 공격자로 선정된 포탑은 N+M 만큼의 공격력이 증가됨
	 * 	3) 가장 약한 포탑
	 * 		- 공격력이 가장 낮은 포탑
	 * 		- 가장 최근에 공격한 포탑
	 * 		- 행과 열의 합이 가장 큰 포탑
	 * 		- 열값이 가장 큰 포탑
	 * 
	 * 2. 공격자의 공격
	 * 	1) 공격자는 가장 강한 포탑을 공격
	 * 	2) 가장 강한 포탑
	 * 		- 공격력이 가장 높은 포탑
	 * 		- 공격한지 가장 오래된 포탑
	 * 		- 행과 열의 합이 가장 작은 포탑
	 * 		- 열 값이 가장 작은 포탑
	 * 
	 * 3. 레이저 공격
	 * 	1) 규칙
	 * 		- 레이저는 상하좌우의 4개의 방향으로 움직인다
	 * 		- 부서진 포탑이 있는 위치는 지날 수 없다
	 * 		- 가장자리에서 막힌 방향으로 진행하면, 반대편으로 나온다.
	 * 	2) 레이저는 최단 경로로 공격합니다.
	 * 	3) 최단 경로가 없다면 포탄 공격을 진행합니다.
	 * 	4) 최단 경로가 2개 이상이라면 우, 하, 좌, 상의 우선순위대로 먼저 움직인 경로가 선택됩니다.
	 * 	5) 최단 경로가 정해진 후 공격 대상에는 공격자의 공격력 만큼의 피해를 입히며, 피해를 입은 포탑은 해당 수치만큼 공격력이 줄어듬.
	 * 	6) 경로에 있는 포탑들은 공격력의 절반만큼의 공격을 받음.
	 * 
	 * 4. 포탄 공격
	 * 	1) 공격 대상에 포탄 투하
	 * 	2) 추가적으로 8방향 스플래쉬 데미지 ( 마찬가지로 절반의 피해 )
	 * 	3) 8방향 중 범위 밖의 경우 반대편 격자에 영향을 미치게 된다.
	 **/
	
	static int R, C, K, tCnt;
	static Turret attacker, target;
	
	// 초기 맵?에는 벽인지 아닌지만 확인할 수 있는 맵으로 설정?
	// int로 만든후 터렛번호 저장
	// 터렛 번호는 터넷 배열의 index, 0부터 시작 및 벽은 -1로 표시
	static int[][] isWall;
	// 공격 가능 경로 표시하기 위한 맵
	// int로 하구, 경로 방향을 역으로 표시해 선택된 경로를 역추적할 수 있도록 설정
	// -1로 초기화! 방향 배열 0~3이 새겨지면 앞서 지나간 경로가 있다고 판단
	static int[][] attackPath;
	
	static ArrayList<Turret> turrets;
	
	// 						우 하 좌 상 좌상 좌하 우상 우하
	static final int[] dr = {0, 1, 0, -1, -1, 1, -1, 1};
	static final int[] dc = {1, 0, -1, 0, -1, -1, 1, 1};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		// 사용되는 2차원 배열 및 터렛 ArrayList 초기화
		isWall = new int[R][C];
		attackPath = new int[R][C];
		turrets = new ArrayList<Turret>();
		// 터렛 개수 확인을 위한 변수 초기화
		tCnt = 0;
		
		for (int r = 0; r < R; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < C; c++) {
				int n = Integer.parseInt(st.nextToken());
				switch (n) {
					case 0:
						isWall[r][c] = -1;
						break;
					default:
						Turret t = new Turret(r, c, n, 0);
						turrets.add(t);
						isWall[r][c] = tCnt;
						tCnt++;
						break;
				}
			}
		}
		
		
		for (int k = 1; k <= K; k++) {
			if (getCntTurret() == 1) break;
			
			// 공격자 터렛 및 타겟 터렛 초기화
			attacker = new Turret(-1, -1, 5001, -1);
			target = new Turret(-1, -1, -1, -1);
			// 공격자 선정
			findAttacker(k);
			// 타겟 선정
			findTarget(k);
				// ? 공격자와 타겟이 같을 수 있는가?
				// 최소 두개 존재한다고 했으니까... 괜찮지 않을까?
			// 레이저 공격 체크
			// attackPath 초기화
			
//			System.out.println(attacker.y + " " + attacker.x + " " + attacker.power);
//			if (attacker.y == 4 && attacker.x == 4 && attacker.power == 51) {
//				int de = -1;
//			}
			
			// 공격자와 타겟 isUsed 체크
			checkAT();
			
			initMap();
			tryLaser();
			// 레이저 성공 시 레이저 공격
			// 레이저 실패 시 포탄 공격
			// 레이저 공격 성공 여부는 attackPath에 -1이 아니면 성공임
			if (attackPath[target.y][target.x] == -1) {
				doBomb();
			} else {
				doLaser();
			}
			
			// 이번 타임에 관심없던 터렛은 += 1
			levelUp();
		}
		
		System.out.println(findAnswer());

	}

	private static int getCntTurret() {
		// TODO Auto-generated method stub
		int ret = 0;
		for (int i = 0; i < tCnt; i++) {
			Turret t = turrets.get(i);
			if (t.power <= 0) continue;
			ret++;
		}
		return ret;
	}

	private static int findAnswer() {
		// TODO Auto-generated method stub
		int ret = -1;
		
		for (int i = 0; i < tCnt; i++) {
			Turret t = turrets.get(i);
			if (ret < t.power) {
				ret = t.power;
			}
		}
		
		return ret;
	}

	private static void levelUp() {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < tCnt; i++) {
			Turret t = turrets.get(i);
			int tIdx = isWall[t.y][t.x];
			if (tIdx == -1) continue;
			if (t.isUsed) {
				t.isUsed = false;
			}
			else {
				turrets.get(i).power += 1;
			}
		}
		
	}

	private static void checkAT() {
		// TODO Auto-generated method stub
		int aIdx = isWall[attacker.y][attacker.x];
		int tIdx = isWall[target.y][target.x];
		
		turrets.get(aIdx).isUsed = true;
		turrets.get(tIdx).isUsed = true;
	}

	private static void doLaser() {
		// TODO Auto-generated method stub
		// target에서 시작해서 시작점까지 역으로 경로 추적 및 데미지 주기
		
		int damage = attacker.power;
		int tIdx = isWall[target.y][target.x];
		
		turrets.get(tIdx).power -= damage;
		if (turrets.get(tIdx).power <= 0) {
			dieTurret(target.y, target.x);
		}
		
		int d = attackPath[target.y][target.x];
		int r = target.y;
		int c = target.x;
		damage /= 2;
		
		while (true) {
			r = (r + dr[d]) % R;
			c = (c + dc[d]) % C;
			if (r < 0) r += R;
			if (c < 0) c += C;
			
			d = attackPath[r][c];
			if (d == 4) break;
			
			int tNum = isWall[r][c];
			turrets.get(tNum).power -= damage;
			if (turrets.get(tNum).power <= 0) {
				dieTurret(r, c);
			}
			
			turrets.get(tNum).isUsed = true;
			
		}
		
	}

	private static void doBomb() {
		// TODO Auto-generated method stub
		int damage = attacker.power;
		int tIdx = isWall[target.y][target.x];
		
		// 가운데 정상 데미지 주기
		turrets.get(tIdx).power -= damage;
		// 데미지를 입은 포탑이 죽었다면 isWall에 표시해주어야함
		if (turrets.get(tIdx).power <= 0) {
			dieTurret(turrets.get(tIdx).y, turrets.get(tIdx).x);
		}
		// 8방향 절반 데미지 주기
		damage /= 2;
		for (int i = 0; i < 8; i++) {
			int yr = (target.y + dr[i]) % R;
			int xr = (target.x + dc[i]) % C;
			if (yr < 0) yr += R;
			if (xr < 0) xr += C;
			int tNum = isWall[yr][xr];
			// 이미 죽은 포탑 or 벽이라면 pass
			if (tNum == -1) continue;
			turrets.get(tNum).power -= damage;
			if (turrets.get(tNum).power <= 0) {
				dieTurret(turrets.get(tNum).y, turrets.get(tNum).x);
			}
			
			turrets.get(tNum).isUsed = true;
			
		}
		
	}

	private static void dieTurret(int y, int x) {
		// TODO Auto-generated method stub
		isWall[y][x] = -1;
	}

	private static void initMap() {
		// TODO Auto-generated method stub
		for (int r = 0; r < R; r++) {
			for (int c = 0; c < C; c++) {
				attackPath[r][c] = -1;
			}
		}
	}

	private static void tryLaser() {
		// TODO Auto-generated method stub
		// attacker에서 BFS로 target에 도달하는지 확인
		// 최단거리는 따로 체크할 필요없음 (우선순위에 맞춰서 탐색하기때문에)
		// 나중에 도착했을때 최단거리를 확인하기 위해 방향 +2 % 4를 한 값을 attackPath에 기록
		
		ArrayDeque<Turret> que = new ArrayDeque<Turret>();
		que.add(attacker);
		attackPath[attacker.y][attacker.x] = 4;
		
		while (!que.isEmpty()) {
			Turret t = que.pollFirst();
			if (t.y == target.y && t.x == target.x) {
				break;
			}
			for (int i = 0; i < 4; i++) {
				// 범위 밖으로 나가는건 체크 안해도 되도록 모듈러 연산 해두기
				int yr = (t.y + dr[i]) % R;
				int xr = (t.x + dc[i]) % C;
				if (yr < 0) yr += R;
				if (xr < 0) xr += C;
				// 이미 앞서 방문했다면 그것이 최단거리 및 우선순위에 적합한 경로 이므로 continue
				// 출발점도 체크 (4이므로 체크됨)
				if (attackPath[yr][xr] >= 0) continue;
				// 벽이면 진행 못해
				if (isWall[yr][xr] == -1) continue;
				que.add(new Turret(yr, xr, 0, 0));
				attackPath[yr][xr] = (i + 2) % 4;
			}
		}
	}

	private static void findTarget(int k) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < tCnt; i++) {
			Turret t = turrets.get(i);
			if (t.power <= 0 || (t.y == attacker.y && t.x == attacker.x)) continue;
			target = checkPower(1, t, target);
		}
		
	}

	private static void findAttacker(int k) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < tCnt; i++) {
			Turret t = turrets.get(i);
			// 죽은 포탑은 pass
			if (t.power <= 0) continue;
			attacker = checkPower(-1, t, attacker);
		}
		
		// 선정된 공격자의 공격력 수치 변경? 및 최근 공격 정보 수정
		int tIdx = isWall[attacker.y][attacker.x];
		turrets.get(tIdx).power += R + C;
		turrets.get(tIdx).recentlyAttack = k;
		
	}

	private static Turret checkPower(int flag, Turret chk, Turret trg) {
		// TODO Auto-generated method stub
		// flag는 1또는 -1로, 대소 비교를 하기 위한 확인용 변수
		// flag -1일때는 작은 것이 우선순위가 높게! (공격자 찾을때)
		// flag  1일때는 작은 것이 우선순위가 낮게! (타겟 찾을때)
		// 1 * (flag) < 2 * (flag) 에서 flag = -1 을 통해 죄측이 더 큰 값이 되도록 조절 가능
		
		// chk가 비교하고자 하는 값! trg는 지금까지 공격자 혹은 타겟의 값
		
		// 파워가 같다면!
		if (chk.power == trg.power) {
			// 최근 공격한 시점이 같다면!
			if (chk.recentlyAttack == trg.recentlyAttack) {
				// 같다면
				if (chk.y + chk.x == trg.y + chk.x) {
					if (chk.x * flag < trg.x * flag) {						
						return chk;
					} else return trg;
				}
				else {
					if ((chk.y + chk.x) * flag < (trg.y + trg.x) * flag) {
						return chk;
					} else return trg;
				}
			}
			// 다르다면 더 큰 것을 return
			else {
				// 최근에 공격한 것일수록 수치가 더 작게 나옴 (-1)일 경우
				if (chk.recentlyAttack * flag < trg.recentlyAttack * flag) {
					return chk;
				} else return trg;
			}
		} 
		// 다르다면 더 작은 것을 return
		else {
			if (chk.power * flag > trg.power * flag) {
				return chk;
			} else return trg;
		}
	}

}

class Turret {
	int y, x, power, recentlyAttack;
	boolean isUsed;
	
	Turret(int y, int x, int p, int r) {
		this.y = y;
		this.x = x;
		this.power = p;
		this.recentlyAttack = r;
		this.isUsed = false;
	}
}

//class Turret implements Comparable<Turret> {
//	int y, x, power, recentlyAttack;
//	
//	Turret(int y, int x, int p, int r) {
//		this.y = y;
//		this.x = x;
//		this.power = p;
//		this.recentlyAttack = r;
//	}
//	
//	@Override
//	public int compareTo(Turret o) {
//		return this.power - o.power;
//	}
//}