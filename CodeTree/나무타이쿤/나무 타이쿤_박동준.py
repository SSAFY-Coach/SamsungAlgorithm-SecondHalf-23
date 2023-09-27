direction = { 
    1 : (0,1), 
    2 : (-1,1), 
    3 : (-1,0),
    4 : (-1,-1),
    5 : (0,-1),
    6 : (1,-1),
    7 : (1,0),
    8 : (1,1),
}

n, m = list(map(int, input().split()))
arr = [list(map(int, input().split())) for _ in range(n)]
move_pattern = [list(map(int,input().split())) for _ in range(m)]
# 영양제 투여 여부 리스트
nutrients = [[0] * n for _ in range(n)]
# 초기 영양제 위치는 고정 좌하단 4개 격자
nutrients[n-1][0],nutrients[n-2][0], nutrients[n-1][1], nutrients[n-2][1] = 1,1,1,1
# 영양제 이동
def move_nutrients(year):
    # 이동시, 좌표를 초과하는것을 방지하기 위해 나머지 연산 수행
    global nutrients
    new_nutrients = [[0] * n for _ in range(n)]
    dx,dy = direction[move_pattern[year][0]]
    move = min(move_pattern[year][1],10)
    rotate = []
    for i in range(n):
        for j in range(n):
            if nutrients[i][j] == 1:
                nx = (i + (dx * move)) % n
                ny = (j + (dy * move)) % n
                rotate.append((nx,ny))
                new_nutrients[nx][ny] = 1
                arr[nx][ny] += 1
    
    for i in range(n):
        for j in range(n):
            nutrients[i][j] = new_nutrients[i][j]


    return rotate


# 대각선 인접 방향 리브로수가 있으면 더 성장 대신 범위 초과는 count x
def grow(rotate):
    # 동시에 자라야함;;
    direction_key = [2,4,6,8]
    # 해당 좌표에 추가되는 값만 기록해줌
    new_height = [0] * len(rotate)
    for idx in range(len(rotate)):
        x,y = rotate[idx]
        for key in direction_key:
            nx = x + direction[key][0]
            ny = y + direction[key][1]
            if 0 <= nx < n and 0 <= ny < n and arr[nx][ny] != 0:
                new_height[idx] += 1
    
    for idx in range(len(rotate)):
        x,y = rotate[idx]
        arr[x][y] += new_height[idx]
    return

def cut_and_insert():
    # 배열의 2이상인곳의 값을 -2 해주고, 새로이 영양제를 투입한다.
    global nutrients
    for i in range(n):
        for j in range(n):
            if arr[i][j] >= 2 and nutrients[i][j] == 0:
                arr[i][j] -= 2
                nutrients[i][j] = 1
            elif arr[i][j] < 2 or nutrients[i][j] == 1:
                nutrients[i][j] = 0
    return

# m년만큼 실행
for year in range(m):
    # 이동
    rotate = move_nutrients(year)
    # rotate 정보에 따라 해당 좌표에 값에 맞게 투입 성장시킴
    grow(rotate)
    # 주입
    cut_and_insert()

result = 0
for i in range(n):
    for j in range(n):
        result += arr[i][j]

print(result)