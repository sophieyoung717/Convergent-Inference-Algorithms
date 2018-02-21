# Convergent Inference Algorithms in Probabilistic Graphical Model   

- Proposed a new family of inference algorithms called Convergent Inference algorithms (CIAs), which enjoy the benefits of both exact and approximate inference algorithms by providing approximate results over the course of inference, and eventually converging to an exact inference result.  My contribution involves:- Proposed a base CIA that performs pseudo-random sampling without replacement using Linear Congruential Generators. - Proposed a new online aggregation algorithm called Leaky Joins that produces samples of a query?s result in the course of normally evaluating the query.- Provided analysis of time complexity and confidence bounds for CIAs.- Generalized the algorithms for any aggregate queries over small but dense tables.- Implemented Variable Elimination, Belief Propagation and Gibbs Sampling algorithms as comparisons.

# How to run:

- compile all .java files.
- Input graph can be downloaded from: [Bayesian Network Repository] (http://www.bnlearn.com/bnrepository/), example: child.dsc
- For child graph, run Main_Child.java
- Result is in 4 .txt files with name at the beginning of Main_Child.java
- For more details, see paper: https://openproceedings.org/2017/conf/edbt/paper-171.pdf