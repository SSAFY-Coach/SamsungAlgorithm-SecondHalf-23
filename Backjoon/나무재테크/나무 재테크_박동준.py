# r,c 1부터 시작 주의
'''
처음 땅의 칸의 양분은 5
한칸에 여러개의 나무가 심어져있을 수 있다.

1. 나무 성장 봄
    - 나무 자신의 나이만큼 양분을 먹고 길이가 1 증가
    - 하나칸에 여러개의 나무가 있다면, 나이가 어린순부터 -> 오름차순 정렬
    - 양분이 부족하면 즉시 죽는다  -> 여러개일 경우에도 적용 되는가? -> ???
2. 양분 성장 여름
    - 죽은 나무의 나이를 2로 나눈 값(몫)이 칸에 양분으로 추가됨, 소수점 버림
3. 가을 - 번식
    - 번식하는 나무는 나이가 5의 배수인 나무만
    - 인접한 8개 칸에 -> 방향벡터 설정
    - 1인 나무가 생긴다
4. 겨울 - 양분추가
    -  양분의 양은 배열로 제공이 된다.
'''

# -- input
n, m, k = list(map(int, input().split()))
add_vita = [list(map(int, input().split())) for _ in range(n)]
vita = [ [5] * n for _ in range(n)]
info  = [list(map(int, input().split())) for _ in range(m)]
# -- 필요 배열
wood = [[[] for _ in range(n)] for _ in range(n)] # 나무 정보 입력용 비어있는 칸
# 번식할 나무가 있는 좌표정보를 저장함
grow_info = []
# 번식 정보를 저장 해 두고, 한번에 복사해서 wood에 옮겨주기위한 배열 필요없었음
# 배열로 할 필요가 있나..? obj 사용, 배열 저장 -> 정보 꺼내서 쓰고 영양분을 더해주고

for x,y,value in info:
    wood[x-1][y-1].append(value)

# -- fn --
def breeding(x,y):
    global change_wood
    # 방향벡터 설정
    direction = [(1,0), (-1,0), (0,1), (0,-1), (1,1), (1,-1), (-1,1), (-1,-1)]
    # 8방향에 길이 1인 나무 추가

    for i in range(len(wood[x][y])):
        if wood[x][y][i] % 5 != 0:
            continue
        for i in range(8):
            dx,dy = direction[i]
            nx = x + dx
            ny = y + dy
            if 0 <= nx < n and 0 <= ny < n:
                wood[nx][ny].append(1)

def grow_and_delete(x,y):
    # 지점에 있는 양분만큼
    vita_value = vita[x][y]
    wood_info = wood[x][y]  # 해당 좌표 배열
    added_vita = 0
    check_dead = [0] * len(wood_info)
    update_wood = []
    if not wood_info:
        return
    wood_info.sort()
    for i in range(len(wood_info)):
        if vita_value - wood_info[i] < 0:
            added_vita += wood_info[i] // 2
            check_dead[i] = 1
        else:
            vita_value -= wood_info[i]
            wood_info[i] += 1

    for i in range(len(wood_info)):
        if check_dead[i] == 0:
            update_wood.append(wood_info[i])
    wood[x][y] = update_wood
    # 비타민이 사용되었으면 업데이트
    vita[x][y] = vita_value + added_vita

def add():
    for i in range(n):
        for j in range(n):
            vita[i][j] += add_vita[i][j]

def count_wood():
    result = 0
    for i in range(n):
        for j in range(n):
            result += len(wood[i][j])
    return result

# -- 시작
for year in range(1,k+1):
    # print(wood)
    for i in range(n):
        for j in range(n):
            grow_and_delete(i,j)
    for i in range(n):
        for j in range(n):
            breeding(i,j)
    add()

print(count_wood())