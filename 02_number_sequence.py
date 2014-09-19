ans_number = -1
ans_start = 0
def update_answer(number, start):
    global ans_number, ans_start
    
    if ans_number == -1 or number < ans_number:
        ans_number = number
        ans_start = start
    elif number == ans_number and start < ans_start:
        ans_start = start

        
def is_right_sequence(number, start, length):
    subnumber = number[start:start + length]
    n = int(subnumber)
    
    #forwards
    cur_index = start + length
    while cur_index < len(number):
        n += 1
        if len(number) - cur_index >= len(str(n)):
            if number[cur_index:cur_index + len(str(n))] != str(n):
                return False
        else:
            if number[cur_index:] != str(n)[:len(number) - cur_index]:
                return False
            
        cur_index += len(str(n))
    
            
    #backwards
    cur_index = start
    n = int(subnumber)
    while cur_index > 0:
        n -= 1
        if cur_index - len(str(n)) >= 0:
            if number[cur_index - len(str(n)):cur_index] != str(n):
                return False
        else:
            if number[:cur_index] != str(n)[len(str(n)) - cur_index:]:
                return False
                    
        cur_index -= len(str(n))   
    
    return True


def calculate_index(number):

    index = 0
    powers_10 = 1
    digits = 1
    while powers_10 * 10 <= number:
        index += (9 * powers_10) * digits
        powers_10 *= 10
        digits += 1
    
    index += (number - powers_10) * digits + 1

    return index + ans_start


def find_min_index(number):
    
    #if there is complete number

    for start in range(len(number)):
        for length in range(start + 1, len(number) - start + 1):
            subnumber = number[start:start + length]
            if not subnumber.startswith('0') and is_right_sequence(number, start, length):
                update_answer(int(subnumber), -start)
    
    # if there is no complete number
    
    for border in range(1, len(number)):
        part1 = number[:border]
        part2 = number[border:]
        
        if not part2.startswith('0'):
            if set(part1) == set('9'):
                part2 = str(int(part2) - 1)
                update_answer(int(part2[:len(part2)] + part1), len(part2))
                
                common = 0
                while common < min(len(part1), len(part2)) and part2[-common - 1] == part1[common]:
                    common += 1
                    if str(int(part2[:len(part2) - common] + part1))[:len(part2)] == part2:
                        update_answer(int(part2[:len(part2) - common] + part1), len(part2) - common)
                        
            else:
                part1 = str(int(part1) + 1)
                update_answer(int(part2[:len(part2)] + part1), -len(part1))
                
                common = 0
                while common < min(len(part1), len(part2)) and part2[-common - 1] == part1[common]:
                    common += 1
                    if str(int(part2[:len(part2) - common] + part1))[:len(part2)] == part2:
                        update_answer(int(part2[:len(part2) - common] + part1), -len(part1))

    return calculate_index(ans_number)       
        
        
with open('input2.txt') as filein:
    for line in filein:
        ans_number = -1
        print(find_min_index(line.strip()))
