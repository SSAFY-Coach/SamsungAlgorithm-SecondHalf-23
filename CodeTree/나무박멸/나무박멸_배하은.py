"""
풀이시간 41분
- 나무가 아예 없을 경우를 빼먹었다... 만약 이걸 테케에 넣어주지 않았다면?
- 내가 생각을 해냈어야 했느데..
- 그냥 노가다
"""

from copy import deepcopy

N, M, K, C = map(int, input().split())
tree = list(list(map(int, input().split())) for _ in range(N))
killer = [[0] * N for _ in range(N)]  # 제초제 몇 년 남았는지

# 시계 반대방향으로 대각선까지 전부 포함
# 상하좌우 = d*2, 대각선 = d*2 + 1
dr = [-1, -1, 0, 1, 1, 1, 0, -1]
dc = [0, -1, -1, -1, 0, 1, 1, 1]
answer = 0


def in_range(r, c):
    return 0 <= r < N and 0 <= c < N


def grow():
    """
    인접한 네 개의 칸 중 나무가 있는 칸의 수만큼 나무가 성장합니다. 성장은 모든 나무에게 동시에 일어납니다.
    :return:
    """

    for r in range(N):
        for c in range(N):
            if tree[r][c] > 0:
                cnt = 0
                for d in range(4):
                    nr, nc = r + dr[d*2], c + dc[d*2]
                    if in_range(nr, nc) and tree[nr][nc] > 0:
                        cnt += 1

                tree[r][c] += cnt


def breed():
    """
    기존에 있었던 나무들은 인접한 4개의 칸 중 벽, 다른 나무, 제초제 모두 없는 칸에 번식을 진행합니다.
    이때 각 칸의 나무 그루 수에서 총 번식이 가능한 칸의 개수만큼 나누어진 그루 수만큼 번식이 되며, 나눌 때 생기는 나머지는 버립니다.
    번식의 과정은 모든 나무에서 동시에 일어나게 됩니다.
    :return:
    """
    global tree
    grid = deepcopy(tree)

    for r in range(N):
        for c in range(N):
            if tree[r][c] > 0:
                blank = 0
                for d in range(4):
                    nr, nc = r + dr[d*2], c + dc[d*2]
                    if in_range(nr, nc) and tree[nr][nc] == 0 and killer[nr][nc] == 0:
                        blank += 1

                # 귀찮지만 또 봐야지..
                for d in range(4):
                    nr, nc = r + dr[d*2], c + dc[d*2]
                    if in_range(nr, nc) and tree[nr][nc] == 0 and killer[nr][nc] == 0:
                        grid[nr][nc] += tree[r][c] // blank

    tree = deepcopy(grid)


def get_most():
    """
    각 칸 중 제초제를 뿌렸을 때 나무가 가장 많이 박멸되는 칸에 제초제를 뿌립니다.
    만약 박멸시키는 나무의 수가 동일한 칸이 있는 경우에는 행이 작은 순서대로, 만약 행이 같은 경우에는 열이 작은 칸에 제초제를 뿌리게 됩니다.
    :return: 제초제를 뿌릴 곳
    """
    cnt, row, col = 0, N, N

    for r in range(N):
        for c in range(N):
            if tree[r][c] > 0:
                now_cnt = tree[r][c]
                for d in range(4):
                    for step in range(1, K+1):
                        # 대각선으로 뻗어나가기
                        nr, nc = r + dr[d*2+1] * step, c + dc[d*2+1] * step
                        if in_range(nr, nc) and tree[nr][nc] > 0:
                            now_cnt += tree[nr][nc]
                        else:
                            break
                # 개수가 더 많거나, 행,열이 더 작거나
                if cnt < now_cnt or (cnt == now_cnt and (row, col) > (r, c)):
                    cnt = now_cnt
                    row, col = r, c

    return row, col


def decrease_killer():
    """
    제초제가 뿌려진 칸에는 c년만큼 제초제가 남아있다가 c+1년째가 될 때 사라지게 됩니다.
    제초제가 뿌려진 곳에 다시 제초제가 뿌려지는 경우에는 새로 뿌려진 해로부터 다시 c년동안 제초제가 유지됩니다.
    :return:
    """
    for r in range(N):
        for c in range(N):
            if killer[r][c]:
                killer[r][c] -= 1


def spread_killer(row, col):
    """
    나무가 없는 칸에 제초제를 뿌리면 박멸되는 나무가 전혀 없는 상태로 끝이 나지만,
    나무가 있는 칸에 제초제를 뿌리게 되면 4개의 대각선 방향으로 k칸만큼 전파되게 됩니다.

    단 전파되는 도중 벽이 있거나 나무가 아예 없는 칸이 있는 경우,
    그 칸 까지는 제초제가 뿌려지며 그 이후의 칸으로는 제초제가 전파되지 않습니다.
    :param row: 제초제 뿌릴 행
    :param col: 제초제 뿌릴 열
    :return:
    """
    global answer

    # 초기 위치
    answer += tree[row][col]
    tree[row][col] = 0
    killer[row][col] = C

    for d in range(4):
        for step in range(1, K + 1):
            nr, nc = row + dr[d * 2 + 1] * step, col + dc[d * 2 + 1] * step
            if in_range(nr, nc):
                killer[nr][nc] = C
                if tree[nr][nc] > 0:
                    answer += tree[nr][nc]
                    tree[nr][nc] = 0
                else:
                    break


def solution():
    for _ in range(M):
        grow()
        breed()
        r, c = get_most()
        if r == N and c == N:
            break
        decrease_killer()
        spread_killer(r, c)

    print(answer)


solution()