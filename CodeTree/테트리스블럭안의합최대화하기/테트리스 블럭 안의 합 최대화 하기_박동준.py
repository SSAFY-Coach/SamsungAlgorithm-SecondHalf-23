# -- 4방향 탐색용 핵심 function
dx = [0,0,1,-1]
dy = [-1,1,0,0]
max_number = 0
def recur(x,y,step,number):
    global max_number
    if step == 4:
        if number > max_number:
            max_number = number
        return

    for i in range(4):
        nx = dx[i] + x
        ny = dy[i] + y
        if 0 <= nx < n and 0 <= ny < m and visit[nx][ny] == 0:
            visit[nx][ny] = 1
            recur(nx,ny,step +1, number + arr[nx][ny])
            visit[nx][ny] = 0

def find_plus(x,y):
    global max_number
    # 아래, 위, 오른쪽, 왼쪽
    info = [0,0,0,0]
    all_sum = arr[x][y]
    for i in range(4):
        nx = dx[i] + x
        ny = dy[i] + y
        if 0 <= nx < n and 0 <= ny < m:
            info[i] = arr[nx][ny]
    all_sum += sum(info)

    for i in range(4):
        if all_sum - info[i] > max_number:
            max_number = all_sum - info[i]
# -- input
n,m = map(int, input().split())
arr = [ list(map(int, input().split())) for _ in range(n)]

# -- 각 지점 탐색
# 각 모양별 한 지점별 탐색 수 20, n,m 최대 200
# 시간복잡도 n^2

visit = [[0]*m for _ in range(n)]
for i in range(n):
    for j in range(m):
        visit[i][j] = 1
        recur(i,j,1,arr[i][j])
        visit[i][j] = 0
        # ㅗ,ㅓ,ㅏ,ㅜ 찾기
        find_plus(i,j)


print(max_number)