package edu.cooper.ece465.threadpool;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.MatrixMultiplication;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.AllArgsConstructor;

public class ThreadPooledParallelMultiplication extends MatrixMultiplication {
  private ExecutorService exec = Executors.newWorkStealingPool();

  public ThreadPooledParallelMultiplication() {
    super(ThreadPooledParallelMultiplication.class.toString());
  }

  @Override
  public void multiplyWithIndices(
      Matrix A,
      Matrix B,
      Matrix C,
      int A_i,
      int A_j,
      int B_i,
      int B_j,
      int C_i,
      int C_j,
      int size) {
    // exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Future<?> f =
        exec.submit(new ThreadPooledParallelMultiply(A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size));
    try {
      f.get();
      exec.shutdown();
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  @AllArgsConstructor
  private class ThreadPooledParallelMultiply implements Runnable {
    private Matrix A;
    private Matrix B;
    private Matrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= A.getRow() / split) {
        SerialMatrixMultiplication.multiplyWithIndex(
            A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size, size, size);
      } else {
        int newSize = size / 2;
        Matrix C1 = new Matrix(C.getRow(), C.getCol());
        Matrix C2 = new Matrix(C.getRow(), C.getCol());
        ThreadPooledParallelMultiply[] tasks =
            new ThreadPooledParallelMultiply[] {
              new ThreadPooledParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, newSize),
              new ThreadPooledParallelMultiply(
                  A, B, C2, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize),
              new ThreadPooledParallelMultiply(
                  A, B, C1, A_i, A_j + newSize, B_i, B_j + newSize, C_i, C_j + newSize, newSize),
              new ThreadPooledParallelMultiply(
                  A,
                  B,
                  C2,
                  A_i,
                  A_j + newSize,
                  B_i + newSize,
                  B_j + newSize,
                  C_i,
                  C_j + newSize,
                  newSize),
              new ThreadPooledParallelMultiply(
                  A, B, C1, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize),
              new ThreadPooledParallelMultiply(
                  A,
                  B,
                  C2,
                  A_i + newSize,
                  A_j + newSize,
                  B_i + newSize,
                  B_j,
                  C_i + newSize,
                  C_j,
                  newSize),
              new ThreadPooledParallelMultiply(
                  A,
                  B,
                  C1,
                  A_i + newSize,
                  A_j,
                  B_i,
                  B_j + newSize,
                  C_i + newSize,
                  C_j + newSize,
                  newSize),
              new ThreadPooledParallelMultiply(
                  A,
                  B,
                  C2,
                  A_i + newSize,
                  A_j + newSize,
                  B_i + newSize,
                  B_j + newSize,
                  C_i + newSize,
                  C_j + newSize,
                  newSize)
            };
        Future<?>[] fs = new Future[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
          fs[i] = exec.submit(tasks[i]);
        }
        try {
          for (Future<?> f : fs) {
            f.get();
          }
        } catch (Exception e) {
          LOG.error(e);
        }
        C.incrementFromMatrices(C1, C2);
      }
    }
  }
}
