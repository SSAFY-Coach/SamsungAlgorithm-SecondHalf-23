r,c,m = list(map(int, input().split()))
sharks= []
arr = [[() for _ in range(c)] for _ in range(r)]
man_position = -1
man_direction = 1
result =0
# -- 없으면 안하면됨 개꿀
if m == 0:
  print(0)
  exit()

# -- 상어 좌표에 따른 값 초기 입력
for _ in range(m):
  x,y,s,d,z = list(map(int, input().split()))
  # r,c에 대입해줌, 결국 이동이 일어난 후 겹치는 과정에 대한 처리는 배열이 더 쉬울것이라 판단함.
  x-= 1
  y -= 1  # 배열 좌표를 0을 기준으로 움직일 수 있게 함, 나머지 계선을 통한 방향 변경 고려
  # 하나의 값만 있으면 됨, 만약 값이 있는 경우는 비교를 통해 최대 값 주입
  arr[x][y] = (s,d,z)

def shark_move(direction, speed,x,y):
  global r,c
  dx = [0, -1,1,0,0]
  dy = [0, 0,0,1,-1]
  now_direction = direction
  # 왕복이 되는 경우가 생길 수 있음 ex) x 4인데 speed가 9라서 한번 움직이면 와라가리 ssap가능
  # 반복문으로 지속 검증을하고 , 방향을 바꿀 수 있게 해준다.
  c_x = x
  c_y = y
  for _ in range(speed):
    nx = c_x + dx[now_direction]
    ny = c_y + dy[now_direction]
    if  0 <= nx < r and 0 <= ny < c:
      c_x = nx
      c_y = ny
    else:
      if now_direction == 1:
        now_direction = 2
      elif now_direction == 2:
        now_direction = 1
      elif now_direction == 3:
        now_direction = 4
      else:
        now_direction = 3
      # 방향이 초과되니까, 좌표 다시 갱신 
      c_x = c_x + dx[now_direction]
      c_y = c_y + dy[now_direction]
  
  return (c_x,c_y,now_direction)

def change_shark():
  # 새로운 상어 이동위치를 만들어주고, 이동이 된 상어가 겹칠때는 제거하는 과정
    new_arr = [[() for _ in range(c)] for _ in range(r)]
    for i in range(r):
        for j in range(c):
          # 배열에 값이 있다면~
            if len(arr[i][j]) != 0:
                nx,ny,d = shark_move(arr[i][j][1], arr[i][j][0],i,j)
                if len(new_arr[nx][ny]) == 0:
                    new_arr[nx][ny] = (arr[i][j][0],d,arr[i][j][2])
                else:
                    shark_size = new_arr[nx][ny][2]
                    if shark_size > arr[i][j][2]:
                        continue
                    else:
                        new_arr[nx][ny] = (arr[i][j][0],d,arr[i][j][2])
  # arr 갱신
    for i in range(r):
        for j in range(c):
            arr[i][j] = new_arr[i][j]

def get_shark():
    global man_position,man_direction,result
    position = man_position +  1
    for i in range(r):
        if len(arr[i][position]) != 0:
            result += arr[i][position][2]
            arr[i][position] = () # 없는 칸으로 만들어주기
            break
    return position

for _ in range(c):
    man_position = get_shark()
    change_shark()
# 100 * 100 * 100 총 100만인디
print(result)