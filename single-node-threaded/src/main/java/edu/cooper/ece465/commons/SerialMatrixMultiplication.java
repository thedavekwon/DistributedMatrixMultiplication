package edu.cooper.ece465.commons;

public class SerialMatrixMultiplication {
  public static void multiply(Matrix A, Matrix B, Matrix C) {
    multiplyWithIndex(A, B, C, 0, 0, 0, 0, 0, 0, A.getRow(), B.getCol(), A.getCol());
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
