n,m,k = list(map(int, input().split()))
arr = [[[] for _ in range(n)] for _ in range(n)] # 여러 원자가 있기 때문에 배열로 생성함
for _ in range(m):
    x,y,w,s,d = list(map(int, input().split()))
    arr[x-1][y-1].append((w,s,d))
# 방향설정
direction = { 
    0 : (-1,0), 
    1 : (-1,1), 
    2 : (0,1),
    3 : (1,1),
    4 : (1,0),
    5 : (1,-1),
    6 : (0,-1),
    7 : (-1,-1),
}

def sum_atom_and_change_direction(atoms):
    # atoms는 특정 좌표에 있는 원소들의 정보가 들어있는 배열
    # 총질량의 나누기 5
    # 0이면 없어지는거임
    total_weight = 0
    total_speed = 0
    # 상하좌우일경우 0번 index count, 대각선일 경우 1 번 index count
    direction_check = [0,0]
    for k in range(len(atoms)):
        total_weight += atoms[k][0]
        total_speed += atoms[k][1]
        if atoms[k][2] % 2 == 0:
            # 모두 상하좌우, 아니면 대각선 하나일때,
            # 0,2,4,6
            direction_check[0] += 1
        else:
            # 대각선이동인 경우 홀수 direction을 가짐
            direction_check[1] += 1
    change_weight = total_weight // 5
    change_speed = total_speed // len(atoms)
    if change_weight == 0:
        # 이제는 없는 칸이 되기 때문에 빈 배열을 반환함
        return []
    # 상하좌우랑 대각선이 다 있는 상태라면
    new_atoms = []
    index = -1
    if direction_check[0] != 0 and direction_check[1] != 0:
        # 대각선
        index = 1
    else:
        # 아닌 경우는 수직
        index = 0
    
    for _ in range(4):
        new_atoms.append((change_weight,change_speed,index))
        index += 2
    return new_atoms


def move_atom():
    global n, arr
    # 이동시, 벗어나면 n으로 나눈 나머지 이용함 대각선 동일
    # 모두 이동시키고 한 좌표에서 이동한 원자가 기존의 배열에 영향이 끼치면 안됨
    new_arr = [[[] for _ in range(n)] for _ in range(n)]
    # 최대 약 13만번 연산 n^3
    for x in range(n):
        for y in range(n):
            for i in range(len(arr[x][y])):
                # 한 좌표에 있는 하나의 원자를 이동시킴
                w,s,d = arr[x][y][i]
                nx = (x + (direction[d][0]*s)) % n
                ny = (y + (direction[d][1]*s)) % n
                new_arr[nx][ny].append((w,s,d))
    # copy
    for x in range(n):
        for y in range(n):
            arr[x][y] = new_arr[x][y]

# k 초 만큼 이동함
for _ in range(k):
    # 1000 * 12만 최악의 케이스 될.. 려나..? 일단고..
    move_atom()
    for i in range(n):
        for j in range(n):
            if len(arr[i][j]) >= 2:
                arr[i][j] = sum_atom_and_change_direction(arr[i][j])

# 이동 이후 남은 원자 정보
result = 0
for i in range(n):
    for j in range(n):
        if arr[i][j]:
            for k in range(len(arr[i][j])):
                result += arr[i][j][k][0]

print(result)