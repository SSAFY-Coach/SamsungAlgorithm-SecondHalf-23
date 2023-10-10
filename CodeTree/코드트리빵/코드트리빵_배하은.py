"""
풀이시간 : 50분
- 1번 행동에서 사람 -> 편의점으로 가려면 편의점에 도착했을 때 어느 방향으로 가야할지 따로 기록필요
=> 편의점 -> 사람 으로 방향을 바꾸면 사람 근처 4방향 중 가장 가까운걸 고르면 되기 때문에 쉽다.
- BFS 결과인 2차원 배열을 탐색할 때, 도달할 수 없는 곳 때문에 값이 0 이상인 걸로 골라야한다.

"""
from collections import deque

N, M = map(int, input().split())
grid = list(list(map(int, input().split())) for _ in range(N))

# 상 좌 우 하
dr = [-1, 0, 0, 1]
dc = [0, -1, 1, 0]

OUT = [-1, -1]
people = [OUT for _ in range(M)]  # 처음엔 격자 밖에 있다.
arrive = [False] * M  # 편의점 도착 여부

blocked = [[False] * N for _ in range(N)]  # 지나갈 수 있는지 여부
goals = []  # 목표 편의점 위치
camp = []  # 베이스 캠프 위치

# 베이스 캠프 저장
for r in range(N):
    for c in range(N):
        if grid[r][c]:
            camp.append((r, c))
camp.sort()  # camp의 위치를 행, 열 기준으로 오름차순 정렬

# 편의점 저장
for _ in range(M):
    r, c = map(lambda x: int(x)-1, input().split())
    goals.append([r, c])


def in_range(r, c):
    return 0 <= r < N and 0 <= c < N


def bfs(r, c):
    visited = [[0] * N for _ in range(N)]  # 거리 비교
    visited[r][c] = 1
    q = deque([(r, c)])

    while q:
        row, col = q.popleft()

        for d in range(4):
            next_row = row + dr[d]
            next_col = col + dc[d]

            # 범위 내에 있는지, 아직 방문하지 않았는지, 갈 수 없는 곳은 아닌지 확인
            if in_range(next_row, next_col) and not visited[next_row][next_col] and not blocked[next_row][next_col]:
                visited[next_row][next_col] = visited[row][col] + 1
                q.append((next_row, next_col))

    return visited


def move_people():
    for idx in range(M):
        if not arrive[idx] and people[idx] != OUT:
            distance = bfs(*goals[idx])
            r, c = people[idx]
            move_row, move_col, move_dist = r, c, N*N  # 이동할 위치와 거리

            for d in range(4):
                nr, nc = r + dr[d], c + dc[d]
                if in_range(nr, nc) and 0 < distance[nr][nc] < move_dist:
                    move_row, move_col, move_dist = nr, nc, distance[nr][nc]

            people[idx] = [move_row, move_col]


def check_arrive():
    for idx in range(M):
        if not arrive[idx] and people[idx] == goals[idx]:
            arrive[idx] = True
            # 이제 이 편의점을 지나갈 수 없다.
            goal_row, goal_col = goals[idx]
            blocked[goal_row][goal_col] = True


def start_move(now):
    if now < M:
        distance = bfs(*goals[now])
        camp_row, camp_col, camp_dist = N, N, N*N

        for r, c in camp:
            if 0 < distance[r][c] < camp_dist:
                camp_row, camp_col, camp_dist = r, c, distance[r][c]

        people[now] = [camp_row, camp_col]  # 격자 위로 올라간다.
        blocked[camp_row][camp_col] = True  # 이제 이 베이스캠프를 지나갈 수 없다.


def solution():
    now = 0
    while True:
        move_people()
        check_arrive()
        start_move(now)
        if all(arrive):
            print(now+1)
            break
        now += 1

solution()