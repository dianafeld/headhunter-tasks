from operator import attrgetter
from heapq import merge
import itertools
import math

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        
    def __lt__(self, pnt):
            return self.y < pnt.y
    
    def __repr__(self):
        return '({0.x}, {0.y})'.format(self)    
    
    
def sq_dist(lhs, rhs):
    return (lhs.x - rhs.x) ** 2 + (lhs.y - rhs.y) ** 2

def read_input():
    
    points = []
    
    with open('input.txt') as filein:
        for line in filein:
            x, y = map(int, line.split())
            points.append(Point(x, y))
              
    return points

INFINITY = 10 ** 18
min_distance = INFINITY

def update_min_distance(dist):
    global min_distance
    min_distance = min(min_distance, dist)
    

def _compute_min_distance_naive(points, left, right):
    return min([sq_dist(*points_pair) for points_pair in itertools.combinations(points[left:right], 2)])


def _compute_min_distance_recursive(points, left, right): # [left; right)
    
    if right - left <= 5:
        dist = _compute_min_distance_naive(points, left, right)
        update_min_distance(dist)
        points[left:right] = sorted(points[left:right], key = attrgetter('y'))
        
    else:
        middle = (left + right) // 2
        middle_x = points[middle].x
        _compute_min_distance_recursive(points, left, middle)
        _compute_min_distance_recursive(points, middle + 1, right)
        
        points[left:right] = merge(points[left:middle], points[middle:right])
        
        
        closest_points = [point for point in itertools.islice(points, left, right) 
                          if abs(point.x - middle_x) < min_distance]
        
        for i in range(len(points)):
            for j in range(i - 1, -1, -1):
                if abs(points[i].y - points[j].y) >= min_distance:
                    break
                else:
                    update_min_distance(sq_dist(points[i], points[j]))
                        

def compute_min_distance(points):
    points.sort(key = attrgetter('x', 'y'))
    
    _compute_min_distance_recursive(points, 0, len(points))

    return math.sqrt(min_distance)


points = read_input()

dist = compute_min_distance(points)
print(dist)
