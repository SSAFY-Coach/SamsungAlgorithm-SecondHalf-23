T = int(input())

def dfs(number, direction):
    global arr,check
    check[number] = 1
    if direction == 1:
        # 시계방향 맨끝을 맨앞으로
        arr[number].insert(0, arr[number].pop(-1))
        # 왼쪽 오른쪽 톱니 체크
        if 0 <= number + 1 < 4 and check[number+1] == 0:
            if arr[number][3] != arr[number+1][6]:
                dfs(number+1,-1)
        if 0 <= number -1  < 4 and check[number-1] == 0:
            if arr[number][7] != arr[number-1][2]:
                dfs(number-1,-1)
    else:
        # 반시계방향 맨앞을 맨뒤로
        arr[number].append(arr[number].pop(0))
        if 0 <= number + 1 < 4 and check[number + 1] == 0:
            if arr[number][1] != arr[number + 1][6]:
                dfs(number + 1, 1)
        if 0 <= number - 1 < 4 and check[number - 1] == 0:
            if arr[number][5] != arr[number - 1][2]:
                dfs(number - 1, 1)
        return

for t in range(1,T+1):
    # -- input --
    k = int(input())
    arr = [list(map(int, input().split())) for _ in range(4)]
    change = [list(map(int, input().split())) for _ in range(k)]

    # -- dfs
    for f in change:
        check = [0 for _ in range(4)]
        dfs(f[0]-1,f[1])

    # 다 돌리고 나서
    result = 0
    for i in range(4):
        result += arr[i][0] * (2**i)
    print("#"+str(t),result)