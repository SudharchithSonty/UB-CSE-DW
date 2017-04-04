import sys, copy
import numpy as np
from sklearn.metrics import jaccard_similarity_score
from sklearn.metrics.cluster import adjusted_rand_score
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA

def read_data(fname):
    pts = np.loadtxt(fname, delimiter="\t")
    return pts

def compute_dist_mat(pts):
    dist_mat = np.hypot(pts[:, 0, np.newaxis]-pts[:, 0], pts[:, 1, np.newaxis]-pts[:, 1])
    return dist_mat


def clustering(dist_mat):
    plist = [[x] for x in range(0, dist_mat.shape[0])]
    nlist = copy.deepcopy(plist)

    # indexes for points start from 0 
    # nlist - stores the indexes of the points in one cluster 
    # plist - stores how the clustes are extended
    no_clusters = 1

    i_bkp = 9999999
    j_bkp = 9999999
    
    while (len(nlist) > no_clusters):
        for i in range(0, len(nlist)):
            for j in range(i+1, len(nlist)-1):
                gl_min = 9999999
                i_bkp = i
                j_bkp = j
                for x in nlist[i]:
                    for y in nlist[j]:
                        if dist_mat[x, y] < gl_min:
                            gl_min = dist_mat[x, y]
                            i_bkp = i
                            j_bkp = j
                
        nlist[i_bkp] = nlist[i_bkp] + nlist[j_bkp]
        plist[i_bkp] = plist[i_bkp] + [plist[j_bkp]]
        plist.remove(plist[j_bkp])
        nlist.remove(nlist[j_bkp])

        # nlist or clusters have been updated you can check for the numbers of clusters by querying len(nlist)
        
    print len(plist)
    print(nlist)

if __name__ == "__main__":
    fname = str(sys.argv[1])
    pts = read_data(fname)
    dist_mat = compute_dist_mat(pts)
    clustering(dist_mat)

def get_groundtruth(s):
    groundtruth = []
    with open(fname) as f:
        for row in f:
            row = row.strip().split()
            groundtruth.append(int(row[1]))
    return groundtruth

def parse(s):
    count = 0
    result = {}
    with open(fname) as f:
        for row in f:
            count += 1
            row = row.strip().split()
            key = int(row[0])
            result[key] = row[2:]
    for x in result:
        final = [float(i) for i in result[x]]
        result[x] = final
    return result
inputdata = parse(fname)


list = []
for key, value in inputdata.iteritems():
    list.append((value))
list = np.array(list)
y = np.array(get_groundtruth(fname))
target_names = [0, 1, 2, 3, 4]
pca = PCA(n_components=2)
X_r = pca.fit(list).transform(list)
plt.figure()
plt.scatter(X_r[:, 0], X_r[:, 1], c=y)
plt.legend()
plt.title('PCA')
plt.show()