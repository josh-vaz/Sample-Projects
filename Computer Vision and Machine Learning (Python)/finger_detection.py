## ---------------------------- ##
## 
## sample_student.py
##
## Example student submission for programming challenge. A few things: 
## 1. Before submitting, change the name of this file to your firstname_lastname.py.
## 2. Be sure not to change the name of the method below, count_fingers.py
## 3. In this challenge, you are only permitted to import numpy, and methods from 
##    the util module in this repository. Note that if you make any changes to your local 
##    util module, these won't be reflected in the util module that is imported by the 
##    auto grading algorithm. 
## 4. Anti-plagarism checks will be run on your submission
##
##
## ---------------------------- ##


import numpy as np
import sys
#It's ok to import whatever you want from the local util module if you would like:
sys.path.append('../util')
from data_handling import breakIntoGrids, reshapeIntoImage

def count_fingers(im):
    '''
    Example submission for coding challenge. 
    
    Args: im (nxm) unsigned 8-bit grayscale image 
    Returns: One of three integers: 1, 2, 3
    
    '''

    ## ------ Input Pipeline Develped in this Module ----- ##
    #You may use the finger pixel detection pipeline we developed in this module:
    #You may also replace this code with your own pipeline if you prefer
    im = im > 92 #Threshold image
    X = breakIntoGrids(im, s = 9) #Break into 9x9 grids

    #Use rule we learned with decision tree
    treeRule1 = lambda X: np.logical_and(np.logical_and(X[:, 40] == 1, X[:,0] == 0), X[:, 53] == 0)
    yhat = treeRule1(X)

    #Reshape prediction ino image:
    yhat_reshaped = reshapeIntoImage(yhat, im.shape)

    ## ----- Your Code Here ---- ##
    
    #get all x values of fingers
    ListN = []
    for n in yhat_reshaped:
        fNum = -1
        for f in n:
            fNum +=1
            if(f > 0): 
                ListN.append(fNum)
                
    #remove bottom 45 image values to avoid counting non-fingers
    ListN = ListN[:len(ListN)-60]

    #sort array
    ListN.sort();
    
    #if elements before is equal or 1 different, belongs to same fing
    count = 1;
    size = 0;
    for i in range(1,len(ListN) - 1):
        if(ListN[i] != ListN[i - 1] and ListN[i] != (ListN[i - 1] + 1) and ListN[i] != (ListN[i - 1] - 1)):
            if(size > 10):    #only add if finger blob is at least 10 pixels big
                count+=1;
                size = 0;
        else: size+=1;
    
    if(count > 3): count = 3;

    
    return count