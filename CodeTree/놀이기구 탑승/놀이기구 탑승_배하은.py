"""
풀이시간 : 30분
- 쉽다고 생각하고 조건을 마구 빠트렸다. 시간이 중요하긴 하지만 맞는게 더 중요하단다...^^
"""

N = int(input())
ride = [[0] * N for _ in range(N)]

dr = [-1, 1, 0, 0]
dc = [0, 0, -1, 1]

order = []  # 놀이기구 타는 순서
friends = [set() for _ in range(N*N+1)]  # 번호별 친구 정보


def in_range(r, c):
    return 0 <= r < N and 0 <= c < N


def friend_info():
    """
    친구 정보를 저장해둔다. 각 학생의 번호가 인덱스이고, 친구 정보는 set으로 저장한다.
    """
    for _ in range(N*N):
        me, f1, f2, f3, f4 = map(int, input().split())
        order.append(me)
        friends[me] = {f1, f2, f3, f4}


def cal_seat(r, c, me):
    """
    현재 자리에서 친구의 수와 빈칸의 수를 반환한다.
    :param r: 현재 행
    :param c: 현재 열
    :param me : 내 번호
    :return: 친구의 수, 빈칸의 수
    """
    f_cnt = 0  # 이 자리에서 격자 내의 친구의 수
    b_cnt = 0  # 이 자리에서 격자 내의 빈칸의 수

    for d in range(4):
        nr, nc = r + dr[d], c + dc[d]
        if in_range(nr, nc):
            if ride[nr][nc] == 0:
                b_cnt += 1
            elif ride[nr][nc] in friends[me]:
                f_cnt += 1

    return f_cnt, b_cnt


def take_seat():
    """
    모든 학생들의 자리를 찾아준다.
    :return:
    """

    for me in order:
        br, bc, bf, bb = N, N, 0, 0  # 가장 우선순위가 높은 행, 열, 친구의 수, 빈칸의 수

        for r in range(N):
            for c in range(N):
                # 비어있을 때만 앉을 수 있다.
                if ride[r][c] == 0:
                    f_cnt, b_cnt = cal_seat(r, c, me)
                    # Tuple 비교법. 원소 순서대로 비교한다.
                    # 행과 열은 작은 위치여야 하므로 -값으로 값을 반전시켰다.
                    # 친구가 더 많거나, 같으면 빈칸이 더 많거나, 같으면 행이 더 작거나, 같으면 열이 더 작은 경우에 업데이트
                    if (f_cnt, b_cnt, -r, -c) > (bf, bb, -br, -bc):
                        bf, bb, br, bc = f_cnt, b_cnt, r, c
        # 나의 위치를 찾았다.
        ride[br][bc] = me


def get_score():
    score = 0

    for r in range(N):
        for c in range(N):
            me = ride[r][c]
            f_cnt = 0
            for d in range(4):
                nr, nc = r + dr[d], c + dc[d]
                if in_range(nr, nc) and ride[nr][nc] in friends[me]:
                    f_cnt += 1

            score += 10 ** (f_cnt-1) if f_cnt > 0 else 0

    print(score)


def solution():
    friend_info()
    take_seat()
    get_score()


solution()