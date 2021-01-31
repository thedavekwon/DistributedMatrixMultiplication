package edu.cooper.ece465.threadpool;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

public class ThreadPooledAtomicParallelMultiplication {
  private static final Logger LOG =
      Logger.getLogger(ThreadPooledAtomicParallelMultiplication.class);
  private static ExecutorService exec =
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public static long multiply(AtomicMatrix A, AtomicMatrix B, AtomicMatrix C) {
    Date start = new Date();
    LOG.info("ThreadPooledAtomicParallelMultiplication.multiply() - start");
    Future<?> f =
        exec.submit(new ThreadPooledAtomicParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    try {
      f.get();
      exec.shutdown();
    } catch (Exception e) {
      LOG.debug(e);
    }
    LOG.info("ThreadPooledAtomicParallelMultiplication.multiply() - end");
    Date end = new Date();
    return end.getTime() - start.getTime();
  }

  @AllArgsConstructor
  private static class ThreadPooledAtomicParallelMultiply implements Runnable {
    private AtomicMatrix A;
    private AtomicMatrix B;
    private AtomicMatrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= A.getRow() / 2) {
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
      }
    }
  }
}
