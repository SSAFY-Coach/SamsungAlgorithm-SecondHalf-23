"""
풀이시간 : 1시간 30분
- ?? 내 컴퓨터에서는 엄청 느렸는데 SWEA로 돌리니까 시간초과 없이 바로 통과..
"""

SIZE = 350
dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]

T = int(input())

for tc in range(T):
    N, M, K = map(int, input().split())
    live = [[0] * SIZE for _ in range(SIZE)]  # 활성화 되는 시점
    info = [[0] * SIZE for _ in range(SIZE)]  # 생명력 정보 (세포 번식할 때 필요)
    dead = [[False] * SIZE for _ in range(SIZE)]  # 죽었는가?

    # 초기 정보 처리
    for r in range(N):
        cells = list(map(int, input().split()))
        for c in range(M):
            live[150 + r][150 + c] = cells[c]
            info[150 + r][150 + c] = cells[c]

    # 배양 시작
    # O(300 * 2 * 350 * 350) = O(73,500,000)
    for now in range(1, K + 1):
        breed = [[0] * SIZE for _ in range(SIZE)]

        for r in range(SIZE):
            for c in range(SIZE):
                if live[r][c] == 0:
                    continue
                elif live[r][c] < now:  # 활성화 되어서 번식하는 상태
                    for d in range(4):
                        nr, nc = r + dr[d], c + dc[d]
                        # 빈 칸이거나 지금 세포가 더 생명력이 큰 경우
                        if not (0 <= nr < SIZE and 0 <= nc < SIZE):
                            continue
                        if live[nr][nc] == 0 and (breed[nr][nc] == 0 or info[nr][nc] < info[r][c]):
                            breed[nr][nc] = now + info[r][c]  # 지금 + 생명력 만큼의 시간에 활성화가 될 것이며
                            info[nr][nc] = info[r][c]  # 생명력을 기록해준다.
                if live[r][c] + info[r][c] == now:  # 죽은 상태
                    dead[r][c] = True

        for r in range(SIZE):
            for c in range(SIZE):
                if breed[r][c]:
                    live[r][c] = breed[r][c]

    # 살아있는 세포 개수
    answer = 0

    for r in range(SIZE):
        for c in range(SIZE):
            if not dead[r][c] and live[r][c]:
                answer += 1

    print(f"#{tc+1} {answer}")