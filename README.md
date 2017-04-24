
## CMPSCI 646 class project

Relevance Feedback has greatly improved the retrieval efficiency. Relevance Feedback involves balancing the original query terms and the feedback terms. The common approach is to fix the balance parameter across all of the queries and the dataset. But this approach can fail when the query and the feedback documents are divergent. So, the balance parameter needs to be optimized for each query and each set of feedback documents. Here, a learning approach, as proposed by Lv and Zhai([Adaptive Relevance Feedback in Information Retrieval] (http://times.cs.uiuc.edu/czhai/pub/cikm09-adptfb.pdf)), to dynamically predict the optimal value of the balance coefficient, has been implemented using RM3 and RM4 on two datasets: TREC123 and Robust04, and the values of the performance measures: MAP, nDCG and ERR have been compared with those obtained using the baseline methods: RM3 and RM4 (without using an optimal value of the balance coefficient).

### Procedure

*Feature selection*   
Out of the all the features described in the above mentioned paper, the following were used in the learning model: Clarity of Query(QEnt_R1, QEnt_R2), Clarity of the feedback documents(FBEnt_R1, FBEnt_R2), Absolute divergence between query and the feedback documents(QFBDiv_A).

*Feature extraction*   
After selecting the features, the next thing is to extract their values by statistical methods. The purpose is to have different values of these features for different queries. So, in our case, since we have chosen 5 features on which to model our learning algorithm, we need to find the values of these features for every query in the dataset. If there are n queries, we will get a matrix of n*5 dimension. This matrix will form the feature matrix which we will use in the logistic regression model. The features were determined by following the ideas and the parameters as mentioned in the paper.

*Finding an optimal value of alpha for every query* - **OptimalFB**  
We take a sample set of all the possible values of alpha from 0.1 through 0.9. We use RM3 and RM4 as our baseline models. So, for every query, we iterate through the sample set of alpha, while plugging the value of alpha in our respective relevance model and finding the values of the evaluation measures. The optimal value of alpha for each query will be the one which gives the best values of the evaluation measures. For simplicity, this base comparison was done using the values of Average Precision(AP) only. So after doing this for all the n queries in the set, we end up with a n*1 vector, which contains the optimal value of alpha for each of the n queries.

*Logistic Regression*  
We take _z = Theta*X_ as a weighted linear equation. _X_ is nothing but the feature matrix, while _z_ is related to alpha by the equation as mentioned in the paper. We already have the target vector, which is the vector of the optimal values of alpha for the n queries. In this project, [scikit-learn](http://scikit-learn.org/stable/) was used to implement logistic regression on the learning model. We used cross-validation in order to cover the whole nature of the query set. The setting for cross-validation was 80% training set and 20% testing set. Once the model is trained, we get the coefficient vector _Theta_, also we get the divergence of our predicted values from the optimal values, and an intercept value. Once this training process is done, we can simply plug in the new set of features for a query into _z = Theta*X_, and get _z_ after adding the intercept value. alpha can be obtained from the logistic regression equation mentioned in the paper.

*Using the predicted values of alpha for computation* - **AdaptFB**  
Now that we have a predicted value of alpha for each query, we simply plug in this value of alpha in the relevance models, and determine the values of the evaluation measures.

*Using a fixed value of alpha* - **FixedFB**  
For the purpose of comparison, the evaluation measures were determined by fixing the value of alpha across all the queries and all the feedback document sets.

### Comparing the values of the evaluation measures obtained from the three approaches - OptimalFB, AdaptFB and FixedFB

Significance tests can be used to compare the three approaches and figure out which approach gives the best results. Although in the project significance tests were not performed to compare the three approaches, most statistical libraries like [Apache Commons Math API](https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/stat/inference/TTest.html) have the TTest class.

### Regarding the source code

Part of the source code used in this project was provided as utility code for the [CMPSCI 646](http://people.cs.umass.edu/~jpjiang/cs646/) class offered in Fall '16.  
The two indexes(index_robust04 and index_trec123), test collections, list of queries and the list of stopwords were provided as part of the course.

### References

[1] Y. Lv and C. Zhai. Adaptive relevance feedback in information retrieval. In *Proceedings of the 18th ACM Conference on Information and Knowledge Management*, CIKM '09, pages 255-264, 2009  
[2] API design for machine learning software: experiences from the scikit-learn project, Buitinck et al., 2013  
[3] Scikit-learn: Machine Learning in Python, Pedregosa et al., JMLR 12, pp. 2825-2830, 2011
