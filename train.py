import numpy
import numpy as np
from sklearn.linear_model import LogisticRegression

np.random.seed(2016)

import os
import pickle
import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import StandardScaler
from sklearn.externals import joblib
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

def load_datafile():
    data = pd.read_csv("Data.csv")
    a = data.pop('z')
    y = data.pop('alpha') 
    y = np.round(y)
    encoder = LabelEncoder().fit(y)
    y = encoder.transform(y)
    scaler = StandardScaler().fit(data)
    X = scaler.transform(data)
    X,y = shuffle(X,y)
    X_train, X_valid, Y_train, Y_valid = train_test_split(X,y,test_size=0.2, random_state=0)
    return X_train, X_valid, Y_train, Y_valid,encoder, scaler

def run_regre():
    X_train, X_valid, Y_train, Y_valid, encoder, scaler = load_datafile()
    clf = LogisticRegression(max_iter=1000, fit_intercept=True) 
    clf.fit(X_train, Y_train)
    print clf.coef_
    print clf.score(X_valid, Y_valid)
    print clf.intercept_
    
if __name__ == '__main__':
    run_regre()
   