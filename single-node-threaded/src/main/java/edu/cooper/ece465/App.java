package edu.cooper.ece465;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledAtomicParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
import edu.cooper.ece465.threads.NaiveAtomicParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;

public class App {
  public static void main(String[] args) throws InterruptedException {
    
  }

  public long[] test(int N) throws InterruptedException {
    Matrix A = Matrix.createRandomMatrix(N, N);
    Matrix B = Matrix.createRandomMatrix(N, N);
    Matrix C = new Matrix(N, N);

    long t1 = SerialMatrixMultiplication.multiply(A, B, C);
    System.out.println("Time taken in milli seconds: " + t1);

    C.clear();
    long t2 = ParallelMultiplication.multiply(A, B, C);
    System.out.println("Time taken in milli seconds: " + t2);

    C.clear();
    long t3 = ThreadPooledNaiveParallelMultiplication.multiply(A, B, C);
    System.out.println("Time taken in milli seconds: " + t3);

    C.clear();
    long t4 = ThreadPooledParallelMultiplication.multiply(A, B, C);
    System.out.println("Time taken in milli seconds: " + t4);

    AtomicMatrix AtomicA = new AtomicMatrix(A);
    AtomicMatrix AtomicB = new AtomicMatrix(B);
    AtomicMatrix AtomicC = new AtomicMatrix(new Matrix(N, N));

    long t5 = NaiveAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    System.out.println("Time taken in milli seconds: " + t5);

    AtomicC.clear();
    long t6 = ThreadPooledAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    System.out.println("Time taken in milli seconds: " + t6);

    return new long[] {t1, t2, t3, t4, t5, t6};
  }
}
