"""
풀이시간 : 1시간 2분
- 새로운 격자의 크기를 어떻게 잡을지가 헷갈렸다.
"""

R, C, K = map(int, input().split())
R, C = R-1, C-1

grid = list(list(map(int, input().split())) for _ in range(3))


def by_row(r_len):
    global grid
    new_grid = [[] for _ in range(r_len)]  # 새로 만든 결과
    max_col = 0  # 가장 긴 열의 길이 찾기

    for r in range(r_len):
        pair = []  # (개수, 숫자) 쌍을 저장
        cnt = [0] * 101  # 숫자가 몇번 등장했는지

        for c in grid[r]:
            cnt[c] += 1

        for num in range(1, 101):  # 0은 제외
            if cnt[num]:
                pair.append((cnt[num], num))

        pair.sort()  # [(1, 1), (1, 2), (1, 3)]
        pair_len = len(pair)  # 이번 행에서 몇개의 쌍인지
        max_col = max(max_col, 2 * pair_len)  # 나중에 0을 채우기 위해 가장 긴 열의 길이 저장

        for i in range(pair_len):  # 정렬한 결과물 추가
            count, number = pair[i]
            new_grid[r] += [number, count]

    grid = []

    for r in range(r_len):  # 빈칸에 0 채워주기
        pair_len = len(new_grid[r])
        if pair_len < max_col:
            new_grid[r] += [0] * (max_col - pair_len)

        # 복사 붙여넣기
        grid.append(new_grid[r])


def by_col(r_len, c_len):
    global grid
    # 행 기준으로 만들고 나중에 행 <-> 열 해주기
    new_grid = [[] for _ in range(c_len)]
    max_row = 0

    for c in range(c_len):
        pair = []
        cnt = [0] * 101
        for r in range(r_len):
            cnt[grid[r][c]] += 1

        for num in range(1, 101):  # 0은 제외
            if cnt[num]:
                pair.append((cnt[num], num))

        pair.sort()  # [(1, 1), (1, 2), (1, 3)]
        pair_len = len(pair)
        max_row = max(max_row, 2 * pair_len)

        for i in range(pair_len):
            count, number = pair[i]
            new_grid[c] += [number, count]

    for r in range(c_len):  # 빈칸에 0 채워주기
        pair_len = len(new_grid[r])
        if pair_len < max_row:
            new_grid[r] += [0] * (max_row - pair_len)

    cross_grid = [[0] * c_len for _ in range(max_row)]

    for r in range(c_len):
        for c in range(max_row):
            if new_grid[r][c] == 0:
                break
            cross_grid[c][r] = new_grid[r][c]

    grid = []
    for row in cross_grid:
        grid.append(row)


def solution():
    second = 0
    while second <= 100:
        r_len, c_len = len(grid), max(len(line) for line in grid)
        if r_len > R and c_len > C and grid[R][C] == K:
            break
        if r_len >= c_len:
            by_row(r_len)
        else:
            by_col(r_len, c_len)
        second += 1

    if second > 100:
        print(-1)
    else:
        print(second)


solution()
