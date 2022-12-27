import requests
import prettytable

base = 'http://127.0.0.1:8080/api/v1'

s = requests.session()
token = s.post(base+'/login', json={'username':'ilya', 'password':'123'}).json()["token"]
print(token)
s.headers['Authorization']=f'Bearer {token}'


def add_teacher(name: str, slots) -> int:
    i = s.post(base+'/teachers', json={'name': name, 'slots': slots}).text
    print('Added teacher', i)
    return int(i)


def add_room(name: str, capacity: int, tools: bool, slots) -> int:
    i = s.post(base+'/rooms', json={'name': name, 'capacity': capacity, 'tools': tools, 'slots': slots}).text
    print('Added room', i)
    return int(i)


def add_course(name: str, tools: bool, frequency: int, teacher: int) -> int:
    i = s.post(base+'/courses', json={'name': name, 'tools': tools, 'frequency': frequency, 'teacher': teacher}).text
    print('Added course', i)
    return int(i)


def add_group(name: str, quantity: int, availableSlots, courses) -> int:
    i = s.post(base+'/groups', json={'name': name, 'availableSlots': availableSlots, 'quantity': quantity, 'courses': courses,}).text
    print('Added group', i)
    return int(i)

res = s.get(base+'/dropdb')
print(res.text)
print(f'dropdb: {res.status_code}')

# tour test goes here
ivanov = add_teacher("Ivanov", [3,10,15,30,32,36,37])
gatilov = add_teacher("Gatilov", [0,14,16,22,23,25])
sidorov = add_teacher("Sidorov", [4,8,11,16,21,24])

law = add_course('Law', False, 1, ivanov)
history = add_course('History', False, 2, ivanov)
semiotics = add_course('Semiotics', True, 1, ivanov)
linguistics = add_course('Linguistics', True, 3, ivanov)
imperative = add_course('Imperative', True, 1, gatilov)
oop = add_course('OOP', False, 1, gatilov)
dp = add_course('Data Processing', True, 2, gatilov)
declarative = add_course('Declarative', False, 2, gatilov)
biology = add_course('Biology', False, 1, sidorov)
microbiology = add_course('Microbiology', True, 1, sidorov)
pharmacy = add_course('Pharmacy', False, 2, sidorov)
bioinf = add_course('BioInformatics', True, 2, sidorov)

add_room("123", 40, False, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48])
add_room("345", 75, True, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48])
add_room("567", 200, False, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48])
add_room("789", 60, True, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48])

add_group("20201", 21, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [semiotics,oop,linguistics])
add_group("20202", 33, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [semiotics,linguistics,history,law])
add_group("20203", 18, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [history,imperative,declarative])
add_group("20204", 14, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [oop,bioinf,linguistics])
add_group("20205", 42, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [history,dp,bioinf])
add_group("20206", 34, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [history,bioinf,microbiology])
add_group("20207", 39, [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
41,42,43,44,45,46,47,48], [history,pharmacy,law])

# do not change after this line

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
