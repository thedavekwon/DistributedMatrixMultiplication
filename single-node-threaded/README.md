# Single Node Threaded Matrix Multiplication
Assume matrix A and B are n by b and the resultant matrix C is n by n.

### Serial Matrix Multiplication
This method is the basic method of computing a matrix, involving going through each corresponding row of A and column of B and serially summing all the multiplications. 

### Naive Parallel Matrix Multiplication
This method involves the "divide and conquer" technique that partitions matrix A and B into equal subpartitions. Each submatrix is broke down until the base case of simple elementwise multiplication is reached. Each of the results are stored in a submatrix of C. For example: C_11=A_11*B_11 + A_12*B_21, C_12 A_11*B_12 + A_12*B_22, ..., C_22=A_21*B_12 + A_22*B_22. Once all of the subcomputations of a partion of C are completed, the element in that corresponded is calculated by summing all of the previous multiplications. 

### Parallel Matrix Multiplication
This method is similar to the naive approach with one addition. Instead of all of the results being stored into a subpartion of C, another temporary matrix T is made to store the results independently. Now each subcomputation (ex: C_11=A_11*B_11 + A_12*B_21) is stored in two seperate matrices since the value of both terms is needed for the final result. Now A_11*B_11 is stored in C and A_12*B_21 is stored in T. When the necessary results are stored, the entry in the next resultant matrix can be calculated. This is done iteratively until the final resultant matrix C is computed. 

### Thread Pool Naive Parallel Matrix Multiplication
This method is the same as the previous method with the addition of more thread management. In the previous method, the number of threads needed for the entire process were all created at the same time. This leads to many context switches between the threads since there is no scheduling done. With thread pools, a specific number of threads are created per pool and dispatched by the operating system to ensure that the resources needed are availible for each thread and that the processors can handle the number of threads executed concurrently. Once an instance of a thread pool has been executed to completion, all the threads in that pool are killed and the next thread pool is executed.  
