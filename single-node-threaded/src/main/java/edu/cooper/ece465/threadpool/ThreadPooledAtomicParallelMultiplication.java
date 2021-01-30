package edu.cooper.ece465.threadpool;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import lombok.AllArgsConstructor;

public class ThreadPooledAtomicParallelMultiplication {
  private static final int MINIMUM_THRESHOLD = 64;
  private static ExecutorService exec =
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public void multiply(AtomicMatrix A, AtomicMatrix B, AtomicMatrix C) {
    Future f =
        exec.submit(new ThreadPooledAtomicParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    try {
      f.get();
      exec.shutdown();
    } catch (Exception e) {

    }
  }

  @AllArgsConstructor
  private class ThreadPooledAtomicParallelMultiply implements Runnable {
    private AtomicMatrix A;
    private AtomicMatrix B;
    private AtomicMatrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= MINIMUM_THRESHOLD) {
        SerialMatrixMultiplication.multiplyWithIndex(
            A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size, size, size);
      } else {
        int newSize = size / 2;
        ThreadPooledAtomicParallelMultiply[] tasks =
            new ThreadPooledAtomicParallelMultiply[] {
              new ThreadPooledAtomicParallelMultiply(
                  A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A, B, C, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A, B, C, A_i, A_j + newSize, B_i, B_j + newSize, C_i, C_j + newSize, newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A,
                  B,
                  C,
                  A_i,
                  A_j + newSize,
                  B_i + newSize,
                  B_j + newSize,
                  C_i,
                  C_j + newSize,
                  newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A, B, C, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A,
                  B,
                  C,
                  A_i + newSize,
                  A_j + newSize,
                  B_i + newSize,
                  B_j,
                  C_i + newSize,
                  C_j,
                  newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A,
                  B,
                  C,
                  A_i + newSize,
                  A_j,
                  B_i,
                  B_j + newSize,
                  C_i + newSize,
                  C_j + newSize,
                  newSize),
              new ThreadPooledAtomicParallelMultiply(
                  A,
                  B,
                  C,
                  A_i + newSize,
                  A_j + newSize,
                  B_i + newSize,
                  B_j + newSize,
                  C_i + newSize,
                  C_j + newSize,
                  newSize)
            };
        FutureTask[] fs = new FutureTask[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
          fs[i] = new FutureTask(fs[i], null);
          exec.execute(fs[i]);
        }
        for (FutureTask f : fs) {
          f.run();
        }
        try {
          for (FutureTask f : fs) {
            f.get();
          }
        } catch (Exception e) {

        }
      }
    }
  }
}
