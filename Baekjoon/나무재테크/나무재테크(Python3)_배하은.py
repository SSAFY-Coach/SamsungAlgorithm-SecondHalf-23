"""
Python3로 통과하려면 dictionary를 사용해야 한다.
"""
import sys
from collections import defaultdict

input = sys.stdin.readline
N, M, K = map(int, input().split())

# 같은 1×1 크기의 칸에 여러 개의 나무가 심어져 있을 수도 있다.
# key : 나무 나이, value : 개수
# defaultdict => 해당 key값이 없으면 자동으로 빈 값(0, [] 등)을 생성하고 할당한다.
# 그냥 dict는 key가 있는지 확인해야함
tree = [[defaultdict(int) for _ in range(N)] for _ in range(N)]
food = [[5] * N for _ in range(N)]
robot = list(list(map(int, input().split())) for _ in range(N))

dr = [1, 1, 1, 0, 0, -1, -1, -1]
dc = [0, 1, -1, 1, -1, 0, 1, -1]


# 봄 + 여름
def spring():
    for r in range(N):
        for c in range(N):
            if tree[r][c]:
                new_born = defaultdict(int)  # 1만큼 자란 나무들
                dead = 0  # 죽은 나무들

                # 어린 나무들부터 보기 위해 key를 정렬
                for age in sorted(tree[r][c].keys()):
                    cnt = tree[r][c][age]  # 해당 나이의 나무 그루 수

                    if food[r][c] < age:  # 한 그루도 양분을 먹을 수 없다면
                        dead += (age // 2) * cnt
                        continue
                    if food[r][c] >= age * cnt:  # 해당 나이의 나무 전부 커버
                        food[r][c] -= age * cnt
                        new_born[age+1] = cnt
                    else:
                        # 전부는 아니지만 일부는 커버할 수 있다.
                        for i in range(cnt):
                            if food[r][c] >= age:
                                food[r][c] -= age
                                new_born[age+1] += 1
                            else:
                                dead += (age // 2) * (cnt - i)
                                break

                food[r][c] += dead
                tree[r][c] = new_born


# 가을 + 겨울
def autumn():
    for r in range(N):
        for c in range(N):
            # 가을 -> 5의 배수의 나이를 가진 나무가 번식한다.
            if tree[r][c]:
                for age in tree[r][c].keys():
                    # 나무의 나이가 5의 배수일 때에 만
                    if age % 5 == 0:
                        cnt = tree[r][c][age]
                        for d in range(8):
                            nr, nc = r + dr[d], c + dc[d]
                            if 0 <= nr < N and 0 <= nc < N:
                                tree[nr][nc][1] += cnt
            # 겨울 -> 땅에 양분 추가
            food[r][c] += robot[r][c]


def get_answer():
    answer = 0

    for r in range(N):
        for c in range(N):
            # values: 나무 개수만 세기 위함
            answer += sum(tree[r][c].values())

    print(answer)


def solution():
    for _ in range(M):
        x, y, z = map(int, input().split())
        tree[x-1][y-1][z] += 1

    for _ in range(K):
        spring()
        autumn()
    get_answer()


solution()