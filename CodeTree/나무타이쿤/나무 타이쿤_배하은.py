"""
풀이시간 : 30분
- 로직 5분
"""
N, M = map(int, input().split())
libro = list(list(map(int, input().split())) for _ in range(N))
dr = [0, -1, -1, -1, 0, 1, 1, 1]
dc = [1, 1, 0, -1, -1, -1, 0, 1]
# list로 만들면 마지막에 영양제를 맞지 않는 위치를 확인할때 in 의 연산이 O(N)이라 set으로 설정
nutri = {(N - 2, 0), (N - 2, 1), (N - 1, 0), (N - 1, 1)}


def move_nutri(d, p):
    """
    이동 방향의 경우 1번부터 8번까지 → ↗ ↑ ↖ ← ↙ ↓ ↘으로 주어지며 이동 칸 수만큼 특수 영양제가 이동하게 됩니다.
    격자의 모든 행,열은 각각 끝과 끝이 연결되어 있습니다.
    즉 격자 바깥으로 나가면 마치 지구가 둥근것처럼 반대편으로 돌아옵니다.
    :param d: 방향
    :param p: 거리
    """
    global nutri
    moved = set()

    for r, c in nutri:
        nr = (r + dr[d] * p) % N
        nc = (c + dc[d] * p) % N
        moved.add((nr, nc))

    # [Tip]
    # nutri.clear()
    # nutri = moved
    # 로 했었는데 어차피 nutri가 moved에 저장된 주소값을 갖게 되면서 참조가 끊어지므로 clear()는 불필요
    # 로직상 copy()를 하지 않아도 되긴 하지만 copy()를 하면 독립적으로 저장
    # 136ms -> 94ms
    nutri = moved.copy()


def insert_nutri():
    """
    특수 영양제는 1 x 1 땅에 있는 리브로수의 높이를 1 증가시키며,
    만약 해당 땅에 씨앗만 있는 경우에는 높이 1의 리브로수를 만들어냅니다.
    """
    for r, c in nutri:
        if libro[r][c]:
            libro[r][c] += 1
        else:
            libro[r][c] = 1


def grow_libro():
    """
    특수 영양제가 있는 땅의 리브로수는 높이가 1만큼 증가하고
    대각선으로 인접한 높이 1 이상의 리브로수의 개수 만큼 높이가 증가합니다.
    대각선으로 인접한 방향이 격자를 벗어나는 경우에는 세지 않습니다.
    """
    for r, c in nutri:
        cnt = 0
        for d in range(4):
            # → ↗ ↑ ↖ ← ↙ ↓ ↘
            nr, nc = r + dr[d*2+1], c + dc[d*2+1]
            if 0 <= nr < N and 0 <= nc < N and libro[nr][nc]:
                cnt += 1
        libro[r][c] += cnt


def buy_nutri():
    """
    특수 영양제를 투입한 리브로수를 제외하고
    높이가 2 이상인 리브로수는 높이 2를 베어서 잘라낸 리브로수로 특수 영양제를 사고,
    해당 위치에 특수 영양제를 올려둡니다.
    :return:
    """
    global nutri
    new_nutri = set()

    for r in range(N):
        for c in range(N):
            if (r, c) not in nutri and libro[r][c] >= 2:
                libro[r][c] -= 2
                new_nutri.add((r, c))

    nutri = new_nutri.copy()


def get_answer():
    answer = 0
    for r in range(N):
        answer += sum(libro[r])

    print(answer)


def solution():
    for _ in range(M):
        d, p = map(int, input().split())
        move_nutri(d-1, p)
        insert_nutri()
        grow_libro()
        buy_nutri()
    get_answer()


solution()