package edu.cooper.ece465.threadpool;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

public class ThreadPooledNaiveParallelMultiplication {
  private static final Logger LOG = Logger.getLogger(ThreadPooledNaiveParallelMultiplication.class);
  private static ExecutorService exec;

  public static long multiply(Matrix A, Matrix B, Matrix C) {
    exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Date start = new Date();
    LOG.debug("ThreadPooledNaiveParallelMultiplication.multiply() - start");
    Future<?> f =
        exec.submit(new ThreadPooledNaiveParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    try {
      f.get();
      exec.shutdown();
    } catch (Exception e) {
      LOG.debug(e);
    }
    LOG.debug("ThreadPooledNaiveParallelMultiplication.multiply() - end");
    Date end = new Date();
    LOG.info("ThreadPooledNaiveParallelMultiplication Time taken in milli seconds: " + (end.getTime() - start.getTime()));
    return end.getTime() - start.getTime();
  }

  @AllArgsConstructor
  private static class ThreadPooledNaiveParallelMultiply implements Runnable {
    private Matrix A;
    private Matrix B;
    private Matrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= A.getRow() / 4) {
        SerialMatrixMultiplication.multiplyWithIndex(
            A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size, size, size);
      } else {
        int newSize = size / 2;
        ThreadPooledNaiveParallelMultiply[] tasks =
            new ThreadPooledNaiveParallelMultiply[] {
              new ThreadPooledNaiveParallelMultiply(A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, newSize),
              new ThreadPooledNaiveParallelMultiply(
                  A, B, C, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize),
              new ThreadPooledNaiveParallelMultiply(
                  A, B, C, A_i, A_j + newSize, B_i, B_j + newSize, C_i, C_j + newSize, newSize),
              new ThreadPooledNaiveParallelMultiply(
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
              new ThreadPooledNaiveParallelMultiply(
                  A, B, C, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize),
              new ThreadPooledNaiveParallelMultiply(
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
              new ThreadPooledNaiveParallelMultiply(
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
              new ThreadPooledNaiveParallelMultiply(
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
        FutureTask<?>[] fs = new FutureTask[tasks.length / 2];
        for (int i = 0; i < tasks.length; i += 2) {
          fs[i / 2] = new FutureTask<Void>(new SequentialRunner(tasks[i], tasks[i + 1]), null);
          exec.execute(fs[i / 2]);
        }
        for (FutureTask<?> f : fs) {
          f.run();
        }
        for (FutureTask<?> f : fs) {
          try {
            f.get();
          } catch (Exception e) {
              LOG.error(e);
          }
        }
      }
    }
  }

  @AllArgsConstructor
  private static class SequentialRunner implements Runnable {
    private ThreadPooledNaiveParallelMultiply first, second;

    public void run() {
      first.run();
      second.run();
    }
  }
}
