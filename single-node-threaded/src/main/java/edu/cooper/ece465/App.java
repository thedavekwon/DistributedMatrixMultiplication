package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
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
    writer.write("N, Serial, Parallel, ThreadPooledNaiveParallel, ThreadPooledParallel\n");
    for (int i = 1; i < 130; i += 10) {
      LOG.info("N = " + 16 * i);
      test(16 * i);
    }
    writer.close();
  }

  public static long[] test(int N) throws InterruptedException, IOException {
    Matrix A = Matrix.createRandomMatrix(N, N);
    Matrix B = Matrix.createRandomMatrix(N, N);
    Matrix C = new Matrix(N, N);

    long t1 = new SerialMatrixMultiplication().multiplyWithTimeMeasure(A, B, C);

    C.clear();
    long t2 = new ParallelMultiplication().multiplyWithTimeMeasure(A, B, C);

    C.clear();
    long t3 = new ThreadPooledNaiveParallelMultiplication().multiplyWithTimeMeasure(A, B, C);

    C.clear();
    long t4 = new ThreadPooledParallelMultiplication().multiplyWithTimeMeasure(A, B, C);

    if (writer != null) {
      writer.write(N + ", " + t1 + ", " + t2 + ", " + t3 + ", " + t4 + "\n");
    }
    return new long[] {t1, t2, t3, t4};
  }
}
