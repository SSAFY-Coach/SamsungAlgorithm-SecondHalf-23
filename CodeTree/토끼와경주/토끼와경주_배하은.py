"""
풀이시간 : 약 2시간
- 왕복 처리에서 또 헷갈림. 제대로 외우자!
"""
import heapq
import sys

input = sys.stdin.readline

# 전역 변수
total_score = 0
N, M = 0, 0
dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]

# 토끼 전역 변수
cnt = 0
index = dict()
score = []
dist = []
rabbits = []


def ready_race(info):
    for i in range(0, cnt):
        pid, d = info[i*2], info[i*2+1]
        # 토끼의 고유 아이디에 맞는 인덱스
        index[pid] = i
        dist.append(d)
        score.append(0)
        # 초기에는 점프 횟수, 행, 열 전부 0이다.
        heapq.heappush(rabbits, (0, 0, 0, 0, pid))


def race(K, S):
    """
    가장 우선순위가 높은 토끼 멀리 보내주기 : O(K * 4)
    가장 우선순위가 높은 토끼에 점수 더해주기
    :param K: 턴 횟수
    :param S: 점수
    :return:
    """
    global total_score
    picked = [False] * cnt

    for _ in range(K):
        # 가장 우선순위가 높은 토끼를 뽑음
        #  1. 현재까지의 총 점프 횟수가 적은 토끼
        #  2. 현재 서있는 행 번호 + 열 번호가 작은 토끼
        #  3. 행 번호가 작은 토끼
        #  4. 열 번호가 작은 토끼
        #  5. 고유번호가 작은 토끼
        jump, _, r, c, pid = heapq.heappop(rabbits)
        idx = index[pid]
        picked[idx] = True  # K번 턴 안에서 뽑힌 적이 있다!

        best_row, best_col = -1, -1

        for d in range(4):
            nr = (r + dr[d] * dist[idx]) % (2*(N-1))
            nc = (c + dc[d] * dist[idx]) % (2*(M-1))

            # 왕복 거리 안에 있는데 N-1 또는 M-1 보다 크다면 원래 의도한 것과 반대 방향으로 N-1, M-1만큼 이동해야함
            if nr > N-1:
                nr = N - 1 - (nr - (N - 1))
                # nr = 2*(N-1) - nr
            if nc > M-1:
                nc = M - 1 - (nc - (M - 1))
                # nc = 2*(M-1) - nc

            # (행 번호 + 열 번호가 큰 칸, 행 번호가 큰 칸, 열 번호가 큰 칸)
            if (nr+nc, nr, nc) > (best_row+best_col, best_row, best_col):
                best_row, best_col = nr, nc

        # 뽑힌 토끼 외에 전부 점수를 받는다는 건, 뽑힌 토끼에서 점수를 빼는 것과 동일하다.
        score[idx] -= best_row + best_col + 2  # (0, 0) 기준이기 때문에
        total_score += best_row + best_col + 2

        # 이동한 위치 반영해서 PQ에 다시 넣어야됨
        heapq.heappush(rabbits, (jump+1, best_row+best_col, best_row, best_col, pid))

    s_row, s_col, s_pid = -1, -1, -1  # 점수를 유일하게 못 받을 토끼의 행, 열, 고유번호

    for jump, rc, r, c, pid in rabbits:
        # (현재 서있는 행 번호 + 열 번호가 큰 토끼, 행 번호가 큰 토끼, 열 번호가 큰 토끼, 고유번호가 큰 토끼)
        # K번의 턴 동안 한번이라도 뽑혔던 적이 있던 토끼 중 가장 우선순위가 높은 토끼를 골라야만 함에 꼭 유의
        if picked[index[pid]] and (rc, r, c, pid) > (s_row+s_col, s_row, s_col, s_pid):
            s_row, s_col, s_pid = r, c, pid

    score[index[s_pid]] += S


def get_best_rabbit():
    for i in range(cnt):
        score[i] += total_score

    print(max(score))


def solution():
    global N, M, cnt
    Q = int(input())
    for _ in range(Q):
        input_data = list(map(int, input().split()))
        order = input_data[0]

        if order == 100:
            N, M, cnt = input_data[1:4]
            ready_race(input_data[4:])
        elif order == 200:
            race(input_data[1], input_data[2])
        elif order == 300:
            pid, L = input_data[1], input_data[2]
            dist[index[pid]] *= L
        else:
            get_best_rabbit()

solution()