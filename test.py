import requests

base = 'http://127.0.0.1:8080/api/v1'

s = requests.session()
token = s.post(base+'/login', json={'username':'ilya', 'password':'123'}).json()["token"]
print(token)
s.headers['Authorization']=f'Bearer {token}'


def add_teacher(name: str, slots: list[int]) -> int:
    i = s.post(base+'/teachers', json={'name': name, 'slots': slots}).text
    print('Added teacher', i)
    return int(i)


def add_room(name: str, capacity: int, tools: bool, slots: list[int]) -> int:
    i = s.post(base+'/rooms', json={'name': name, 'capacity': capacity, 'tools': tools, 'slots': slots}).text
    print('Added room', i)
    return int(i)


def add_course(name: str, tools: bool, frequency: int, teacher: int) -> int:
    i = s.post(base+'/courses', json={'name': name, 'tools': tools, 'frequency': frequency, 'teacher': teacher}).text
    print('Added course', i)
    return int(i)


def add_group(name: str, quantity: int, availableSlots: list[int], courses: list[int]) -> int:
    i = s.post(base+'/groups', json={'name': name, 'availableSlots': availableSlots, 'quantity': quantity, 'courses': courses,}).text
    print('Added group', i)
    return int(i)


t1 = add_teacher("Vlasov", [1,2,3,       8,9,10])
t2 = add_teacher("irtegov", [1,2,3, 8, 9, 10,   15, 16, 17])

os = add_course('OS', False, 2, t2)
jaba = add_course('Jaba', True, 1, t1)
pac = add_course('PAC', True, 2, t1)

add_room("small", 13, False, [1, 9, 17])
add_room("big", 100, True, [1, 2, 3])

add_group("20213", 13, [1,2,3,8,9,10,15,16,17], [os, jaba, pac])
add_group("20215", 50, [1,2,3,8,9,10,15,16,17], [os, jaba])

res = s.post(base + "/table/generate")
print(res.status_code)