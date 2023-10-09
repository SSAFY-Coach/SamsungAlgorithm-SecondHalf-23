"""
풀이시간 : 1시간 12분
- 공격 대상자를 정해진 순간 공격력을 뺐더니 공격 과정에서 의도와 다르게 전개됨(갈 수 없는 곳이 되어버림)
"""
from collections import deque
import sys
input = sys.stdin.readline

N, M, K = map(int, input().split())
power = list(list(map(int, input().split())) for _ in range(N))  # 포탑의 공격력
record = [[0] * M for _ in range(N)]  # 포탑이 공격한 시점
involved = []  # 공격에 휘말렸는가

# 우 / 하 / 좌 / 상
dr = [0, 1, 1, 1, 0, -1, -1, -1]
dc = [1, -1, 0, 1, -1, 1, 0, -1]


def attacker(turn):
    """
    O(N*M)
    부서지지 않은 포탑 중 가장 약한 포탑이 공격자로 선정됩니다.
    핸디캡이 적용되어 N+M만큼의 공격력이 증가됩니다.
    가장 약한 포탑은 다음의 기준으로 선정됩니다.

    1. 공격력이 가장 낮은 포탑이 가장 약한 포탑입니다.
    2. 가장 최근에 공격한 포탑이 가장 약한 포탑입니다. (모든 포탑은 시점 0에 모두 공격한 경험이 있다고 가정하겠습니다.)
    3. 각 포탑 위치의 행과 열의 합이 가장 큰 포탑이 가장 약한 포탑입니다.
    4. 각 포탑 위치의 열 값이 가장 큰 포탑이 가장 약한 포탑입니다.
    :param turn: 몇 번째 턴
    :return 공격자 행, 열
    """
    level, recent, row, col = 5001, 0, 0, 0

    for r in range(N):
        for c in range(M):
            if power[r][c]:
                if (-power[r][c], record[r][c], r+c, c) > (-level, recent, row+col, col):
                    level, recent, row, col = power[r][c], record[r][c], r, c

    power[row][col] += N + M
    involved[row][col] = True
    record[row][col] = turn

    return row, col


def victim(ar, ac):
    """
    O(N*M)
    위에서 선정된 공격자는 자신을 제외한 가장 강한 포탑을 공격합니다.
    가장 강한 포탑은 위에서 정한 가장 약한 포탑 선정 기준의 반대이며, 다음과 같습니다.

    1. 공격력이 가장 높은 포탑이 가장 강한 포탑입니다.
    2. 공격한지 가장 오래된 포탑이 가장 강한 포탑입니다. (모든 포탑은 시점 0에 모두 공격한 경험이 있다고 가정하겠습니다.)
    3. 각 포탑 위치의 행과 열의 합이 가장 작은 포탑이 가장 강한 포탑입니다.
    4. 각 포탑 위치의 열 값이 가장 작은 포탑이 가장 강한 포탑입니다.
    :param ar: 공격자 행
    :param ac: 공격자 열
    :return 공격 대상의 행, 열
    """
    level, recent, row, col = 0, 1001, N, M

    for r in range(N):
        for c in range(M):
            if ar == r and ac == c:
                continue
            if power[r][c]:
                if (-power[r][c], record[r][c], r + c, c) < (-level, recent, row + col, col):
                    level, recent, row, col = power[r][c], record[r][c], r, c

    involved[row][col] = True


    return row, col


