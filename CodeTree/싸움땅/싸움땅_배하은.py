"""
풀이시간 : 1시간
- 플레이어의 위치를 2차원 배열에 따로 저장할지, class 안에 값으로 같이 저장할지 항상 고민됨
"""

N, M, K = map(int, input().split())


class Player:
    def __init__(self, r: int, c: int, d: int, s: int, g: int):
        self.r = r
        self.c = c
        self.d = d
        self.s = s
        self.g = g

    def __repr__(self):
        return f"위치 : {self.r, self.c} / 방향 :{self.d} / 초기 : {self.s} / 총 : {self.g}"


players = []  # class Player 정보 저장
guns = [[[] for _ in range(N)] for _ in range(N)]  # 총은 3차원 배열에 저장(한 위치에 여러개 가능)
score = [0] * M  # 각 플레이어가 획득한 점수

# 지문에 맞는 방향 순서
dr = [-1, 0, 1, 0]
dc = [0, 1, 0, -1]


def init() -> None:
    # 총 저장
    for r in range(N):
        line = list(map(int, input().split()))
        for c in range(N):
            if line[c]:
                guns[r][c].append(line[c])

    # 플레이어 저장
    for _ in range(M):
        x, y, d, s = map(int, input().split())
        players.append(Player(x - 1, y - 1, d, s, 0))


def in_range(r: int, c: int) -> bool:
    """
    r, c가 격자 내에 있는지 판단한다
    :param r: 행
    :param c: 열
    :return: True or False
    """
    return 0 <= r < N and 0 <= c < N


def is_player_here(r: int, c: int) -> int:
    """
    r, c 위치에 다른 플레이어가 있는지 확인한다.
    참고로 여기로 이미 이동하고 나서 찾으면 안된다.
    :param r: 행
    :param c: 열
    :return: 다른 플레이어의 index 번호 or -1 (없음)
    """
    for other in range(M):
        if players[other].r == r and players[other].c == c:
            return other
    return -1


def move(idx: int) -> tuple[int]:
    """
    순서대로 움직이기 때문에, idx번 플레이어를 움직인다.
    :param idx: 플레이어의 인덱스 번호
    :return: 다음 위치
    """

    p = players[idx]
    nr, nc = p.r + dr[p.d], p.c + dc[p.d]
    if not in_range(nr, nc):
        p.d = (p.d + 2) % 4  # 범위 밖일 경우 반대로 이동
        nr, nc = p.r + dr[p.d], p.c + dc[p.d]

    return nr, nc


def get_gun(idx) -> None:
    """
    플레이어가 총이 있다면 내려놓는다.
    해당 위치에 총이 있다면 가장 강한 총을 얻는다.

    :param idx: 플레이어 번호
    """
    p = players[idx]
    if p.g:
        guns[p.r][p.c].append(p.g)
    if guns[p.r][p.c]:
        strong = max(guns[p.r][p.c])
        p.g = strong
        guns[p.r][p.c].remove(strong)


def fight(m, y):
    """
    나(me)와 상대(you)가 싸운다. 조건에 따라 승자, 패자 순으로 반환한다.
    :param m: 이동해온 플레이어 번호
    :param y: 원래있던 플레이어 번호
    :return: 승자 번호, 패자 번호
    """
    me, you = players[m], players[y]

    if (me.s+me.g, me.s) > (you.s+you.g, you.s):
        score[m] += (me.s+me.g) - (you.s + you.g)
        return m, y

    score[y] += (you.s+you.g) - (me.s+me.g)
    return y, m


def lose(l):
    """
    지금 위치에 총을 내려 놓고,
    다음에 갈 수 있는 칸을 찾은 뒤,
    그 위치로 이동해서,
    총을 얻는다.
    :param l: 패자 번호
    :return:
    """
    loser = players[l]
    if loser.g:
        guns[loser.r][loser.c].append(loser.g)
        loser.g = 0

    for dt in range(4):
        # 다음 칸으로 이동할 수 없을 경우 90' 회전해야함
        nr = loser.r + dr[(loser.d + dt) % 4]
        nc = loser.c + dc[(loser.d + dt) % 4]

        if in_range(nr, nc) and is_player_here(nr, nc) == -1:
            loser.d = (loser.d + dt) % 4
            loser.r, loser.c = nr, nc
            break

    get_gun(l)


def solution():
    init()
    for _ in range(K):
        for idx in range(M):
            nr, nc = move(idx)
            other = is_player_here(nr, nc)
            # nr, nc에서 다른 플레이어가 있는지 확인할 때 '나'를 포함하지 않도록
            # 찾고 나서 업데이트 해준다.
            players[idx].r, players[idx].c = nr, nc

            if other == -1:
                get_gun(idx)
            else:
                winner, loser = fight(idx, other)
                lose(loser)
                get_gun(winner)

    print(*score)


solution()
