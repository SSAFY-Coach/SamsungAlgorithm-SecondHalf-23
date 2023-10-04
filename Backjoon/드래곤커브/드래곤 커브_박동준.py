'''
좌표축 방향이 달라서 유의해야 할 문제
x축 ->
y축 아래

90도 회전 -> xy 대칭 + y축 대칭

시작점 -> 끝점이 된다.
시작점은 유지, 대신 끝점 좌표는 계속 갱신
'''
n = int(input())
curve_info = [list(map(int, input().split())) for _ in range(n)]
direction = [(1,0),(0,-1),(-1,0),(0,1)]
directory = {}  # 정보 저장용, 겹치는건 상관없음 ㅇㅇ 배열로 추가예정

def find_zero_gap(num):
    count = 0
    if num < 0:
        while num != 0:
            count +=1
            num += 1
        return count
    elif num == 0:
        return count
    else:
        while num != 0:
            count += 1
            num -= 1
        return -1 * count

for x,y,d,g in curve_info:
    # 유효좌표 진위여부는 마지막에 directory에서 판별 예정
    # 초기 endpoint 설정
    grow_point = 0
    curve = []
    endx, endy = x + direction[d][0], y + direction[d][1]
    if g == 0:
        directory[(x,y)] = True
        directory[(endx,endy)] = True
        continue
    curve.append((x, y))
    curve.append((endx,endy))
    for i in range(g):
        # end포e인트 갱신을 위한 0,0까지의 차이 구하기
        gap_x = find_zero_gap(endx)
        gap_y = find_zero_gap(endy)
        added_curve=[]
        for cv in curve:
            if cv != (endx,endy): # end포인트가 아니라면
                cx = cv[0] + gap_x
                cy = cv[1] + gap_y
                # y/x 축 대칭 + y축 대칭
                nx = -1 * cy - gap_x
                ny = cx - gap_y
                added_curve.append((nx,ny))
            if cv == (x,y):
                endx,endy = nx, ny

        curve += added_curve
    # #완료되면 curve 정보를 directory에 갱신
    for pos in curve:
        if not directory.get(pos,False):    # 없다면
            directory[pos] = True
check = [[0] * 101 for _ in range(101)]
for idx in directory:
    x,y = idx
    if 0<= x <= 100 and 0 <= y <= 100:
        check[x][y] = 1
result = 0
di = [(0,1),(1,1),(1,0)]
for i in range(len(check)):
    for j in range(len(check)):
        if check[i][j] == 1:
            count = 0
            for k in range(3):
                nx = i + di[k][0]
                ny = j + di[k][1]
                if 0 <= nx <= 100 and 0<= ny <= 100 and check[nx][ny] == 1:
                    count += 1
            if count == 3:
                result += 1

print(result)








