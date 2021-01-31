package edu.cooper.ece465.commons;

import java.util.Date;
import org.apache.log4j.Logger;

public class SerialMatrixMultiplication {
  private static final Logger LOG =
      Logger.getLogger(SerialMatrixMultiplication.class);
  public static long multiply(Matrix A, Matrix B, Matrix C) {
    Date start = new Date();
    LOG.debug("SerialMatrixMultiplication.multiply() - start");
    multiplyWithIndex(A, B, C, 0, 0, 0, 0, 0, 0, A.getRow(), B.getCol(), A.getCol());
    LOG.debug("SerialMatrixMultiplication.multiply() - end");
    Date end = new Date();
    LOG.info("SerialMatrixMultiplication Time taken in milli seconds: " + (end.getTime() - start.getTime()));
    return end.getTime() - start.getTime();
  }

  public static void multiplyWithIndex(
      Matrix A,
      Matrix B,
      Matrix C,
      int A_i,
      int A_j,
      int B_i,
      int B_j,
      int C_i,
      int C_j,
      int A_size,
      int B_size,
      int C_size) {
    for (int i = 0; i < A_size; i++) {
      for (int j = 0; j < B_size; j++) {
        for (int k = 0; k < C_size; k++) {
          C.incrementValue(
              C_i + i, C_j + j, A.getValue(A_i + i, A_j + k) * B.getValue(B_i + k, B_j + j));
        }
      }
    }
  }

  public static void multiplyWithIndex(
      AtomicMatrix A,
      AtomicMatrix B,
      AtomicMatrix C,
      int A_i,
      int A_j,
      int B_i,
      int B_j,
      int C_i,
      int C_j,
      int A_size,
      int B_size,
      int C_size) {
    for (int i = 0; i < A_size; i++) {
      for (int j = 0; j < B_size; j++) {
        for (int k = 0; k < C_size; k++) {
          C.incrementValue(
              C_i + i, C_j + j, A.getValue(A_i + i, A_j + k) * B.getValue(B_i + k, B_j + j));
        }
      }
    }
  }
}
