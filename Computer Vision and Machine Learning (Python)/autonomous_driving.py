## ---------------------------- ##
##
## Example student submission code for autonomous driving challenge.
## You must modify the train and predict methods and the NeuralNetwork class. 
## 
## ---------------------------- ##

import numpy as np
import cv2

def train(path_to_images, csv_file):
    '''
    First method you need to complete. 
    Args: 
    path_to_images = path to jpg image files
    csv_file = path and filename to csv file containing frame numbers and steering angles. 
    Returns: 
    NN = Trained Neural Network object 
    '''

    # You may make changes here if you wish. 
    # Import Steering Angles CSV
    data = np.genfromtxt(csv_file, delimiter = ',')
    frame_nums = data[:,0]
    steering_angles = data[:,1]
    
    #normalize data
    steering_angles = (steering_angles - steering_angles.min()) / (steering_angles.max() - steering_angles.min())
    
    #create y data as 64 element array with probability range at index determined by bins 
    binResults = np.linspace(steering_angles.min(),steering_angles.max(),64) #evenly sequenced angle possibilities
    rangeY = np.linspace(.1,.9,5) #probabilities (far->close)
    bins = []
    for i in range(steering_angles.size):
        value = 0
        for k in range(binResults.shape[0]):
            if steering_angles[i] >= binResults[k]: value = k #set index
        listY = np.zeros(64)
        listY[value] = 1
        for k in range(4):  #set nearby elements to probabilities if possible
            if(k + value < 64 and k + value >= 0):
                listY[k+value] = rangeY[k]
            if(value - k < 64 and value - k >= 0):
                listY[value-k] = rangeY[k]       
        bins.append(listY)
    y = np.asarray(bins)
    
    #Proccess images
    imageData = []
    for i in range(frame_nums.shape[0]):
        frame_num = int(frame_nums[i])
        im_full = cv2.imread(path_to_images + '/' + str(int(frame_num)).zfill(4) + '.jpg')
        im_full = cv2.resize(im_full, (64,64)) #Size dependent on input and output layer size of NN
        im_full = apply_alv_vision(im_full,.7) #Mask of road (hopefully)
        im_full = np.hstack(im_full)
        imageData.append(im_full)
    
    #Normalize Data
    imageData = np.asarray(imageData)
    X = imageData/255 #Throws overflow errors when divided by imageData.max(), (which is not 255!)

    #Set up network features
    N = Neural_Network()  
    params = N.getParams()
    iterations = 30
    rate = 0.7
    
    #Training
    for i in range(iterations):
        if i == 2: rate = .5
        elif i == 15: rate = .3
        for testx,testy in split(X,y,20): #Sliding over data
            grads = N.computeGradients(testx,testy)
            N.setParams(params - (rate * grads))
            params = N.getParams()
    
    return N
    
def alv_vision(image, rgb, thresh):
    return (np.dot(image.reshape(-1, 3), rgb) > thresh).reshape(image.shape[0], image.shape[1])    
    
def apply_alv_vision(im, thresh):    
    return alv_vision(im, rgb = [-1, 0, 1], thresh = thresh) #Blue-Red

def split(X, y, size):
    for i in range(X.shape[0] - size):
        yield (X[i:(i + size)], y[i:(i + size)]) #yield one new feature at a time, minus one old feature

def predict(NN, image_file):
    '''
    Second method you need to complete. 
    Given an image filename, load image, make and return predicted steering angle in degrees. 
    '''
    #process image the same way as training images
    im_full = cv2.imread(image_file)
    im_full = cv2.resize(im_full, (64,64))
    im_full = apply_alv_vision(im_full,.7)
    im_full = np.hstack(im_full)
    im = (np.asarray(im_full))/255
    
    #make guess based on bin index
    binResults = np.linspace(-165,23,64)
    guess = NN.forward(im)
    angle = binResults[np.argmax(guess)]
    
    return angle

class Neural_Network(object):
    def __init__(self):        
        #Define Hyperparameters
        self.inputLayerSize = 4096
        self.outputLayerSize = 64
        self.hiddenLayerSize = 32 #works well at half outputlayer
        
        #Weights (parameters)
        self.W1 = np.random.randn(self.inputLayerSize,self.hiddenLayerSize)
        self.W2 = np.random.randn(self.hiddenLayerSize,self.outputLayerSize)
        
    def forward(self, X):
        #Propogate inputs though network
        self.z2 = np.dot(X, self.W1)
        self.a2 = self.sigmoid(self.z2)
        self.z3 = np.dot(self.a2, self.W2)
        yHat = self.sigmoid(self.z3) 
        return yHat
        
    def sigmoid(self, z):
        #Apply sigmoid activation function to scalar, vector, or matrix
        return 1/(1+np.exp(-z))
    
    def sigmoidPrime(self,z):
        #Gradient of sigmoid
        return np.exp(-z)/((1+np.exp(-z))**2)
    
    def costFunction(self, X, y):
        #Compute cost for given X,y, use weights already stored in class.
        self.yHat = self.forward(X)
        J = 0.5*sum((y-self.yHat)**2)
        return J
        
    def costFunctionPrime(self, X, y):
        #Compute derivative with respect to W and W2 for a given X and y:
        self.yHat = self.forward(X)
        
        delta3 = np.multiply(-(y-self.yHat), self.sigmoidPrime(self.z3))
        dJdW2 = np.dot(self.a2.T, delta3)
        
        delta2 = np.dot(delta3, self.W2.T)*self.sigmoidPrime(self.z2)
        dJdW1 = np.dot(X.T, delta2)  
        
        return dJdW1, dJdW2
    
    #Helper Functions for interacting with other classes:
    def getParams(self):
        #Get W1 and W2 unrolled into vector:
        params = np.concatenate((self.W1.ravel(), self.W2.ravel()))
        return params
    
    def setParams(self, params):
        #Set W1 and W2 using single paramater vector.
        W1_start = 0
        W1_end = self.hiddenLayerSize * self.inputLayerSize
        self.W1 = np.reshape(params[W1_start:W1_end], (self.inputLayerSize , self.hiddenLayerSize))
        W2_end = W1_end + self.hiddenLayerSize*self.outputLayerSize
        self.W2 = np.reshape(params[W1_end:W2_end], (self.hiddenLayerSize, self.outputLayerSize))
        
    def computeGradients(self, X, y):
        dJdW1, dJdW2 = self.costFunctionPrime(X, y)
        return np.concatenate((dJdW1.ravel(), dJdW2.ravel()))