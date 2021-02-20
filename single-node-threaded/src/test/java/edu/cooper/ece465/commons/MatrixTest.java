package edu.cooper.ece465.commons;

import static org.junit.Assert.assertTrue;

import org.javatuples.Quartet;
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
  public void split4() {
    int[][] array =
        new int[][] {
          {1, 1, 2, 2},
          {1, 1, 2, 2},
          {3, 3, 4, 4},
          {3, 3, 4, 4},
        };
    Matrix m = new Matrix(array);
    Quartet<Matrix, Matrix, Matrix, Matrix> matrices = m.split4();

    for (int i = 1; i < 5; i++) {
      Matrix t = (Matrix) matrices.getValue(i - 1);
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 2; k++) {
          assertTrue(t.getValue(j, k) == i);
        }
      }
    }
  }
}
