"""
풀이시간 : 30분
- 0.5초는 위치를 2배로 늘려주면 해결됨
- 영원히 만나지 않는 애들은 1000부터 -1000까지 한번 갔는데 충돌 안되었으면 그대로 끝임
  - 그래서 2000*2, 4000초가 넘으면 끝내도록 함
  - O(4000 * (1000+1000+1000)) = O(12,000,000) 즉, 천2백만이라 ㄱㅊ음
"""
from collections import defaultdict


T = int(input())

dx = [0, 0, -1, 1]
dy = [1, -1, 0, 0]


class Atom:
    def __init__(self, x, y, d, e):
        self.x = x
        self.y = y
        self.d = d
        self.e = e


for tc in range(T):
    answer = 0
    N = int(input())
    atoms = []

    # 원자 정보 저장
    for i in range(N):
        x, y, d, e = map(int, input().split())
        atoms.append(Atom(x*2, y*2, d, e))

    sec = 0

    while sec <= 4000:
        pos = defaultdict(list)  # 좌표(r, c)를 key로 하고, 해당 위치에 원자의 idx를 리스트로 저장
        cnt = len(atoms)  # 원자가 사라지기 때문에 갯수를 매번 세어야함.
        # 1. 원자 이동
        for i in range(cnt):
            atom = atoms[i]
            atom.x += dx[atom.d]
            atom.y += dy[atom.d]
            pos[(atom.x, atom.y)].append(i)

        # 2. 충돌 확인
        destroyed = []  # 없어질 원자의 index 저장
        for position, atom_list in pos.items():
            if len(atom_list) > 1:
                destroyed += atom_list

                for idx in atom_list:
                    answer += atoms[idx].e

        destroyed.sort(reverse=True)  # 배열의 뒤에서부터 지워야 인덱스 에러가 안남

        for idx in destroyed:
            atoms.pop(idx)

        sec += 1

    print(f"#{tc+1} {answer}")