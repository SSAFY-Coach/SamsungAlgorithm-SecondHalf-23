"""
[범위를 넘어가는 경우 반대편으로 돌아가는 반복을 빠르게 처리하기]
풀이시간 : 2시간 50분 (ㅁㅊ)
- 상어 왕복 처리하는게 너무 어려웠음...
- 왕복처리 참고 :
  - https://www.acmicpc.net/source/66567357
  - 왔다 갔다를 몇 번 하는지를 세고, 모듈러 했는데 범위 밖이면 위치 및 방향 재조정
"""


class Shark:
    def __init__(self, r=-1, c=-1, s=-1, d=-1, z=-1):
        self.r = r  # 행
        self.c = c  # 열
        self.s = s  # 속도
        self.d = d  # 방향
        self.z = z  # 크기

    def __repr__(self):
        return f"상어 위치 : {self.r, self.c} , 속도 : {self.s}, 방향 : {self.d}, 크기 : {self.z}"


answer = 0
R, C, M = map(int, input().split())
sharks = [Shark()]  # 상어의 정보 저장
grid = [[0] * C for _ in range(R)]  # 상어의 번호 저장
# 1번부터 시작해서
dr = [-1, 1, 0, 0]
dc = [0, 0, 1, -1]


def init():
    for idx in range(M):
        r, c, s, d, z = map(int, input().split())
        # grid는 (0,0)부터 시작하니 r, c에서 1씩 뺀다.
        sharks.append(Shark(r - 1, c - 1, s, d - 1, z))
        # grid는 0으로 채워져있기 때문에 첫 번째 상어가 0이면 구분이 안되므로 1부터 시작한다.
        # 0번에 가짜 상어 넣어뒀음
        grid[r - 1][c - 1] = idx + 1


def fishing(col):
    global answer
    for row in range(R):
        if grid[row][col]:
            num = grid[row][col]  # 상어 번호
            answer += sharks[num].z  # 상어 크기 더하기
            # 상어 사라짐
            sharks[num].r = -1
            sharks[num].c = -1
            break


def move_shark():
    global grid
    for idx in range(1, M + 1):
        # 이미 잡혔거나, 먹혔거나
        shark = sharks[idx]
        if (shark.r, shark.c) == (-1, -1):
            continue
        nr, nc = shark.r + dr[shark.d] * shark.s, shark.c + dc[shark.d] * shark.s

        # [왕복을 빠르게 처리하는 법!!]

        if nr < 0 or nr >= R:
            count = nr // (R-1)  # (R-1) 왕복을 홀수번 => 반대, 짝수번 => 같은 방향
            # nr >= R 이면 R-2 이내의 범위로 옮긴다.
            nr %= (R - 1)

            if count % 2 == 1:
                nr = R-1-nr  # 반대 방향으로 움직이고 있으므로
                sharks[idx].d += (-1) ** shark.d

        if nc < 0 or nc >= C:
            count = nc // (C-1)
            nc %= (C-1)
            if count % 2 == 1:
                nc = C - 1 - nc
                sharks[idx].d += (-1) ** shark.d

        sharks[idx].r, sharks[idx].c = nr, nc

    # 다 움직였으니 이제 grid에 올려보자.
    grid = [[0] * C for _ in range(R)]
    for idx in range(1, M + 1):
        shark = sharks[idx]
        if (shark.r, shark.c) == (-1, -1):
            continue

        # 위치에 아무 상어도 없다면 idx 자리
        if not grid[shark.r][shark.c]:
            grid[shark.r][shark.c] = idx
        else:
            # 있다면 더 큰애가 살아남고, 먹힌 애는 (-1, -1)
            other = grid[shark.r][shark.c]
            if sharks[other].z > shark.z:
                sharks[idx] = Shark()
            else:
                grid[shark.r][shark.c] = idx
                sharks[other] = Shark()


def solution():
    init()
    for col in range(C):
        fishing(col)
        move_shark()

    print(answer)


solution()


"""
상어 움직이는 이전 코드
shark = sharks[idx]
if (shark.r, shark.c) == (-1, -1):
    continue
nr, nc = shark.r + dr[shark.d] * shark.s, shark.c + dc[shark.d] * shark.s

if not (0 <= nr < R and 0 <= nc < C):
    if shark.d == 1:
        dist = shark.s % ((R-1)*2)
        nr = shark.r + dr[shark.d] * dist
        if nr < 0:
            if abs(nr) < R:
                nr = -nr
                sharks[idx].d = 2
            else:
                nr = (R-1) + (nr % -(R-1))
    elif shark.d == 2:
        dist = shark.s % ((R - 1) * 2)
        nr = shark.r + dr[shark.d] * dist
        if nr >= R:
            if nr <= 2*(R-1):
                nr = (R-1)*2 - nr
                sharks[idx].d = 1
            else:
                nr = nr % (2*(R-1))
    elif shark.d == 3:
        dist = shark.s % ((C-1)*2)
        nc = shark.c + dc[shark.d] * dist
        if nc >= C:
            if nc <= 2*(C-1):
                nc = (C-1)*2 - nc
                sharks[idx].d = 4
            else:
                nc = nc % (2*(C-1))
    elif shark.d == 4:
        dist = shark.s % ((C - 1) * 2)
        nc = shark.c + dc[shark.d] * dist
        if nc < 0:
            if abs(nc) < C:
                nc = -nc
                sharks[idx].d = 3
            else:
                nc = (C-1) + (nc % -(C-1))

sharks[idx].r, sharks[idx].c = nr, nc
"""