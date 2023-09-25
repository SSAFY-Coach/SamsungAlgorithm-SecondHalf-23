"""
[시계방향 회전]
- 풀이시간 : 2시간 (로직 구성 25분)
- 핵심 부분 :
1) 참가자의 정보 저장 방식 ( 위치 = tuple, 탈출 여부 = boolean)
2) (참가자, 출구)를 포함하는 가장 작은 정사각형 만들기
3) 벽을 저장한 grid에 더해 출구, 참가자도 시계방향 회전
"""
answer = 0
N, M, K = map(int, input().split())
maze = list(list(map(int, input().split())) for _ in range(N))

runners = list()  # 참가자 위치
escaped = [False] * M  # 참가자 탈출 여부
for _ in range(M):
    row, col = map(int, input().split())
    runners.append([row-1, col-1])  # 기준 좌표 (1,1)

e_row, e_col = map(lambda x: int(x) - 1, input().split())  # 출구 위치(exit_row, exit_col)

# 상하좌우(참가자 다음 위치 선정 우선순위)
dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]


def in_range(r, c):
    return 0 <= r < N and 0 <= c < N


def move():
    """
    모든 참가자들이 동시에 움직인다.
    :return:
    """
    global answer  # answer을 수정하려면 전역 변수임을 명시

    for idx in range(M):
        if escaped[idx]:
            continue
        r_row, r_col = runners[idx]  # 기존 참가자 위치 (runner_row, runner_col)
        dist = abs(e_row - r_row) + abs(e_col - r_col)  # 기존 위치와 출구의 최단 거리
        n_row, n_col = r_row, r_col  # 새롭게 움직일 위치(안 바뀔 수도 있으니 기존 위치 저장) (new_row, new_col)

        for d in range(4):
            t_row, t_col = r_row + dr[d], r_col + dc[d]  # 상-하-좌-우 순으로 새로운 위치를 탐색 (temp_row, temp_col)
            t_dist = abs(e_row - t_row) + abs(e_col - t_col)
            # [⚠주의] 탈출 처리 빼먹음
            if t_row == e_row and t_col == e_col:
                n_row, n_col = e_row, e_col
                escaped[idx] = True
                break
            # 범위 내에 있는가? 빈칸인가? 최단 거리가 기존보다 더 짧은가?
            if in_range(t_row, t_col) and not maze[t_row][t_col] and dist > t_dist:
                n_row, n_col, dist = t_row, t_col, t_dist

        # 참가자 위치 변경
        runners[idx] = [n_row, n_col]
        # 새 위치와 기존의 위치가 다르다면 움직인 거리에 1 추가
        answer += 1 if n_row != r_row or n_col != r_col else 0


def rotate_clock(s_row, s_col, diff, row, col):
    # 출구와 참가자를 회전하는 함수 따로 작성

    # 정사각형 기준점 빼서 0, 0으로 맞추기
    row -= s_row
    col -= s_col

    # [⚠주의] 시계 방향 외우고, r과c 원본을 왼쪽, 오른쪽에 두는 것에 대해서도 정리
    # 원래의 위치가 어떻게 바뀌는지를 반환한다. 새위치[c][N-1-r] = 원본[r][c]
    row, col = col, diff-row

    # 다시 정사각형 기준점 추가하기
    row += s_row
    col += s_col

    return [row, col]


def rotate_maze():
    """
    미로를 회전시킨다.
    참가자가 없는 경우는 미리 처리했다.
    """
    global e_row, e_col  # 출구의 위치가 바뀐다.

    # 가장 작고 가장 위, 왼쪽에 있는 정사각형의 정보
    # 여러개 중 row와 col은 0에 가까운 것, edge는 작은 것을 골라야 한다.
    row, col, diff = N, N, N

    for idx in range(M):
        # 탈출한 참가자와는 정사각형을 만들 수 없다.
        if escaped[idx]:
            continue
        r_row, r_col = runners[idx]

        # 참가자와 출구의 행, 열 차이 중 더 큰 것이 정사각형 한 변의 길이 (now_diff)
        n_diff = max(abs(e_row - r_row), abs(e_col - r_col))

        # 같아도 행,열이 더 0에 가까울 수 있기 때문에 같은 것도 포함해야한다.
        if n_diff <= diff:
            # 기존의 정사각형 기준점(위+왼)보다 (0,0)에 가까운가?
            # 출구와 참가자 중 더 아래와 더 위에 있는 행의 값을 선별한다.
            down, up = max(e_row, r_row), min(e_row, r_row)
            right, left = max(e_col, r_col), min(e_col, r_col)

            # 기준점은 큰 값의 행에서 정사각형의 한 변의 길이만큼 뺀 것인데
            # 만약 음수가 나온다면 0으로 조정해준다.
            n_row = down - n_diff if down - n_diff >= 0 else 0
            n_col = right - n_diff if right - n_diff >= 0 else 0
            if diff > n_diff or (diff == n_diff and (n_row, n_col) < (row, col)):
                row, col = n_row, n_col
                diff = n_diff

    # 시계방향으로 90도 회전해야한다.
    # 1. 정사각형 크기 만큼의 2차원 배열을 만든다.
    edge = diff + 1
    # [⚠주의] 2차원 배열을 하나만 쓰면 얼마나 복잡할까?
    copied = [[0] * edge for _ in range(edge)]
    rotated = [[0] * edge for _ in range(edge)]

    for r in range(edge):
        for c in range(edge):
            copied[r][c] = maze[row+r][col+c]

    # 3. 새로 만든 2차원 배열을 90도 회전시킨다. 이때 -1한다.(음수가 되지 않게 조심)
    for r in range(edge):
        for c in range(edge):
            rotated[c][diff-r] = copied[r][c] - 1
            if rotated[r][c] < 0:
                rotated[r][c] = 0

    # 4. 정사각형 기준점을 반영해 기존의 maze 배열에 복사 붙여넣기 한다.
    for r in range(edge):
        for c in range(edge):
            maze[row+r][col+c] = rotated[r][c]

    # 5. 참가자와 출구도 정사각형 내에 있다면 회전 시켜야 한다.
    # [⚠주의] edge와 diff를 구분함으로써 부등호의 범위가 바뀌었는데 반영을 하다 말았음..
    if row <= e_row < row + edge and col <= e_col < col + edge:
        e_row, e_col = rotate_clock(row, col, diff, e_row, e_col)

    for idx in range(M):
        r_row, r_col = runners[idx]
        if not escaped[idx] and row <= r_row < row + edge and col <= r_col < col + edge:
            runners[idx] = rotate_clock(row, col, diff, r_row, r_col)


def solution():
    for _ in range(K):
        move()
        if all(escaped):  # 모든 참가자가 탈출했다면 미로를 회전시킬 수 없다.
            break
        rotate_maze()

    print(answer)
    print(e_row+1, e_col+1)


solution()