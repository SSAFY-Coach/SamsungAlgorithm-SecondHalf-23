"""
[3차원 배열 + 방향이 전부 상하좌우 or 대각선인지?]
풀이시간 : 45분

"""

from copy import deepcopy

N, M, K = map(int, input().split())
grid = [[[] for _ in range(N)] for _ in range(N)]  # 3차원 배열
# 방향 d는 0부터 7까지 순서대로 ↑, ↗, →, ↘, ↓, ↙, ←, ↖ 의미합니다.
dr = [-1, -1, 0, 1, 1, 1, 0, -1]
dc = [0, 1, 1, 1, 0, -1, -1, -1]


def init():
    for _ in range(M):
        x, y, m, s, d = map(int, input().split())
        # 입력 값이 1 이상 부터
        grid[x-1][y-1].append([m, s, d])


def move():
    global grid
    # grid에서 바로 움직이면 이미 움직인 원자가 또 움직일 수 있다.
    moved = [[[] for _ in range(N)] for _ in range(N)]

    for row in range(N):
        for col in range(N):
            # 해당 위치에 원자가 있다면
            if grid[row][col]:
                # 원자가 여러개일 수 있다.
                for m, s, d in grid[row][col]:
                    nr = (row + dr[d] * s) % N
                    nc = (col + dc[d] * s) % N
                    moved[nr][nc].append([m, s, d])

    # 깊은 복사
    grid = deepcopy(moved)


def composite():
    """
    이동이 모두 끝난 뒤에 하나의 칸에 2개 이상의 원자가 있는 경우에는 합성이 일어납니다.

    [기준]
    - 질량은 합쳐진 원자의 질량에 5를 나눈 값입니다.
    - 속력은 합쳐진 원자의 속력에 합쳐진 원자의 개수를 나눈 값입니다.
    - 방향은 합쳐지는 원자의 방향이 모두 상하좌우 중 하나이거나 대각선 중에 하나이면,
      각각 상하좌우의 방향을 가지며 그렇지 않다면 대각선 네 방향의 값을 가집니다.
    - 편의상 나눗셈 과정에서 생기는 소숫점 아래의 수는 버립니다.
    """

    for row in range(N):
        for col in range(N):
            # a. 같은 칸에 있는 원자들은 각각의 질량과 속력을 모두 합한 하나의 원자로 합쳐집니다.
            cnt = len(grid[row][col])
            if cnt > 1:
                mass, speed = 0, 0
                # 상하좌우 / 대각선
                even, odd = 0, 0

                for m, s, d in grid[row][col]:
                    mass += m
                    speed += s
                    # 방향 계산에서 착각함. 합쳐지는 원소의 수는 짝수, 홀수가 정해지지 않았음
                    # 그런데 합쳐지는 원소가 짝수일거라고 멋대로 생각하고,
                    # 방향의 합이 짝이면 모두 상하좌우 or 대각선, 홀이면 섞인 것이라고 판단함.
                    if d % 2 == 0:
                        even += 1
                    else:
                        odd += 1

                grid[row][col] = []

                # b. 이후 합쳐진 원자는 4개의 원자로 나눠집니다.
                # c. 나누어진 원자들은 모두 해당 칸에 위치하고 질량과 속력, 방향은 기준을 따라 결정됩니다.
                mass //= 5
                if mass == 0:
                    # d. 질량이 0인 원소는 소멸됩니다.
                    continue
                speed //= cnt

                if odd == cnt or even == cnt:
                    # 모두 상하좌우 or 모두 대각선. 상하좌우 저장
                    for d in range(4):
                        grid[row][col].append([mass, speed, d*2])
                else:
                    # 상하좌우랑 대각선이 섞였다. 대각선 4방향을 저장
                    for d in range(4):
                        grid[row][col].append([mass, speed, d*2+1])


def get_answer():
    answer = 0

    for row in range(N):
        for col in range(N):
            # 해당 위치에 원자가 있다면
            if grid[row][col]:
                # 원자가 여러개일 수 있다.
                for m, _, _ in grid[row][col]:
                    answer += m

    print(answer)


def solution():
    init()
    for _ in range(K):
        move()
        composite()
    get_answer()


solution()