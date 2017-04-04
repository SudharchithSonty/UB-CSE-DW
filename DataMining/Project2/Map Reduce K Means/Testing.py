import numpy as np
import random
from sklearn.metrics import jaccard_similarity_score
import matplotlib.pyplot as plt
from sklearn.decomposition import  PCA
import sys
filename = "/Users/Admin/Desktop/DataMining/Project-2/input/iyer.txt"
groundtruth = []
predicted = []

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
def get_groundtruth(s):
    with open(s) as f:
        for row in f:
            row = row.strip().split()
            groundtruth.append(int(row[1]))
    return groundtruth

file2 = sys.argv[1]


groundtruth = get_groundtruth(filename)


with open(file2) as f:
    for row in f:
        row  = row.strip().split("\t")
        predicted.append(int(row[2]))



def get_jaccard_score():
    y_true = groundtruth
    y_pred = predicted
    return jaccard_similarity_score(y_true, y_pred)

print "JACCARD COEFFIECIENT IS: " + str(get_jaccard_score())



list = []
for key, value in inputdata.iteritems():
    list.append((value))
list = np.array(list)
y = np.array(predicted)
target_names = [0, 1, 2, 3, 4]
pca = PCA(n_components=2)
X_r = pca.fit(list).transform(list)
plt.figure()
plt.scatter(X_r[:, 0], X_r[:, 1], c=y, )
plt.legend()
plt.title('PCA')
plt.show()
