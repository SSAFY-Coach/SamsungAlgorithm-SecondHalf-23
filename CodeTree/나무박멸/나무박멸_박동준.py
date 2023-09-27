# - input 및 필요 배열 생성
n,m,k,c = list(map(int, input().split()))
arr = [list(map(int, input().split())) for _ in range(n)]
spray = [[0] * n for _ in range(n)]
count_deleted_wood = [[0] * n for _ in range(n)]
dx,dy = [-1,1,0,0],[0,0,-1,1]
# 칸 수 만큼 나무 성장 fn
def grow_wood(x,y):
    if arr[x][y] == 0 or arr[x][y] == -1:
        return 0
    count = 0
    #x,y 좌표 기준 4방향의 이동 가능 칸 여부 확인
    for i in range(4):
        nx = x + dx[i]
        ny = y + dy[i]
        # 벽이 아니고, 비어있는 칸이 아니라면
        if 0<= nx < n and 0 <= ny < n and arr[nx][ny] != -1 and arr[nx][ny] != 0:
            count += 1
    return count

# 주변 번식 -> 값이 있는 경우 더하고 나머지는 버림
def add_wood():
    # 번식이 될 칸에 대한 정보는 원 배열에 마지막에 더해져야함
    # 제초제에 대한 영역 역시 포함되어야함
    update_arr = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if arr[i][j] == 0 or arr[i][j] == -1:
                continue
            count = 0
            # 값이 있는 경우라면,4 방향중 0인 값이 있는곳을 찾는다
            for d in range(4):
                nx = i + dx[d]
                ny = j + dy[d]
                # 조건 : 범위 내에 있고, 나무가 있는 곳에서 이동한 곳이 비어있는곳이고, 제초제가 뿌려지지 않은 곳이라면
                if 0 <= nx < n and 0 <= ny < n and arr[nx][ny] == 0 and spray[nx][ny] == 0:
                    count += 1
            
            for d in range(4):
                nx = i + dx[d]
                ny = j + dy[d]
                if 0 <= nx < n and 0 <= ny < n and arr[nx][ny] == 0 and spray[nx][ny] == 0:
                    update_arr[nx][ny] += arr[i][j]//count
    

    for i in range(n):
        for j in range(n):
            arr[i][j] += update_arr[i][j]
    return

# 제초제 뿌리기
def find_kill_wood_rotate():
    max_count = 0
    x,y = 0,0
    # 탐색하고 최대 값의 좌표 갱신
    dx = [-1,1,-1,1]
    dy = [-1,-1,1,1]

    for i in range(n):
        for j in range(n):
            if arr[i][j] == 0 or arr[i][j] == -1:
                continue
            count = arr[i][j]
            for d in range(4):
                nx = i + dx[d]
                ny = j + dy[d]
                for kk in range(k): # 이게 맞나..?
                    # k의 범위만큼 찾기
                    if 0 <= nx < n and 0 <= ny < n:
                        # 0 또는 -1이면 그 방향은 멈춰야함
                        if arr[nx][ny] == -1 or arr[nx][ny] == 0:
                            break
                        else:
                            # 카운트를 더해주고, 다음 좌표값 계산을 위해 더해줌
                            count += arr[nx][ny]
                            nx += dx[d]
                            ny += dy[d]
            # 4방향에 대한 탐색이 끝나면,최대 값인 경우 갱신시켜줌
            if max_count < count:
                x,y = i,j
                max_count = count
    # 제초시키고 배열 표시 시킴
    spray[x][y] = c
    arr[x][y] = 0
    for d in range(4):
        nx = x + dx[d]
        ny = y + dy[d]
        for _ in range(k):
            if 0 <= nx < n and 0 <= ny < n:
                if arr[nx][ny] == -1:
                    break
                elif arr[nx][ny] == 0:
                    spray[nx][ny] = c
                else:
                    spray[nx][ny] = c
                    arr[nx][ny] = 0
                    nx += dx[d]
                    ny += dy[d]
    return max_count

# m 년동안 진행 
result = 0
for _ in range(m):
    # 성장
    for i in range(n):
        for j in range(n):
            arr[i][j] += grow_wood(i,j)
    add_wood()
    # 제초제를 뿌리기전 이미 뿌려져있는 제초약들의 연도 감소
    for i in range(n):
        for j in range(n):
            if spray[i][j] != 0:
                spray[i][j] -= 1
    result += find_kill_wood_rotate()

print(result)