answer = 0
N, M = map(int, input().split())
nums = list(list(map(int, input().split())) for _ in range(N))
# 모든 블록을 포함하는 정사각형으로 만들까 하다가 그냥 최소한의 크기로 만들었다.
blocks = [
    [[1 for _ in range(4)]], [[1] for _ in range(4)], [[1] * 2 for _ in range(2)],
    [[1, 0], [1, 1], [0, 1]], [[0, 1, 1], [1, 1, 0]], [[0, 1], [1, 1], [1, 0]], [[1, 1, 0], [0, 1, 1]],
    [[1, 0], [1, 1], [1, 0]], [[1, 1, 1], [0, 1, 0]], [[0, 1], [1, 1], [0, 1]], [[0, 1, 0], [1, 1, 1]],
    [[1, 0], [1, 0], [1, 1]], [[1, 1, 1], [1, 0, 0]], [[1, 1], [0, 1], [0, 1]], [[0, 0, 1], [1, 1, 1]],
    [[0, 1], [0, 1], [1, 1]], [[1, 0, 0], [1, 1, 1]], [[1, 1], [1, 0], [1, 0]], [[1, 1, 1], [0, 0, 1]]
]

# 무려 5중 for문
for row in range(N):
    for col in range(M):
        # 위의 19개 블록을 하나씩 계산한다.
        for block in blocks:
            summation = 0
            # 블록의 세로, 가로 길이
            block_r, block_c = len(block), len(block[0])
            # 블록이 2차원 영역을 넘어서진 않는가?
            if row + block_r <= N and col + block_c <= M:
                for r in range(block_r):
                    for c in range(block_c):
                        # 0이면 더해지지 않아야 하기 때문에 곱했다.
                        summation += nums[row + r][col + c] * block[r][c]
                answer = max(answer, summation)

print(answer)



"""
하드코딩 말고 백트랙킹을 활용해서 풀이할 수 있다.
왜냐면 테트리스 블록은 어떤 모양을 하든 4개의 정사각형이 이어져있는 모양이기 때문이다.

---------------

dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]
visited = []

def make_block(cnt, summation):
    global answer
    # 주어진 블록은 어떤 모양을 하던 4개의 사각형으로 이어져있다.(아..!)
    if cnt == 4:
        answer = max(answer, summation)
        return
    
    # 아직 사각형 4개가 붙지 않았다면 계속 만들어야한다.
    for r, c in visited:
        for d in range(4):
            nr, nc = r + dr[d], c + dc[d]

            if 0 <= nr < N and 0 <= nc < M and (nr, nc) not in visited:
                visited.append((nr, nc))
                make_block(cnt+1, summation + nums[nr][nc])
                # 가장 최근에 넣은 위치가 자연스럽게 빠진다.
                visited.pop()


for row in range(N):
    for col in range(M):
        visited.append((row, col))
        make_block(1, nums[row][col])
        visited.pop()

print(answer)
"""