def lazer(ar, ac, vr, vc):
    """
    상하좌우의 4개의 방향으로 움직일 수 있습니다. 부서진 포탑이 있는 위치는 지날 수 없습니다.
    가장자리에서 막힌 방향으로 진행하고자 한다면, 반대편으로 나옵니다.
    레이저 공격은 공격자의 위치에서 공격 대상 포탑까지의 최단 경로로 공격합니다.
    만약 그러한 경로가 존재하지 않는다면 (2) 포탄 공격을 진행합니다.
    만약 경로의 길이가 똑같은 최단 경로가 2개 이상이라면, 우/하/좌/상의 우선순위대로 먼저 움직인 경로가 선택됩니다.

    최단 경로가 정해졌으면, 공격 대상에는 공격자의 공격력 만큼의 피해를 입히며, 피해를 입은 포탑은 해당 수치만큼 공격력이 줄어듭니다.
    또한 공격 대상을 제외한 레이저 경로에 있는 포탑도 공격을 받게 되는데, 이 포탑은 공격자 공격력의 절반 만큼의 공격을 받습니다. (절반이라 함은 공격력을 2로 나눈 몫을 의미합니다.)
    :param ar: 공격자 행
    :param ac: 공격자 열
    :param vr: 공격 대상 행
    :param vc: 공격 대상 열
    :return: 레이저 공격이 가능한지 여부
    """
    route = [[[-1, -1] for _ in range(M)] for _ in range(N)]  # 이전에 어디서 왔는지 좌표 기록
    q = deque([(ar, ac)])

    while q:
        r, c = q.popleft()

        for d in range(4):
            nr = (r + dr[d*2]) % N
            nc = (c + dc[d*2]) % M

            if power[nr][nc] == 0 or route[nr][nc] != [-1, -1]:
                continue

            route[nr][nc] = [r, c]
            q.append((nr, nc))

    if route[vr][vc] == [-1, -1]:
        return False
    else:
        damage = power[ar][ac]
        power[vr][vc] = power[vr][vc] - damage if power[vr][vc] >= damage else 0

        damage //= 2
        pr, pc = route[vr][vc]  # prev_row, prev_col
        while (pr, pc) != (ar, ac):
            power[pr][pc] = power[pr][pc] - damage if power[pr][pc] >= damage else 0
            involved[pr][pc] = True
            pr, pc = route[pr][pc]

        return True


def bomb(ar, ac, vr, vc):
    """
    공격 대상에 포탄을 던집니다. 공격 대상은 공격자 공격력 만큼의 피해를 받습니다.
    추가적으로 주위 8개의 방향에 있는 포탑도 피해를 입는데, 공격자 공격력의 절반 만큼의 피해를 받습니다. (절반이라 함은 공격력을 2로 나눈 몫을 의미합니다.)

    공격자는 해당 공격에 영향을 받지 않습니다.
    만약 가장자리에 포탄이 떨어졌다면, 위에서의 레이저 이동처럼 포탄의 추가 피해가 반대편 격자에 미치게 됩니다.

    :param ar: 공격자 행
    :param ac: 공격자 열
    :param vr: 공격 대상 행
    :param vc: 공격 대상 열
    """
    damage = power[ar][ac]
    power[vr][vc] = power[vr][vc] - damage if power[vr][vc] >= damage else 0

    damage //= 2

    for d in range(8):
        nr = (vr+dr[d]) % N
        nc = (vc+dc[d]) % M
        if (nr, nc) == (ar, ac):
            continue
        involved[nr][nc] = True
        power[nr][nc] = power[nr][nc] - damage if power[nr][nc] >= damage else 0


def fix():
    """
    공격이 끝났으면, 부서지지 않은 포탑 중 공격과 무관했던 포탑은 공격력이 1씩 올라갑니다.
    공격과 무관하다는 뜻은 공격자도 아니고, 공격에 피해를 입은 포탑도 아니라는 뜻입니다.
    """
    for r in range(N):
        for c in range(M):
            if power[r][c] and not involved[r][c]:
                power[r][c] += 1


def get_answer():
    # 남아있는 포탑 중 가장 강한 포탑의 공격력을 출력
    answer = 0

    for r in range(N):
        answer = max(answer, max(power[r]))

    print(answer)


def alive():
    cnt = 0

    for r in range(N):
        for c in range(M):
            if power[r][c]:
                cnt += 1

    return cnt


def solution():
    global involved

    for turn in range(K):
        involved = [[False] * M for _ in range(N)]
        if alive() == 1:
            break

        ar, ac = attacker(turn+1)  # O(100)
        vr, vc = victim(ar, ac)  # O(100)

        # 공격을 할 때에는 레이저 공격을 먼저 시도하고, 만약 그게 안 된다면 포탄 공격을 합니다.
        if not lazer(ar, ac, vr, vc):
            bomb(ar, ac, vr, vc)

        fix()

    get_answer()

solution()