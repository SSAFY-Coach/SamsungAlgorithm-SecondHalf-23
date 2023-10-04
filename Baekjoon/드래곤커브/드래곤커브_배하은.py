"""
풀이시간 : 1시간 20분
- 쉬운듯.. 어려운듯...
- 끝점을 기준으로 회전 시키는게 헷갈림.
"""

import sys

input = sys.stdin.readline

N = int(input())

# 우 상 좌 하
dr = [0, -1, 0, 1]
dc = [1, 0, -1, 0]

total = []  # 모든 커브의 좌표들을 저장한 곳

for _ in range(N):
    x, y, d, gen = map(int, input().split())
    # 시작 방향에 맞춰 시작점과 끝점 추가
    curves = [(y, x), (y + dr[d], x + dc[d])]

    for g in range(gen):
        end = len(curves) - 1
        end_row, end_col = curves[end]  # 끝점
        moved = []  # 회전해서 만들어진 커브들

        # end 기준 90도 회전
        for i in range(end):
            r, c = curves[i]
            # 끝점을 (0, 0) 으로 두고
            r -= end_row
            c -= end_col
            # 시계 방향 90도 회전
            r, c = c, -r
            # 끝점 다시 원복
            r += end_row
            c += end_col

            moved.append((r, c))

        # 마지막에 추가된 점이 끝점이 되도록
        moved.reverse()
        # 커브에 추가
        curves += moved
    total += curves

# 'in' 계산을 빠르게하고 중복을 제거하기 위해 set으로 변경
total = set(total)

answer = 0

for r, c in total:
    rec = 0
    # (r,c) 기준 오른쪽, 아래, 오른쪽 아래가 total에 존재한다면
    for dr, dc in [(1, 0), (0, 1), (1, 1)]:
        if 0 <= r + dr <= 100 and 0 <= c + dc <= 100 and (r + dr, c + dc) in total:
            rec += 1

    if rec == 3:
        answer += 1

print(answer)
