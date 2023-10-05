"""
풀이시간 : 40분
- 병원에도 백신이 퍼질 수 있다는걸 처리하지 않아서 디버깅함
"""
from collections import deque
from itertools import combinations

IMPOSSIBLE = 987654321

N, M = map(int, input().split())
city = list(list(map(int, input().split())) for _ in range(N))  # 0 바이러스, 1 벽, 2 병원
hospitals = list()  # 병원 위치 저장 = [(r, c), (r, c)...]
virus = 0  # 바이러스 갯수
answer = IMPOSSIBLE  # 바이러스를 전부 없애는데 걸리는 최소 시간. 불가능하면 -1

dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]


def init():
    """
    병원의 위치와 바이러스의 총 개수를 저장한다.
    """
    global virus
    for r in range(N):
        for c in range(N):
            if city[r][c] == 2:
                hospitals.append((r, c))
            elif city[r][c] == 0:
                virus += 1


def bfs(vaccine_hostpitals):
    """
    병원 조합을 파라미터로 받아 한번에 덱에 넣고 BFS를 탐색한다.
    :param vaccine_hostpitals:
    :return:
    """
    global answer
    visited = [[0] * N for _ in range(N)]

    for hr, hc in vaccine_hostpitals:
        # 병원들은 1로 시작
        visited[hr][hc] = 1

    q = deque(list(vaccine_hostpitals))
    kill_virus = 0  # 제거한 바이러스의 수

    while q:
        r, c = q.popleft()

        for d in range(4):
            nr, nc = r + dr[d], c + dc[d]
            if 0 <= nr < N and 0 <= nc < N and not visited[nr][nc] and city[nr][nc] != 1:
                q.append((nr, nc))
                visited[nr][nc] = visited[r][c] + 1
                # 바이러스와 병원이어도 둘 다 덱에 넣고, 시간을 추가해주는 것은 같아서
                # 벽이 아닌경우를 처리하고, 바이러스면 죽인 바이러스의 개수 추가
                if city[nr][nc] == 0:
                    kill_virus += 1

    if kill_virus == virus:
        # 바이러스를 다 죽였다면 비교한다.
        clear_virus_time = 1  # 바이러스를 다 죽이는데 걸린 시간 찾기

        for r in range(N):
            for c in range(N):
                # 그냥 max 함수로 비교했더니 백신을 처음에 못 받은 병원에 도달한 것도 계산함
                if city[r][c] == 0:
                    clear_virus_time = max(clear_virus_time, visited[r][c])

        answer = min(answer, clear_virus_time-1)  # 병원에서 1을 포함해서 출발해서 다시 빼줘야함


def solution():
    init()
    for vaccine_hospitals in combinations(hospitals, M):
        bfs(vaccine_hospitals)

    if answer != IMPOSSIBLE:
        print(answer)
    else:
        print(-1)

solution()