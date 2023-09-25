"""
풀이시간 못 잼

사람들 전부 위치를 저장한 set()을 만들고
그 set()안에서 combination으로 인원수를 0에서 최대까지 늘려가면서
각각 계단에 타고 내려갈 사람을 정해놓고
시뮬레이션 돌리면 되지 않을까?
"""
from itertools import combinations


class Stair:
    def __init__(self, r, c, k):
        self.r = r
        self.c = c
        self.k = k


T = int(input())


def stair_down(stair, cnt, wait, down, now):
    # 대기열에서 계단으로 오른 사람 처리 (반복문 range 때문에)
    out = 0

    for idx in range(cnt):
        # 아직 계단에 도착하지도 못 했다. 이후 사람도 마찬가지이니 종료
        if wait[idx] > now:
            break
        else:
            for i in range(3):
                # 계단에 들어갈 자리가 있는가?
                if down[i] == 0:
                    # 있다면 계단 내려가는데 걸리는 시간 넣어주고 뺄 개수 추가
                    down[i] = stair.k
                    out += 1
                    break
            # 지금 아무데도 못 들어갔다면 뒤의 사람도 마찬가지
            if out == 0:
                break

    # 여기서 실제로 제거한다.
    for _ in range(out):
        wait.pop(0)


def move(group1, group2):
    # 이번 조합에서 걸리는 시간
    now_time = 0

    # 내려가기까지 남은 시간
    stair1_down = [0, 0, 0]
    stair2_down = [0, 0, 0]

    # 각 계단의 대기열
    stair1_wait = []
    stair2_wait = []

    # 대기열에 이동 시간 추가
    for r, c in group1:
        stair1_wait.append(abs(stair[0].r - r) + abs(stair[0].c - c))

    for r, c in group2:
        stair2_wait.append(abs(stair[1].r - r) + abs(stair[1].c - c))

    # 빨리 도착한 사람 순서대로 계단에 오르도록
    stair1_wait.sort()
    stair2_wait.sort()

    # 멈추는 조건은 while문 안에 작성
    while True:
        # 이제 남은 대기 인원
        stair1_cnt, stair2_cnt = len(stair1_wait), len(stair2_wait)

        # 정답 비교
        if stair1_cnt == 0 and stair2_cnt == 0 and sum(stair1_down) == 0 and sum(stair2_down) == 0:
            return now_time

        # 계단 내려가는 중인 애들 처리
        for i in range(3):
            if stair1_down[i] > 0:
                stair1_down[i] -= 1
            if stair2_down[i] > 0:
                stair2_down[i] -= 1

        stair_down(stair[0], stair1_cnt, stair1_wait, stair1_down, now_time)
        stair_down(stair[1], stair2_cnt, stair2_wait, stair2_down, now_time)
        now_time += 1


for tc in range(T):
    N = int(input())
    grid = list(list(map(int, input().split())) for _ in range(N))
    pos = set()  # 사람 위치를 (r, c) 로 저장. set으로 한 이유는 조합 때문에
    stair = list()  # 계단의 정보 저장
    answer = 9999999999  # 최대값 계산하기 귀찮아서

    # 2차원 배열에서 사람들의 위치와 계단의 위치를 찾아 저장.
    # 실제로 2차원 배열을 타고 움직이지 않으니까 grid는 필요없음
    for r in range(N):
        for c in range(N):
            if grid[r][c] == 1:
                pos.add((r, c))
            elif grid[r][c] > 1:
                stair.append(Stair(r, c, grid[r][c]))

    # 인원 수
    cnt = len(pos)

    # [주의] 모든 사람이 한 계단에 쏠리는 경우도 포함해야한다.
    # 계단이 2개라서 그룹도 2개로 나눌건데, 이미 만들어진 그룹 활용하고 싶어서 range를 반만 사용
    for i in range(0, cnt // 2 + 1):
        # 첫 번째 계단으로 갈 사람들 그룹의 조합
        # ex) i = 1이면, [((0, 1),), ((1, 2),), ((4, 0),), ((2, 1),), ((2, 3),), ((0, 2),)]
        comb = list(combinations(pos, i))
        for g1 in comb:
            # set의 차집합을 사용해 두 번째 계단으로 갈 사람 그룹을 구한다.
            g2 = pos.difference(g1)
            answer = min(answer, move(g1, g2))

            # 수가 다른 경우에만 반대로 뒤집는다. combination 함수 덜 사용하려고.
            if len(g1) != len(g2):
                answer = min(answer, move(g2, g1))

    print(f"#{tc + 1} {answer}")
