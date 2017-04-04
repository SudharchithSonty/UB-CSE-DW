import numpy as np
np.set_printoptions(threshold=np.inf)
import sys
from numpy import linalg as LA
from sklearn.preprocessing import normalize


#------FUNCTION TO CHECK FOR CONVERGENCE----#
def converge(A, B):
    if(np.array_equal(A, B)):
        return True
    else:
        return False
#-----------------END------------------#


fname = str(sys.argv[1])
expansion = int(sys.argv[2])
inflation = int(sys.argv[3])
delims = " "
matrix = np.zeros((180, 180))
prev = np.array(matrix)
#-------------CODE TO INDEX THE VALUES IN DATASET IN A CONTIGUOUS FORMAT----#
temp = set()
with open(fname) as f:
    for line in f:
        tmp = line.strip()
        tmp = tmp.split(delims)
        idx = tmp[0]
        idy = tmp[1]
        if not temp.__contains__(idx):
            temp.add(idx)
        if not temp.__contains__(idy):
            temp.add(idy)

idxlist = []

dict = {}
i =0
for s in temp:
    key = s
    idxlist.append(i)
    dict[s] = i
    i+=1
#----------------------------END-----------------------#


#----------------------CODE TO CREATE THE INPUT MATRIX FROM THE DATASET-----------------#
with open(fname) as f:
    for line in f:
        tmp = line.strip()
        tmp = tmp.split(delims)
        idx = int(dict[tmp[0]])
        idy = int(dict[tmp[1]])
        matrix[idx][idy] = 1


#ADDING SELF LOOPS
np.fill_diagonal(matrix,1)
#END

#NORMALIZING FOR THE FIRST ITERATION
Adjmatrix = normalize(matrix,norm = 'l1', axis =0)

#ROUNDING OFF TO TWO DECIMAL POINTS
Adjmatrix = np.around(Adjmatrix, decimals=2)
#--------------END----------------#


#-------------CODE TO IMPLEMENT THE ALGORITHM----------#
count = 0
while(not converge(prev, Adjmatrix)):
    prev = Adjmatrix
    Adjmatrix = LA.matrix_power(Adjmatrix, expansion)
    Adjmatrix = np.power(Adjmatrix, inflation)
    Adjmatrix = normalize(Adjmatrix,norm = 'l1', axis =0)
    Adjmatrix = np.around(Adjmatrix, decimals=2)
    count = count + 1
# --------------------END----------------------#

print "Number of Iterations is: " + str(count)

#-------------CODE TO GENERATE CLUSTERS AND CREATE MAPPING OF NODE WITH CLUSTER------#
cluster = []
result = {}
for (i,row) in enumerate(Adjmatrix):
    temp = []
    for (j, value) in enumerate(row):
        if value > 0:
            if(not temp.__contains__(j)):
                temp.append(j)
    if(len(temp) != 0):
        cluster.append(temp)

print cluster


result1 = {}
for x in range(0,len(cluster)):
    key = x
    result[key] = cluster[x]
    key1 =  cluster[x]
    for y in key1:
        result1[y] = x
#---------------------END-----------------------#





#----------CODE FOR WRITING TO THE CLU FILE---------------#

f = open('/Users/Admin/Downloads/Data_For_HW3/result_attweb.clu', 'w')
for x in result1.keys():
        f.write(str(result1[x]) + '\n')
f.close()

#------------END-------------#

