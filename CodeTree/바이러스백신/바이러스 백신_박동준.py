'''
m 개의 병원 고르기
골라진 경우 조합함 -> 돌림
m의 갯수 10개 ssap 가능
n -> 50 2500 ->
'''

from collections import deque

n, m = list(map(int, input().split()))
arr = [list(map(int, input().split())) for _ in range(n)]

hospital = []  # 병원 좌표 배열 만들기
virus = 0  # 바이러스 갯수 체크

combis = []

for i in range(n):
    for j in range(n):
        if arr[i][j] == 2:
            hospital.append((i, j))
        elif arr[i][j] == 0:
            virus += 1


# 바이러스 갯수를 하고, 방문처리를 통해 활성화 되었던 바이러스 갯수만 체크함

def combination(cur, idx, combi):
    global m
    if cur == m:
        x = combi[:]
        combis.append(x)
        return

    for i in range(idx, len(hospital)):
        combi.append(i)
        combination(cur + 1, i + 1, combi)
        combi.pop()
        combination(cur, i + 1, combi)


combination(0, 0, [])


def spread():
    return


# 각 조합별 시간 체크
result = 1e9  # 결과값, 없는 경우에는 -1이기 때문에
for com in combis:
    que = deque()
    visit = [[0] * n for _ in range(n)]  # 방문 배열 체크

    for num in com:
        x, y = hospital[num]
        visit[x][y] = 1
        que.append([x, y])
    # 큐에 추가 및 방문처리
    # 벽을 제외한 지역에는 백신이 공급됨.
    count = 0
    time = 0
    dx = [1, 0, -1, 0]
    dy = [0, -1, 0, 1]
    while que:
        # 종료조건, 100의 경우 배열의 최대 크기로 산정함, 1.5 * n 초가 최대
        if count == virus or time >= 100:
            break

        length = len(que)
        for i in range(length):
            x, y = que.popleft()
            for k in range(4):
                nx = x + dx[k]
                ny = y + dy[k]
                if 0 <= nx < n and 0 <= ny < n and visit[nx][ny] == 0 and (arr[nx][ny] == 0 or arr[nx][ny] == 2):
                    que.append([nx, ny])
                    visit[nx][ny] = 1
                    if arr[nx][ny] != 2:
                        count += 1

        time += 1
        if count == virus:
            result = min(result, time)
            break

if result == 1e9:
    if virus == 0 and count == 0:
        print(0)
    else:
        print(-1)
else:
    print(result)

