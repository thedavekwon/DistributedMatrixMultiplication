package edu.cooper.ece465.threads;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import lombok.AllArgsConstructor;
import java.util.Date;
import org.apache.log4j.Logger;

public class NaiveAtomicParallelMultiplication {
  private static final Logger LOG = Logger.getLogger(NaiveAtomicParallelMultiplication.class);

  public static long multiply(AtomicMatrix A, AtomicMatrix B, AtomicMatrix C)
      throws InterruptedException {
    Date start = new Date();
    LOG.debug("NaiveAtomicParallelMultiplication.multiply() - start");
    Thread t = new Thread(new NaiveParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    t.start();
    t.join();
    LOG.debug("NaiveAtomicParallelMultiplication.multiply() - end");
    Date end = new Date();
    LOG.info("NaiveAtomicParallelMultiplication Time taken in milli seconds: " + (end.getTime() - start.getTime()));
    return end.getTime() - start.getTime();
  }

  @AllArgsConstructor
  private static class NaiveParallelMultiply implements Runnable {
    private AtomicMatrix A;
    private AtomicMatrix B;
    private AtomicMatrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= A.getRow() / 4) {
        SerialMatrixMultiplication.multiplyWithIndex(
            A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size, size, size);
      } else {
        int newSize = size / 2;
        Thread[] threads =
            new Thread[] {
              new Thread(new NaiveParallelMultiply(A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A, B, C, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A,
                      B,
                      C,
                      A_i,
                      A_j + newSize,
                      B_i,
                      B_j + newSize,
                      C_i,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A,
                      B,
                      C,
                      A_i,
                      A_j + newSize,
                      B_i + newSize,
                      B_j + newSize,
                      C_i,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A, B, C, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A,
                      B,
                      C,
                      A_i + newSize,
                      A_j + newSize,
                      B_i + newSize,
                      B_j,
                      C_i + newSize,
                      C_j,
                      newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A,
                      B,
                      C,
                      A_i + newSize,
                      A_j,
                      B_i,
                      B_j + newSize,
                      C_i + newSize,
                      C_j + newSize,
                      newSize)),
              new Thread(
                  new NaiveParallelMultiply(
                      A,
                      B,
                      C,
                      A_i + newSize,
                      A_j + newSize,
                      B_i + newSize,
                      B_j + newSize,
                      C_i + newSize,
                      C_j + newSize,
                      newSize))
            };
        for (Thread thread : threads) {
          thread.start();
        }
        for (Thread thread : threads) {
          try {
            thread.join();
          } catch (InterruptedException e) {
            LOG.error(e);
            e.printStackTrace();
          }
        }
      }
    }
  }
}
