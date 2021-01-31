# Class Project for ECE 465 at Cooper Union
## Description
Naive matrix multiplication involves iterating over all of the elements of matrix A and matrix B row by row to calulate specific elements in the resultant matrix C in a certain order. Each element is calculated using the following formula: c_{{ij}}=\sum _{{k=1}}^{m}a_{{ik}}b_{{kj}}. Given matrix A with nxm dimensions nad matrix B with mxp dimensions, the naive algorithm will take Θ(nmp) time. For simpler cases with square matrices, the algorithm will take Θ(n^3) time. 

The parallel matrix multiplication algorithm implemented invloves two optimizations: the "divide and conquer" method and the distribution of independent computations. The "divide and conquer" method subdivides matrix A and matrix B into equal subpartitions, if possible, creating eight multiplications of submatrices. This subdivision occurs until the base case of c11 = a11b11 occurs for all submatrices. To supplement the division, each submatrix multiplication is distributed to its own thread since each computation can be done independently of each other. Each thread executes its given multiplication and waits until all threads have finished. The corresponding elements are added together to form the resultant matrix. This optimazation in a real-world system achieves a Θ(n^2) time, a speed up from the naive algorithm.

## Usage
```
git clone https://github.com/thedavekwon/DistributedMatrixMultiplication.git
cd DistributedMatrixMultiplication
./build.sh

# Single Node Threaded
java -cp single-node-threaded/target/single-node-threaded-0.0.1-jar-with-dependencies.jar edu.cooper.ece465.App 
```
