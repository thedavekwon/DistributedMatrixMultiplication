package edu.cooper.ece465.threadpool;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

public class ThreadPooledParallelMultiplication {
  private static final Logger LOG = Logger.getLogger(ThreadPooledParallelMultiplication.class);
  private static ExecutorService exec;

  public static long multiply(Matrix A, Matrix B, Matrix C) {
    exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Date start = new Date();
    LOG.debug("ThreadPooledParallelMultiplication.multiply() - start");
    Future<?> f =
        exec.submit(new ThreadPooledParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    try {
      f.get();
      exec.shutdown();
    } catch (Exception e) {
      LOG.error(e);
    }
    LOG.debug("ThreadPooledParallelMultiplication.multiply() - end");
    Date end = new Date();
    LOG.info("ThreadPooledParallelMultiplication Time taken in milli seconds: " + (end.getTime() - start.getTime()));
    return end.getTime() - start.getTime();
  }

  @AllArgsConstructor
  private static class ThreadPooledParallelMultiply implements Runnable {
    private Matrix A;
    private Matrix B;
    private Matrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= A.getRow() / 2) {
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
        // FutureTask<?>[] fs = new FutureTask[tasks.length];
        // for (int i = 0; i < tasks.length; i++) {
        //   fs[i] = new FutureTask<Void>(fs[i], null);
        //   exec.execute(fs[i]);
        // }
        // for (FutureTask<?> f : fs) f.run();
        // for (FutureTask<?> f : fs) {
        //   try {
        //     f.get();
        //   } catch (Exception e) {
        //     // LOG.debug(e);
        //   }
        // }
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
        // LOG.debug(C1.toString());
        // LOG.debug(C2.toString());
        // LOG.debug(C.toString());
      }
    }
  }
}
