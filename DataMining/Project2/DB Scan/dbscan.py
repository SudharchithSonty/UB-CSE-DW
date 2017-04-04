import numpy as np
import math
from sklearn.metrics import jaccard_similarity_score
from sklearn.metrics.cluster import adjusted_rand_score
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA



points = np.loadtxt("/Users/Admin/Desktop/DataMining/Project-2/input/new_dataset_1.txt", delimiter="\t")
clusters = []                   
visited = []
Noise = []
result = {}

for i in range(len(points)):
    result[i+1] = -1
 
   
def _regionQuery(point_id,eps):
        neighbors = []
        
        for d in points:
            sum = 0
            for i in range(2,len(d)):
                
                sum +=  (d[i] - point_id[i])**2
            if math.sqrt(sum) <= eps:
                neighbors.append(d)
                #print ("eps", eps, math.sqrt(sum))
                #print ("point_id", (point_id[0] - point_id[0]))
        return neighbors



def DBSCAN(eps, min_pts):
     
    count = 0
    for i in range(len(points)):
        point_id = points[i]
        if point_id[0] in visited: 
            continue
        visited.append(point_id[0])
        neighbors = _regionQuery(point_id, eps)
        if len(neighbors) < min_pts:
            Noise.append(point_id[0])
        else:
            
            count += 1
            expandCluster(point_id, neighbors, clusters, count, eps, min_pts, visited)
    #return  (len(clusters[count]))   

    #for cluster in clusters:
      #  col =[rand(1),rand(1),rand(1)]     
      #  print (cluster) 
            
def expandCluster(point_id, neighbors, clusters, count, eps, min_pts, visited):
    clusters.append(point_id[0])
    result[point_id[0]] = count
    for j in range(len(neighbors)):
        pid = points[j]
        if pid[0] not in visited:
            visited.append(pid[0])
            neighbors1 = _regionQuery(pid, eps)
            if len(neighbors1) >= min_pts:
                neighbors += neighbors1
        if pid[0] not in clusters:
            clusters.append(pid[0])
#            clusters.append(count)
            result[pid[0]] = count   
            #print (count)
DBSCAN(4, 80)
print("{" + "\n".join("{}: {}".format(k,v) for k, v in result.items()) + "}")
#print (len(clusters))
#print (result.keys())



filename = "/Users/Admin/Desktop/DataMining/Project-2/input/iyer.txt"
def get_groundtruth(s):
    groundtruth = []
    with open(filename) as f:
        for row in f:
            row = row.strip().split()
            groundtruth.append(int(row[1]))
    return groundtruth
    
def get_jaccard_score():
    y_true = get_groundtruth(filename)
    y_pred = list(result.values())
    print (jaccard_similarity_score(y_true, y_pred))
    print (adjusted_rand_score(y_true, y_pred))

def parse(s):
    count = 0
    result = {}
    with open(filename) as f:
        for row in f:
            count += 1
            row = row.strip().split()
            key = int(row[0])
            result[key] = row[2:]
    for x in result:
        final = [float(i) for i in result[x]]
        result[x] = final
    return result
inputdata = parse(filename)

result = result.values()
list = []
for key, value in inputdata.iteritems():
    list.append((value))
list = np.array(list)
y = np.array(result)
target_names = [0, 1, 2, 3, 4]
pca = PCA(n_components=2)
X_r = pca.fit(list).transform(list)
plt.figure()
plt.scatter(X_r[:, 0], X_r[:, 1], c=y)
plt.legend()
plt.title('PCA')
plt.show()
