from collections import deque
import sys

N, M, K = list(map(int, input().split()))
power = [list(map(int, input().split())) for _ in range(N)]
# 최단거리 탐색용 배열

# 공격된 좌표 정보
attacked = [[0] * M for _ in range(N)]
# 최근 공격한 정보
attacker_time = [[0] * M for _ in range(N)]
result = 0
# 우하좌상 최우선 이동
dx = [0, 1, 0, -1]
dy = [1, 0, -1, 0]


# 약한공격자 찾기, 길이에 따라 최근 공격정보를 저장하는 attack에 최근 턴이 적힌
def find_worst():
    rotate = []
    min_number = 1e9
    for i in range(N):
        for j in range(M):
            if power[i][j] < min_number and power[i][j] != 0:
                rotate = []
                rotate.append((i, j))
                min_number = power[i][j]
            if power[i][j] == min_number and power[i][j] != 0:
                rotate.append((i, j))
    # 찾고, 가장 최근에 공격한 포탑 확인
    last_attack_time = -1
    # count 동일 시간이 있는지
    last_attack_tower = []
    for i in range(len(rotate)):
        x, y = rotate[i]
        if attacker_time[x][y] > last_attack_time:
            last_attack_time = attacker_time[x][y]
            last_attack_tower = []
            last_attack_tower.append((x, y))
        elif attacker_time[x][y] == last_attack_time:
            last_attack_tower.append((x, y))

    # 합이 가장 큰 포탑 그리고 열이 가장 큰 포탑
    last_attack_tower.sort(key=lambda x: (-sum(x), -x[1]))
    return last_attack_tower[0]


def find_big(lowx, lowy):
    # 동일 로직 추가
    rotate = []
    max_number = 0
    for i in range(N):
        for j in range(M):
            # 공격자로 선택된 좌표는 제외하고 찾음
            if i == lowx and j == lowy:
                continue
            if power[i][j] > max_number and power[i][j] != 0:
                rotate = []
                rotate.append((i, j))
                min_number = power[i][j]
            if power[i][j] == max_number and power[i][j] != 0:
                rotate.append((i, j))

    last_attack_time = 1e9
    last_attack_tower = []
    for i in range(len(rotate)):
        x, y = rotate[i]
        if attacker_time[x][y] < last_attack_time:
            last_attack_time = attacker_time[x][y]
            last_attack_tower = []
            last_attack_tower.append((x, y))
        elif attacker_time[x][y] == last_attack_time:
            last_attack_tower.append((x, y))

    last_attack_tower.sort(key=lambda x: (sum(x), x[1]))
    return last_attack_tower[0]


def bfs(x, y, ex, ey):
    global info
    # 가지치기
    que = deque()
    que.append([0, x, y])
    while que:
        dist, x, y = que.popleft()
        if y == ey and x == ex:
            return (x, y)

        for i in range(4):
            ny = (dy[i] + y) % M
            nx = (dx[i] + x) % N

            if visit[nx][ny] == -1 and power[nx][ny] != 0:
                visit[nx][ny] = x * M + y
                que.append([dist + 1, nx, ny])
    return (-1, -1)


def cannon(x, y, sx, sy, pw):
    # 해당 좌표 기준 다 둘러~
    power[x][y] -= pw
    attacked[x][y] = 1
    if power[x][y] < 0:
        power[x][y] = 0
    direction = [(1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (1, -1), (-1, -1), (-1, 1)]
    for dx, dy in direction:
        nx = (x + dx) % N
        ny = (y + dy) % M
        attacked[nx][ny] = 1
        if power[nx][ny] == 0:
            continue
        if nx == sx and ny == sy:
            continue
        power[nx][ny] -= pw // 2
        if power[nx][ny] < 0:
            power[nx][ny] = 0


attacked_turret_list = []
visit = []
for k in range(K):
    visit = [[-1] * M for _ in range(N)]
    attacked = [[0] * M for _ in range(N)]
    x, y = find_worst()
    attacker_time[x][y] = k + 1
    attacked[x][y] = 1
    power[x][y] += N + M
    pw = power[x][y]
    bx, by = find_big(x, y)
    info = []
    visit[x][y] = -2
    ane = bfs(x, y, bx, by)
    if ane != (-1, -1):
        nx, ny = ane[0], ane[1]
        while True:
            nx, ny, = visit[nx][ny] // M, visit[nx][ny] % M
            if visit[nx][ny] == -2:
                break
            attacked_turret_list.append((nx, ny))
            attacked[nx][ny] = 1
        damage = pw
        for nx, ny in attacked_turret_list:
            power[nx][ny] = max(0, power[nx][ny] - damage // 2)
        attacked[bx][by] = 1
        power[bx][by] = max(0, power[bx][by] - damage)
    else:
        cannon(bx, by, x, y, power[x][y])

    for i in range(N):
        for j in range(M):
            if attacked[i][j] == 0 and power[i][j] != 0:
                power[i][j] += 1

    for i in range(N):
        for j in range(M):
            if power[i][j] > result:
                result = power[i][j]
print(result)