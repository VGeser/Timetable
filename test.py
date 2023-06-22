import requests
import prettytable

base = 'http://127.0.0.1:8080/api/v1'

s = requests.session()
token = s.post(base+'/login', json={'username':'ilya', 'password':'123'}).json()["token"]
print(token)
s.headers['Authorization']=f'Bearer {token}'

def addOne(b):
    return list(map(lambda a: a + 1, b))

def add_teacher(name: str, slots: list[int]) -> int:
    print(f'val {name.lower()} = repos.teachers.getByName("{name}")')
    res = s.post(base+'/teachers', json={'name': name, 'slots': addOne(slots)})
    # print(res.text)
    i = res.json()['id']
    # print('Added teacher', i)
    return int(i)


def add_room(name: str, capacity: int, tools: bool, slots: list[int]) -> int:
    print(f'val room{name.lower()} = repos.rooms.getByName("{name}")')
    i = s.post(base+'/rooms', json={'name': name, 'capacity': capacity, 'tools': tools, 'slots': addOne(slots)}).json()['id']
    # print('Added room', i)
    return int(i)


def add_course(name: str, tools: bool, frequency: int, teacher: int) -> int:
    print(f'val {name.lower()} = repos.courses.getByName("{name}")')
    i = s.post(base+'/courses', json={'name': name, 'tools': tools, 'frequency': frequency, 'teacher': teacher}).json()['id']
    # print('Added course', i)
    return int(i)


def add_group(name: str, quantity: int, availableSlots: list[int], courses: list[int]) -> int:
    print(f'val group{name.lower()} = repos.groups.getByName("{name}")')
    i = s.post(base+'/groups', json={'name': name, 'slots': addOne(availableSlots), 'quantity': quantity, 'courses': courses,}).json()['id']
    # print('Added group', i)
    return int(i)

print('dropping', s.post(base+'/dropdb'))
if len(s.get(base + '/teachers').json()) == 0:

    ivanov = add_teacher("Ivanov", [3,10,15,19,30,32,36,37])
    gatilov = add_teacher("Gatilov", [0,14,16,22,23,25])
    sidorov = add_teacher("Sidorov", [4,8,11,16,21,24,33])

    law = add_course('Law', False, 2, ivanov)
    history = add_course('History', False, 2, ivanov)
    semiotics = add_course('Semiotics', True, 1, ivanov)
    linguistics = add_course('Linguistics', True, 3, ivanov)
    imperative = add_course('Imperative', True, 1, gatilov)
    oop = add_course('OOP', False, 1, gatilov)
    dp = add_course('Data Processing', True, 2, gatilov)
    declarative = add_course('Declarative', False, 2, gatilov)
    biology = add_course('Biology', False, 2, sidorov)
    microbiology = add_course('Microbiology', True, 1, sidorov)
    pharmacy = add_course('Pharmacy', False, 2, sidorov)
    bioinf = add_course('BioInformatics', True, 2, sidorov)

    add_room("123", 15, True, [4,8,16,21,33])
    add_room("345", 30, False, [0])
    add_room("456", 80, True, [3,11,14,15,16,19,22,23,24,25,30,32,37])
    add_room("678", 100, False, [10,36])

    add_group("20201", 50, [3,10,14,15,16,19,30,32,36,37], [law,history,semiotics,linguistics,declarative])
    add_group("20202", 25, [0,10,11,14,16,22,23,24,25,36], [imperative,oop,dp,declarative,history,bioinf])
    add_group("20203", 12, [3,4,8,10,15,16,19,21,30,32,33,36], [biology,microbiology,pharmacy,law,linguistics,history])

# res = s.post(base + "/table/generate")
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


trbs = s.get(base+'/table/problems').json()
print(*trbs, sep='\n')

# for e in s.get(base + '/teachers').json():
#     name = e['name']
#     i = e['id']
#     print(name)
#     json = s.get(base + f'/table/teacher/{i}').json()
#     draw_table(json)
#     print()
#
# for e in s.get(base + '/groups').json():
#     name = e['name']
#     i = e['id']
#     print(name)
#     json = s.get(base + f'/table/group/{i}').json()
#     draw_table(json)
#     print()
#
# for e in s.get(base + '/rooms').json():
#     name = e['name']
#     i = e['id']
#     print(name)
#     json = s.get(base + f'/table/room/{i}').json()
#     draw_table(json)
#     print()