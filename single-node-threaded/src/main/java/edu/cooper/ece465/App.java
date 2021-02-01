package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class App {
  private static BufferedWriter writer;
  private static final Logger LOG = Logger.getLogger(App.class);

  public static void main(String[] args) throws InterruptedException, IOException {
    writer = new BufferedWriter(new FileWriter("output.csv"));
    writer.write("N, Serial, Parallel, ThreadPooledNaiveParallel\n");
    for (int i = 1; i < 130; i += 10) {
      LOG.info("N = " + 16*i);
      test(16*i);
    }
    writer.close();
  }

  public static long[] test(int N) throws InterruptedException, IOException {
    Matrix A = Matrix.createRandomMatrix(N, N);
    Matrix B = Matrix.createRandomMatrix(N, N);
    Matrix C = new Matrix(N, N);

    long t1 = SerialMatrixMultiplication.multiply(A, B, C);

    C.clear();
    long t2 = ParallelMultiplication.multiply(A, B, C);

    C.clear();
    long t3 = ThreadPooledNaiveParallelMultiplication.multiply(A, B, C);

    // C.clear();
    // long t4 = ThreadPooledParallelMultiplication.multiply(A, B, C);

    // AtomicMatrix AtomicA = new AtomicMatrix(A);
    // AtomicMatrix AtomicB = new AtomicMatrix(B);
    // AtomicMatrix AtomicC = new AtomicMatrix(new Matrix(N, N));

    // long t5 = NaiveAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);

    // AtomicC.clear();
    // long t6 = ThreadPooledAtomicParallelMultiplication.multiply(AtomicA, AtomicB, AtomicC);
    writer.write(N + ", " + t1 + ", " + t2 + ", " + t3 + "\n");
    return new long[] {t1, t2, t3};
  }
}
