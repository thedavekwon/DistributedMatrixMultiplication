package edu.cooper.ece465;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledAtomicParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
import edu.cooper.ece465.threads.NaiveAtomicParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;
import java.util.Date;

public class App {
  public static void main(String[] args) throws InterruptedException {
    int N = 8;

    // Matrix A = Matrix.createRandomMatrix(N, N);
    // Matrix B = Matrix.createRandomMatrix(N, N);
    Matrix A = Matrix.I(N, N);
    Matrix B = Matrix.I(N, N);
    Matrix C = new Matrix(N, N);

    Date start = new Date();
    SerialMatrixMultiplication.multiply(A, B, C);
    Date end = new Date();
    System.out.println(A.equals(C));
    System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));

    C.clear();
    start = new Date();
    ParallelMultiplication.multiply(A, B, C);
    end = new Date();
    System.out.println(A.equals(C));
    System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));

    C.clear();
    start = new Date();
    new ThreadPooledNaiveParallelMultiplication().multiply(A, B, C);
    end = new Date();
    System.out.println(A.equals(C));
    System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));

    // C.clear();
    // start = new Date();
    // new ThreadPooledParallelMultiplication().multiply(A, B, C);
    // end = new Date();
    // System.out.println(A.equals(C));
    // System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));

    // AtomicMatrix AtomicA = new AtomicMatrix(A);
    // AtomicMatrix AtomicB = new AtomicMatrix(B);
    // AtomicMatrix AtomicC = new AtomicMatrix(new Matrix(N, N));

    // start = new Date();
    // NaiveAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    // end = new Date();
    // System.out.println(AtomicA.equals(AtomicC));
    // System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));

    // AtomicC.clear();
    // start = new Date();
    // new ThreadPooledAtomicParallelMultiplication().multiply(AtomicA, AtomicB, AtomicC);
    // end = new Date();
    // System.out.println(AtomicA.equals(AtomicC));
    // System.out.println("Time taken in milli seconds: " + (end.getTime() - start.getTime()));
  }
}
