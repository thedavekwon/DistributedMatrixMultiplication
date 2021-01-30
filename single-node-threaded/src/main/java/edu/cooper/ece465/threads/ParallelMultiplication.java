package edu.cooper.ece465.threads;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import lombok.AllArgsConstructor;

public class ParallelMultiplication {
  private static final int MINIMUM_THRESHOLD = 128;

  public static void multiply(Matrix A, Matrix B, Matrix C) throws InterruptedException {
    Thread t = new Thread(new NaiveParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow()));
    t.start();
    t.join();
  }

  @AllArgsConstructor
  private static class NaiveParallelMultiply implements Runnable {
    private Matrix A;
    private Matrix B;
    private Matrix C;
    private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public void run() {
      if (size <= MINIMUM_THRESHOLD) {
        SerialMatrixMultiplication.multiplyWithIndex(
            A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, size, size, size);
      } else {
        int newSize = size / 2;
        Matrix C1 = new Matrix(C.getRow(), C.getCol());
        Matrix C2 = new Matrix(C.getRow(), C.getCol());
        Thread[] threads =
            new Thread[] {
              new Thread(
                  new NaiveParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
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
                  new NaiveParallelMultiply(
                      A, B, C1, A_i + newSize, A_j, B_i, B_j, C_i + newSize, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
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
                  new NaiveParallelMultiply(
                      A, B, C2, A_i, A_j + newSize, B_i + newSize, B_j, C_i, C_j, newSize)),
              new Thread(
                  new NaiveParallelMultiply(
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
                  new NaiveParallelMultiply(
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
                  new NaiveParallelMultiply(
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
        for (Thread thread : threads) {
          thread.start();
        }

        for (Thread thread : threads) {
          try {
            thread.join();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        C.incrementFromMatrix(C1, C2);
      }
    }
  }
}