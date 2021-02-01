# Class Project for ECE 465 at Cooper Union
## Description
This project is splitted up into three different parts. The first single-node-threaded contains a single node multi threaded computation of matrix multiplication.


## Algorithm Description
Naive matrix multiplication involves iterating over all of the elements of matrix A and matrix B row by row to calulate specific elements in the resultant matrix C in a certain order. Each element is calculated using the following formula:

<img src="https://latex.codecogs.com/svg.latex?\Large&space;c_{i,%20j}=\sum_{k=1}^{m}a_{i,k}b_{k,j}" />

Given matrix A with [n x m] dimensions nad matrix B with [m x p]  dimensions, the naive algorithm will take Θ(n*m*p) time. For simpler cases with square matrices, the algorithm will take Θ(n^3) time. 


The parallel matrix multiplication algorithm implemented invloves two optimizations: the "divide and conquer" method and the distribution of independent computations. The "divide and conquer" method subdivides matrix A and matrix B into equal subpartitions, if possible, creating eight multiplications of submatrices. 

<img src="https://wikimedia.org/api/rest_v1/media/math/render/svg/cdaaee9668d62d9a95660308b76ff583b54a46bd" title="Matrix Split"> [[1]](#1)

This subdivision occurs until the base case of C = A x B occurs for all  submatrices when certain threshold is met up. To supplement the division, each submatrix multiplication is distributed to its own thread since each computation can be done independently of each other. Each thread executes its given multiplication and waits until all threads have finished. The corresponding elements are added together to form the resultant matrix. 
In the ideal case, the maximum possible speedup is Θ(n^3/log^2n) with a temporary matrix and unlimited amount of threads. More realistically, this optimazation achieves a Θ(n^2) time without using a temporary matrix, a speed up from the naive algorithm.

## Usage
```
git clone https://github.com/thedavekwon/DistributedMatrixMultiplication.git
cd DistributedMatrixMultiplication
./build.sh

# Single Node Threaded
java -cp single-node-threaded/target/single-node-threaded-0.0.1-jar-with-dependencies.jar edu.cooper.ece465.App 
```

## References
<a id="1">[1]</a> 
https://en.wikipedia.org/wiki/Matrix_multiplication_algorithm#Shared-memory_parallelism
