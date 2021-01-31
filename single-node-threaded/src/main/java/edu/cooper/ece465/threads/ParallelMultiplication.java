package edu.cooper.ece465.threads;

import java.util.Date;
import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

public class ParallelMultiplication {
  private static final Logger LOG = Logger.getLogger(ParallelMultiplication.class);
  public static long multiply(Matrix A, Matrix B, Matrix C) throws InterruptedException {
    Date start = new Date();
    Thread t = new Thread(new ParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    LOG.info("ParallelMultiplication.multiply() - start");
    t.start();
    t.join();
    LOG.info("ParallelMultiplication.multiply() - end");
    Date end = new Date();
    return end.getTime() - start.getTime();
  }

  @AllArgsConstructor
  private static class ParallelMultiply implements Runnable {
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
        Matrix C1 = new Matrix(C.getRow(), C.getCol());
        Matrix C2 = new Matrix(C.getRow(), C.getCol());
        Thread[] threads =
            new Thread[] {
              new Thread(new ParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, newSize)),
              new Thread(
                  new ParallelMultiply(
                      A,
                      B,
                      C1,
                      A_i,
                      A_j + newSize,
                      B_i,
                      B_j + newSize,
                      C_i,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new ParallelMultiply(
                      A, B, C1, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize)),
              new Thread(
                  new ParallelMultiply(
                      A,
                      B,
                      C1,
                      A_i + newSize,
                      A_j,
                      B_i,
                      B_j + newSize,
                      C_i + newSize,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new ParallelMultiply(
                      A, B, C2, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize)),
              new Thread(
                  new ParallelMultiply(
                      A,
                      B,
                      C2,
                      A_i,
                      A_j + newSize,
                      B_i + newSize,
                      B_j + newSize,
                      C_i,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new ParallelMultiply(
                      A,
                      B,
                      C2,
                      A_i + newSize,
                      A_j + newSize,
                      B_i + newSize,
                      B_j,
                      C_i + newSize,
                      C_j,
                      newSize)),
              new Thread(
                  new ParallelMultiply(
                      A,
                      B,
                      C2,
                      A_i + newSize,
                      A_j + newSize,
                      B_i + newSize,
                      B_j + newSize,
                      C_i + newSize,
                      C_j + newSize,
                      newSize))
            };
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) {
          try {
            thread.join();
          } catch (InterruptedException e) {
            LOG.debug(e);
            e.printStackTrace();
          }
        }
        C.incrementFromMatrix(C1, C2);
      }
    }
  }
}
