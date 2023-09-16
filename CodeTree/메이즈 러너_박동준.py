# -- input --
n,m,k = list(map(int, input().split()))
arr = [list(map(int, input().split())) for _ in range(n)]
human = [((map(int, input().split()))) for _ in range(m)]
exit = list(map(int, input().split()))
exit[0] -= 1
exit[1] -= 1
for i in range(m):
    x,y = human[i]
    human[i] = (x-1,y-1)

# -- 갱신되는 값을 저장할 list 생성, key : index number
distance = [0] * m  # 거리 계산 값 저장
move_count = [0] * m # 총 이동거리 저장
escape = [0] * m
# -- 현재 위치와 사람별 거리 계산
def calc_distance(x,y,exit_x,exit_y):
    return abs(exit_x - x) + abs(exit_y - y)

def start_distance_info_renewal(index,x,y,exit_x, exit_y):
    distance[index] = calc_distance(x,y,exit_x,exit_y)
# -- 사람별 이동 계산

def move(index):
    global human, exit, arr
    # 문제조건, 상하좌우 될경우 상하 우선
    x,y = human[index][0], human[index][1]
    dx = [-1,1,0,0]
    dy = [0,0,1,-1]
    for i in range(4):
        nx = x + dx[i]
        ny = y + dy[i]
        if 0 <= nx < n and 0 <= ny < n and arr[nx][ny] == 0 and distance[index] > calc_distance(nx,ny,exit[0],exit[1]):
            # 범위내에 있고 움직일 수 있고 이동한 위치가 거리상으로 현재 위치보다 거리가 짧을 때
            move_count[index] += 1
            if nx == exit[0] and ny == exit[1]:
                escape[index] = 1
            human[index] = (nx,ny)
            break

def calc_position(exit,human,length):
    for i in range(n):
        li = list(range(i,i+length+1))
        if exit in li and human in li:
            return i

def calc_length(exit_x, exit_y,x,y):
    return max(abs(exit_x - x), abs(exit_y - y))

def find_smallest_position(exit_x,exit_y,index):
    # 출구와 사람 1명을 포함하는 가장 작은 사각형 만들기
    human_x,human_y = human[index]
    start_x, start_y = 0,0 # 시작하는 좌표 찾기
    length = calc_length(exit_x, exit_y,human_x,human_y)

    x = calc_position(exit_x,human_x,length)
    y = calc_position(exit_y,human_y,length)
    # print(x,y,'xy',index,'일때',exit_x,exit_y,human[index])
    return [x,y,length]

def find_samll_square():
    min_length = 200
    arr = []
    for idx in range(len(human)):
        if escape[idx] == 1: continue
        sx,sy,length = find_smallest_position(exit[0],exit[1],idx)
        if length < min_length:
            arr = [(sx,sy)]
            min_length = length
        elif length == min_length:
            arr.append((sx,sy))
    arr.sort()
    return [min_length,arr[0]]

def find_square_human(start_x,start_y,length):
    human_index_info = [] #들어가는 유저 정보 저장
    for i in range(start_x, start_x + length+1):
        for j in range(start_y,start_y+length+1):
            for z in range(len(human)):
                if human[z] == (i,j) and escape[z] == 0:
                    human_index_info.append(z)
    # print(human_index_info,'info')
    return human_index_info

def copy_rotation():
    global exit
    # 작은 사각형 범위에 맞춰서 회전 후 값이 있는 배열은 1씩 감소
    # 여기에 들어가는 사람과 출구 좌표도 업데이트 해야함
    length, start_point = find_samll_square()
    copy_arr = []
    for i in range(start_point[0], start_point[0] + length+1):
        row = []
        for j in range(start_point[1],start_point[1] + length+1):
            row.append(arr[i][j])
        copy_arr.append(row)

    # 이럴거면 길이변수 뺄껄 이라고햇더니 아니? length 를 받아왔었네 내정신좀봐
    new_arr = [[0]*len(copy_arr) for _ in range(len(copy_arr))]
    # 복사 후 정사각형 회전
    for i in range(len(copy_arr)):
        for j in range(len(copy_arr)):
            if copy_arr[i][j] != 0:
                new_arr[j][length-i] = copy_arr[i][j] - 1
            else:
                new_arr[j][length-i] = copy_arr[i][j]

    human_index_info = find_square_human(start_point[0],start_point[1],length)
    for idx in range(len(human_index_info)):
        z = human_index_info[idx]
        x,y = human[z] # 1,0
        x = x-start_point[0]
        y = y-start_point[1]

        nx = y + start_point[0]
        ny = length - x + start_point[1]
        human[z] = (nx,ny)
  
    s_x = exit[0] - start_point[0]
    s_y = exit[1] - start_point[1]

    new_x = s_y + start_point[0]
    new_y = length - s_x + start_point[1]
    exit = [new_x,new_y]

    for i in range(start_point[0], start_point[0] + length+1):
        for j in range(start_point[1],start_point[1] + length+1):
            arr[i][j] = new_arr[i-start_point[0]][j - start_point[1]]

for t in range(k):
    # 사람별 최단거리 계산
    for idx in range(m):
        if escape[idx] == 1: continue
        start_distance_info_renewal(idx,human[idx][0],human[idx][1],exit[0], exit[1])
        move(idx)
    if sum(escape) == m:
        break
    copy_rotation()

print(sum(move_count))
print(exit[0] +1,exit[1]+1)
