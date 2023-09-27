
T = int(input())
for t in range(T):
    # -- input
    n = int(input())
    arr = [list(map(int, input().split())) for _ in range(n)]

    # -- 로직 생성
    escape = []
    user = []
    for i in range(n):
        for j in range(n):
            if arr[i][j] !=0 and arr[i][j] != 1:
                escape.append((i,j,arr[i][j]))
            if arr[i][j] == 1:
                user.append((i,j))


    user_timeTable = [[0,0] for _ in range(len(user))]

    for i in range(len(user)):
        x,y = user[i]
        for j in range(2):
            distance = abs(x - escape[j][0]) + abs(y - escape[j][1])
            user_timeTable[i][j] = distance
    user_timeTable.sort()
    length = len(user_timeTable)
    combination = []

    def recur(cur,arr):
        if cur == length:
            combination.append(arr[:])
            return

        for i in range(2):
            arr[cur] = i
            recur(cur+1,arr)
            arr[cur] = 0

    recur(0,[0] * length)
    min_time = 1e9

    def find_last_time(dp, arr, length):
        global last_biggest_minute
        if not arr:
            return
        if len(arr) <= 3:
            last_biggest_minute = max(arr[-1] + 1 + length, last_biggest_minute)
        else:
            # 각 처음 값들이 탈출하는데 걸리는 시간
            dp[0],dp[1],dp[2] = arr[0] + 1 + length,arr[1] + 1 + length, arr[2] + 1 + length
            for z in range(3,len(arr)):
                # 이전 3번째 앞의 값이 탈출하는데 걸리는 시간 + 내가 탈출하는데 걸리는 시간
                # 내가 탈출하는데 걸리는시간 : 입구도착까지 걸리는 시간 + 입구에서 대기하는 시간(지연된시간) + 길이
                if dp[z-3] - arr[z] <= 0:
                    dp[z] = arr[z] + 1 + length
                else:
                    dp[z] = dp[z-3] - arr[z] + length + arr[z]
            last_biggest_minute = max(dp[-1], last_biggest_minute)


    for case in combination:
        # 스택 큐로 할려다가 길이만큼 계단에 설 수 있는줄 알고 어지러웠따..
        # 경우의 수 문제이기 때문에 dp일 확률이 높다고 생각했고, 점화식을 찾는데 한참이였따..
        first_exit = []
        second_exit = []
        last_biggest_minute = 0
        for idx in range(len(case)):
            if case[idx] == 0:
                first_exit.append(user_timeTable[idx][0])
            else:
                second_exit.append(user_timeTable[idx][1])
        first_exit.sort()
        second_exit.sort()
        dp1 = [0] * len(first_exit)
        dp2 = [0] * len(second_exit)
        find_last_time(dp1,first_exit,escape[0][2])
        find_last_time(dp2,second_exit,escape[1][2])
        min_time = min(last_biggest_minute, min_time)

    print("#" + str(t+1),str(min_time))









    # 각 거리 측정 완료
