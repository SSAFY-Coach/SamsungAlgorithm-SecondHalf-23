"""
[queue를 회전시킬 수 있는가?]
풀이시간 : 43분
- 로직 21분
  1) 자석 날을 deque로 저장해서, rotate 함수 사용 😏
  2) 회전을 재귀로 처리할까 하다가 그냥 왼쪽, 오른쪽 자석에 대해 반복문 처리
  3) 대신 회전하려는 자석에서 가까운 자석부터 보면서 회전 안하면 break

- deque의 rotate https://docs.python.org/ko/3.8/library/collections.html?highlight=rotate#collections.deque.rotate
  - rotate(오른쪽으로 이동할 횟수)
  - ex) rotate(1) = deque.appendleft(deque.pop()) => 시계
        rotate(-1) = deque.append(deque.popleft()) => 반시계
"""
from collections import deque

T = int(input())

for tc in range(T):
    N = int(input())
    magnets = []  # [deque([0, 0, 1, 0, 0, 1, 0, 0]), ...]

    # 각 자석을 rotate 시키기 위해 deque로 생성
    for _ in range(4):
        magnets.append(deque(list(map(int, input().split()))))

    # N번 반복
    for _ in range(N):
        num, d = map(int, input().split())
        num -= 1

        # 각 자석이 회전을 할지 말지 표기함. 1이면 시계, -1이면 반시계, 0이면 안함
        rotate_list = [0] * 4
        rotate_list[num] = d  # 회전을 시작하는 자석(메인)

        # 메인의 왼쪽에 있는 자석들
        for left in range(num-1, -1, -1):
            if magnets[left+1][6] != magnets[left][2]:
                # 한칸 떨어지면 반대, 두칸 떨어지면 같은 방향이다.
                rotate_list[left] = d * ((-1) ** abs(num - left))
            else:
                # 회전하지 않았다면 그 이후의 자석들은 볼 필요 없음.
                break

        # 메인의 오른쪽에 있는 자석들.
        for right in range(num+1, 4):
            if magnets[right-1][2] != magnets[right][6]:
                rotate_list[right] = d * ((-1) ** abs(num - right))
            else:
                break

        # 자석의 회전이 순차적으로 일어나는게 아니고 동시에 일어나기 때문에
        # 회전 방향을 저장해놓은 뒤 처리한다.
        for idx in range(4):
            if rotate_list[idx] == 1:
                magnets[idx].rotate(1)
            elif rotate_list[idx] == -1:
                magnets[idx].rotate(-1)

    answer = 0

    for idx in range(4):
        if magnets[idx][0] == 1:
            answer += 2 ** idx

    print(f"#{tc+1} {answer}")