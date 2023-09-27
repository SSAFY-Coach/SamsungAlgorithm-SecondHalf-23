'''
비활성, 활성, 다이

격자 활성 -> bfs or dfs 문제임
- 특이점
1. 배양 용기 무한 사이즈 (하...) -> 무한으로 둘수는 없음 -> 결국 4방향 번식임
# 유무만 알면 되기 때문에, 객체로 다룸
# 대신, 객체 갯수 역시 늘어나기 때문에, 죽은 세포들에 대한 탐색은 쉽게 되도록, 죽은 좌표에 대한 탐색이 가능하도록해줌
------------
1. 이미 줄기세포가 있는 경우에는 번식이 불가능함, 죽은 세포 역시 마찬가지
2.

세포 정보가 담긴 객체를 순회시킴.
1. 하나씩 정보를 먼저 가져옴
2. 각 끝의 시간을 확인하고 줄여줌 -> 활성화 및 비활성화 체크를 위해
    2-1. 시간을 줄였을 때 0이 된다면, 비활 -> 활, 활 -> 죽음
        0 비활 , 1 활
    2-2. 활성화일경우 -> 4방향 번식
        2-2-1. 조건, 죽은세포 좌표에 있는지
        2-2-2. 하단조건) 기존에 없을 경우 바로 주입, 만약 있는 좌표라면 생명력 수치 크기를 큰 놈으로 대치

- 활성화 및 비활성화 체크 함수
- 초기 input 객체 생성
-
'''

T = int(input())
for t in range(1,T+1):
    N,M,time = list(map(int, input().split()))
    arr = [list(map(int, input().split())) for _ in range(N)]
    dx = [0,0,1,-1]
    dy = [1,-1,0,0]
    life_cell = {}
    dead_cell = {}
    # 입력에 따른 data 처리
    for i in range(N):
        for j in range(M):
            if arr[i][j] != 0:
                life_cell[(i,j)] = [arr[i][j],0,arr[i][j]]
    # 일단 한번 사용
    for hour in range(time):
        new_ceil = {}
        delete_key = []
        for key in life_cell:
            # 각 상태 확인
            x,y = key
            life, status, time = life_cell[key]
            time -= 1
            # 활성화, 비활성화냐에 따라 달라지는데, 비활의 경우 1만 감소해서 갱신시켜주면됨
            if status == 0:
                if time != 0:
                    life_cell[key] = [life,status,time]
                else:
                    status = 1
                    life_cell[(x, y)] = [life, status, life]
            else:
                # 여기가 찐임
                for i in range(4):
                    nx = x + dx[i]
                    ny = y + dy[i]
                    # nx,ny에 값이 없다면 그리고 죽은 세포에도 값이 없다면
                    if life_cell.get((nx,ny),()) == () and dead_cell.get((nx,ny),0) == 0:
                        # 이번 순회를 통해 새로이 생성되어야할 값이 있었을 경우 생명력이 큰 값으로 교체
                        # print('들어옴')
                        if new_ceil.get((nx,ny),()) and new_ceil[(nx,ny)][0] < life:
                            new_ceil[(nx,ny)] = [life,0,life]
                        # 없을 경우 새로 입력해줌
                        elif new_ceil.get((nx,ny),()) == ():
                            new_ceil[(nx,ny)] = [life,0,life]
                if time == 0:
                    delete_key.append((x, y))
                    dead_cell[(x, y)] = 1
                else:
                    life_cell[(x,y)] = [life,status,time]

        for new_key in new_ceil:
            life_cell[new_key] = new_ceil[new_key]
        for x,y in delete_key:
            del life_cell[(x,y)]
    # result
    print("#"+str(t),len(life_cell))