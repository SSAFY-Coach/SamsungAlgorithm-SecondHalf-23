"""
풀이시간 : 2시간 20분
아 에바다...
"""
N = int(input())

green = [[0] * 4 for _ in range(6)]
blue = [[0] * 6 for _ in range(4)]
answer = 0


def print_green():
    print("[초록]")
    for line in green:
        print(*line)
    print()


def print_blue():
    print("[파랑]")
    for line in blue:
        print(*line)
    print()


def place_block(t, x, y):
    if t == 1:  # 1x1
        # 초록
        last = 5  # 마지막으로 안착할 위치를 찾음
        for i in range(5):
            if green[i + 1][y]:
                last = i
                break
        green[last][y] = 1

        # 파랑
        last = 5
        for i in range(5):
            if blue[x][i + 1]:
                last = i
                break
        blue[x][last] = 1
    elif t == 2:  # 1x2
        # 초록
        last = 5
        for i in range(5):
            if green[i + 1][y] or green[i + 1][y + 1]:
                last = i
                break
        green[last][y: y + 2] = [1, 1]

        # 파랑
        last = 4
        for i in range(4):
            if blue[x][i + 1] or blue[x][i + 2]:
                last = i
                break
        blue[x][last:last + 2] = [1, 1]
    else:  # 2x1
        # 초록
        last = 4
        for i in range(4):
            if green[i + 1][y] or green[i + 2][y]:
                last = i
                break
        green[last][y] = 1
        green[last + 1][y] = 1

        # 파랑
        last = 5
        for i in range(5):
            if blue[x][i + 1] or blue[x + 1][i + 1]:
                last = i
                break
        blue[x][last] = 1
        blue[x + 1][last] = 1


def erase_full():
    global answer

    # 초록
    for r in range(5, -1, -1):
        # 밑에서부터 보면서 비움
        if all(green[r]):
            green[r] = [0, 0, 0, 0]
            answer += 1

    blank = 0
    for r in range(5, -1, -1):
        if not any(green[r]):
            blank += 1
        elif blank:
            # 빈칸이 있다면 현재 행에서 blank만큼 떨어진 곳에 복붙
            for nr in range(r, -1, -1):
                green[nr+blank] = green[nr][:]
            blank = 0

    # 파랑
    # 우선 반시계 방향으로 90' 회전
    r_blue = [[0] * 4 for _ in range(6)]

    for r in range(4):
        for c in range(6):
            r_blue[c][3-r] = blue[r][c]

    # 다 찬거 터트리기
    for r in range(5, -1, -1):
        if all(r_blue[r]):
            r_blue[r] = [0, 0, 0, 0]
            answer += 1

    blank = 0
    for r in range(5, -1, -1):
        if not any(r_blue[r]):
            blank += 1
        elif blank:
            for nr in range(r, -1, -1):
                r_blue[nr+blank] = r_blue[nr][:]
            blank = 0

    # 원복
    for r in range(6):
        for c in range(4):
            blue[3-c][r] = r_blue[r][c]


def special_sector():
    # 초록
    cnt = 0
    for r in range(2):
        # 0, 1 행에 뭐라도 있다면 개수 추가
        if any(green[r]):
            cnt += 1

    if cnt:
        for r in range(5, 1, -1):
            # 개수만큼 아래로 밀어냄
            green[r] = green[r-cnt][:]

        for r in range(1, 1 - cnt, -1):
            # 특수 영역을 비움
            green[r] = [0, 0, 0, 0]

    # 파랑
    # 우선 반시계 방향으로 90' 회전
    cnt = 0
    r_blue = [[0] * 4 for _ in range(6)]

    for r in range(4):
        for c in range(6):
            r_blue[c][3 - r] = blue[r][c]

    for r in range(2):
        if any(r_blue[r]):
            cnt += 1

    if cnt:
        for r in range(5, 1, -1):
            r_blue[r] = r_blue[r-cnt][:]

        for r in range(1, 1-cnt, -1):
            r_blue[r] = [0, 0, 0, 0]

        # 원복
        for r in range(6):
            for c in range(4):
                blue[3 - c][r] = r_blue[r][c]


def print_answer():
    print(answer)
    block_cnt = 0

    for r in range(6):
        for c in range(4):
            block_cnt += green[r][c] + blue[c][r]

    print(block_cnt)


for _ in range(N):
    t, x, y = map(int, input().split())
    place_block(t, x, y)
    erase_full()
    special_sector()

print_answer()
