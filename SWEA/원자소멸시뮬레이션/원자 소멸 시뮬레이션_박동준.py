'''
원자수 1000개
배열 크기 2000 * 2000 -> 400만이라서 모든 배열 탐색을 하면 바로 시초

충돌
- 인접하고 방향이 서로 반대라면 0,1 // 2,3 의 경우
- 향하는 좌표가 같은 경우
- 부딛힌 에너지 합산만큼 방출
- 동시이동 시작

sol
- 이동한 후의 좌표를 계산해야함
- 좌표 기준으로 탐색 -> 단 모든 배열 탐색이 아니라, directory 기준으로
    case1. 제일 먼저 가는 방향에 있는 인접 원자 좌표 확인, 방향이 0,1 // 2,3의 경우 바로 에너지 합산 후 제거
    case2. 이동 할 좌표 계산, 이동하는 좌표에 값이 있다면, 충돌 후 없어짐 리스트에 담아두고 에너지 합 계산 및 제거
    - 이후 남은 좌표들의정보를 원 객체에 업데이트
'''

direction = [(1, 0), (-1, 0), (0, -1), (0, 1)]
T = int(input())


def reverse_crash(n1, n2):
    flag = False
    if n1 == 0 and n2 == 1:
        flag = True
    elif n1 == 1 and n2 == 0:
        flag = True
    elif n1 == 2 and n2 == 3:
        flag = True
    elif n1 == 3 and n2 == 2:
        flag = True
    else:
        flag = False

    return flag


for t in range(1, T + 1):
    directory = {}
    n = int(input())
    info = [list(map(int, input().split())) for _ in range(n)]
    for atom in info:
        # (방향,에너지)
        # 하나의 좌표에는 충돌 아니면 하나 또는 없음
        directory[(atom[0], atom[1])] = (atom[2], atom[3])

    # 동시에 이동임 어쨋든 기준은 1초
    # 0.5초가 되는걸 어떻게 찾을것인가
    result = 0
    # 시간은 최대 2000초임
    # 원자수 1000개







'''
    for idx in directory:
        atoms = directory[idx]
        x,y = idx   # 원래 좌표
        for d in range(len(atoms)):
            nx = x + direction[atoms[d][0]][0]
            ny = y + direction[atoms[d][0]][1]
            if directory.get((nx,ny),[]) and -1000 <= nx <= 1000 and -1000 <= ny <= 1000:
                # case1 체크
                check_crush = [0] * len(directory[(nx,ny)])
                next_atoms = directory[(nx,ny)]
                for i in range(len(next_atoms)):
                    if reverse_crash(next_atoms[i][0],d):
                        result += atoms[d][1] + next_atoms[i][1]
                        check_crush[i] = 1
                directory[(nx,ny)] = []
                for i in range(len(check_crush)):
                    if check_crush[i] == 0:
                        directory[(nx,ny)].append(atoms[i])
            elif not(-1000 <= nx <= 1000 and -1000 <= ny <= 1000):
                if not -1000 <= nx <= 1000:
                    nx = x
                if not -1000 <= nx <= 1000:
                    ny = y
                



'''
