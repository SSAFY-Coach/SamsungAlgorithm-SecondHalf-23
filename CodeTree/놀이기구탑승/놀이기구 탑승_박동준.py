# -- input ---

# 입력받고 조회만 하게됨.
n = int(input())
# 탑승 배열
arr = [[0] * n for _ in range(n)]
score_info =[0,1,10,100,1000]
like_info = {}
order = []

for i in range(n*n):
    like_info[i+1] = [0] * ((n*n)+1)
# 입력 정보에 따라 미리 값을 입력시킴

for _ in range(n*n):
    info = list(map(int, input().split()))
    order.append(info[0])
    for i in range(1,len(info)):
        like_info[info[0]][info[i]] = 1

#--- 필요함수 생성

# 내 번호에 따른 특정 좌표, 주변 4방향 탐색
def find_like_friend(x,y,now_number):
    dx = [0,0,1,-1]
    dy = [1,-1,0,0]
    count = 0
    zero_count = 0
    for i in range(4):
        nx = x + dx[i]
        ny = y + dy[i]
        if 0 <= nx < n and 0 <= ny < n:
            if arr[nx][ny] != 0 and like_info[now_number][arr[nx][ny]] == 1:
                count += 1
            if arr[nx][ny] == 0:
                zero_count += 1
    # return 값을 통해 sort 하여 값을 찾아냄, 최대 400개 sort 문제 x                
    return (count,zero_count,x,y)

def insert_student():
    # 인접한 4칸 탐색
    # n ^ 4
    for num in order:
        info = []
        for i in range(n):
            for j in range(n):
                if arr[i][j] == 0:
                    count_arr = find_like_friend(i,j,num)
                    info.append(count_arr)
        info.sort( key = lambda x : (-x[0],-x[1],x[2],x[3]))
        arr[info[0][2]][info[0][3]] = num
    # 좋아하는 친구 count
    return

insert_student()
result = 0
for i in range(n):
    for j in range(n):
        count,zero,x,y = find_like_friend(i,j,arr[i][j])
        result += score_info[count]

print(result)