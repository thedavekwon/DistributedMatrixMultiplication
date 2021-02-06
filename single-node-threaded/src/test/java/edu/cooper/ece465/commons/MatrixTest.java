package edu.cooper.ece465.commons;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MatrixTest {
  private static int N = 4;

  @Test
  public void equals() {
    int[][] array =
        new int[][] {
          {1, 0, 0, 0},
          {0, 1, 0, 0},
          {0, 0, 1, 0},
          {0, 0, 0, 1}
        };
    Matrix A = new Matrix(array);
    Matrix B = new Matrix(array);
    assertTrue("equals", A.equals(B));
  }


  @Test
  public void I() {
    int[][] array =
        new int[][] {
          {1, 0, 0, 0},
          {0, 1, 0, 0},
          {0, 0, 1, 0},
          {0, 0, 0, 1}
        };
    Matrix A = new Matrix(array);
    Matrix B = Matrix.I(4, 4);
    assertTrue("I", A.equals(B));
  }

  @Test
  public void incrementFromMatrices() {
    int[][] arrayA =
        new int[][] {
          {1, 1, 1, 1},
          {1, 1, 1, 1},
          {1, 1, 1, 1},
          {1, 1, 1, 1},
        };
    int[][] arrayB =
        new int[][] {
          {2, 2, 2, 2},
          {2, 2, 2, 2},
          {2, 2, 2, 2},
          {2, 2, 2, 2},
        };
    Matrix A = new Matrix(arrayA);
    Matrix B = new Matrix(arrayB);
    Matrix C = new Matrix(4, 4);
    C.incrementFromMatrices(A, B);

    assertTrue(
        "incrementFromMatrices",
        C.equals(
            new Matrix(
                new int[][] {
                  {3, 3, 3, 3},
                  {3, 3, 3, 3},
                  {3, 3, 3, 3},
                  {3, 3, 3, 3},
                })));
  }

  @Test
  public void incrementFromMatricesII() {
    int[][] arrayA =
        new int[][] {
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
          {1, 1, 1, 1, 1},
        };
    int[][] arrayB =
        new int[][] {
          {2, 2, 2, 2, 2},
          {2, 2, 2, 2, 2},
          {2, 2, 2, 2, 2},
          {2, 2, 2, 2, 2},
        };
    Matrix A = new Matrix(arrayA);
    Matrix B = new Matrix(arrayB);
    Matrix C = new Matrix(4, 5);
    C.incrementFromMatrices(A, B);

    assertTrue(
        "incrementFromMatricesII",
        C.equals(
            new Matrix(
                new int[][] {
                  {3, 3, 3, 3, 3},
                  {3, 3, 3, 3, 3},
                  {3, 3, 3, 3, 3},
                  {3, 3, 3, 3, 3},
                })));
  }

}