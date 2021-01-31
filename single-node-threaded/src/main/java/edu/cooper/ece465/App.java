package edu.cooper.ece465;

import org.apache.log4j.Logger;
import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledAtomicParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
import edu.cooper.ece465.threads.NaiveAtomicParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;

public class App {
  private static final Logger LOG = Logger.getLogger(App.class);
  public static void main(String[] args) throws InterruptedException {
    for (int i = 16; i < 4096; i *= 2) {
      LOG.info("N = " + i);
      test(i);
    }
  }

  public static long[] test(int N) throws InterruptedException {
    Matrix A = Matrix.createRandomMatrix(N, N);
    Matrix B = Matrix.createRandomMatrix(N, N);
    Matrix C = new Matrix(N, N);

    long t1 = SerialMatrixMultiplication.multiply(A, B, C);

    C.clear();
    long t2 = ParallelMultiplication.multiply(A, B, C);
    LOG.info("Time taken in milli seconds: " + t2);

    C.clear();
    long t3 = ThreadPooledNaiveParallelMultiplication.multiply(A, B, C);
    LOG.info("Time taken in milli seconds: " + t3);

    // C.clear();
    // long t4 = ThreadPooledParallelMultiplication.multiply(A, B, C);
    // LOG.info("Time taken in milli seconds: " + t4);

    // AtomicMatrix AtomicA = new AtomicMatrix(A);
    // AtomicMatrix AtomicB = new AtomicMatrix(B);
    // AtomicMatrix AtomicC = new AtomicMatrix(new Matrix(N, N));

    // long t5 = NaiveAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    // LOG.info("Time taken in milli seconds: " + t5);

    // AtomicC.clear();
    // long t6 = ThreadPooledAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    // LOG.info("Time taken in milli seconds: " + t6);

    return new long[] {t1, t2, t3};
  }
}
