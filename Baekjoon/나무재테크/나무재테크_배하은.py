"""
PyPy3 풀이

풀이시간 : 2시간 5분

[시간 초과로 두드려 맞은 과정]
1. 처음엔 각 위치를 heapq(Priority Queue)로 저장하고, 여름 + 가을 + 겨울을 합쳤음. => 시간초과
2. 3차원 list + 정렬로 바꿔봄
3. dictionary(HashMap)로 저장해서 key: 나무 나이, value: 나무 개수로 계산해봄. (근데 Python3으로 통과하려면 이렇게 해야하네..?)
3. 죽은 나무를 저장하던 3차원 list를 제거
4. 질문 게시판에서 deque()를 보고 적용 + sys.stdin.readline
   - 나무를 뺄때는 popleft, 번식으로 넣을땐 appendleft(1)
   - https://www.acmicpc.net/board/view/110170
   - deque로 번식으로 태어난 나이가 1인 나무만 계속 왼쪽에 넣어주면 정렬할 필요가 없음
5. deepcopy 제거

욕나옴
"""
import sys
from collections import deque

input = sys.stdin.readline
N, M, K = map(int, input().split())

# 같은 1×1 크기의 칸에 여러 개의 나무가 심어져 있을 수도 있다.
tree = [[deque() for _ in range(N)] for _ in range(N)]
food = [[5] * N for _ in range(N)]
robot = list(list(map(int, input().split())) for _ in range(N))

dr = [1, 1, 1, 0, 0, -1, -1, -1]
dc = [0, 1, -1, 1, -1, 0, 1, -1]


def in_range(r, c):
    return 0 <= r < N and 0 <= c < N


def spring():
    """
    나무가 자신의 나이만큼 양분을 먹고, 나이가 1 증가한다.
    각각의 나무는 나무가 있는 1×1 크기의 칸에 있는 양분만 먹을 수 있다.
    하나의 칸에 여러 개의 나무가 있다면, 나이가 어린 나무부터 양분을 먹는다.
    만약, 땅에 양분이 부족해 자신의 나이만큼 양분을 먹을 수 없는 나무는 양분을 먹지 못하고 즉시 죽는다.
    """
    grow = [[deque() for _ in range(N)] for _ in range(N)]  # 새로 자라난 나무

    for r in range(N):
        for c in range(N):
            if tree[r][c]:
                dead = 0  # 바로 food에 반영하면 겹침
                for age in tree[r][c]:
                    if food[r][c] >= age:
                        grow[r][c].append(age+1)
                        food[r][c] -= age
                    else:
                        dead += (age // 2)  # 여기서 '여름' 처리
                food[r][c] += dead
                # 1씩 자란 나무들 복붙
                tree[r][c] = grow[r][c]


def autumn():
    """
    나무가 번식한다. 번식하는 나무는 나이가 5의 배수이어야 하며, 인접한 8개의 칸에 나이가 1인 나무가 생긴다.
    상도의 땅을 벗어나는 칸에는 나무가 생기지 않는다.
    """
    for r in range(N):
        for c in range(N):
            for age in tree[r][c]:
                if age % 5 == 0:
                    for d in range(8):
                        nr, nc = r + dr[d], c + dc[d]
                        if 0 <= nr < N and 0 <= nc < N:
                            tree[nr][nc].appendleft(1)
            food[r][c] += robot[r][c]  # 여기서 '겨울' 처리


def get_answer():
    answer = 0

    for r in range(N):
        for c in range(N):
            answer += len(tree[r][c])

    print(answer)


def solution():
    for _ in range(M):
        x, y, z = map(int, input().split())
        tree[x-1][y-1].append(z)

    for _ in range(K):
        spring()
        autumn()
    get_answer()


solution()