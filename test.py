import requests
import prettytable

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


if len(s.get(base + '/teachers').json()) == 0:

    t1 = add_teacher("Vlasov", [1,2,3, 8,9,10]) 
    t2 = add_teacher("irtegov", [1,2,3, 9, 10, 15, 16, 17]) 
    
    os = add_course('OS', False, 2, t2) 
    jaba = add_course('Jaba', True, 1, t1) 
    pac = add_course('PAC', True, 2, t1) 
    
    add_room("small", 13, True, [1, 9]) 
    add_room("big", 100, True, [1, 2, 3]) 
    
    add_group("20213", 13, [1,2,3,8,9,10,15,16,17], [ jaba, pac]) 
    add_group("20215", 50, [1,2,3,8,9,10,15,16,17], [os, jaba])

res = s.post(base + "/table/generate")
print(res.status_code)




def draw_table(json):
    tbl = prettytable.PrettyTable()
    tbl.header = False
    tbl.hrules = prettytable.ALL
    for day in json:
        row = []
        for slot in day:
            if slot is None:
                row.append('')
                continue
            s = ''
            s += slot['course']['name'] + '\n'
            s += slot['room']['name'] + '\n'
            s += slot['teacher']['name'] + '\n'
            s += ', '.join(map(lambda a: a['name'], slot['groups'])) + '\n'
            row.append(s)
        tbl.add_column('', row)

    print(tbl)


for e in s.get(base + '/teachers').json():
    name = e['name']
    i = e['id']
    print(name)
    json = s.get(base + f'/table/teacher/{i}').json()
    draw_table(json)
    print()

for e in s.get(base + '/groups').json():
    name = e['name']
    i = e['id']
    print(name)
    json = s.get(base + f'/table/group/{i}').json()
    draw_table(json)
    print()

for e in s.get(base + '/rooms').json():
    name = e['name']
    i = e['id']
    print(name)
    json = s.get(base + f'/table/room/{i}').json()
    draw_table(json)
    print()