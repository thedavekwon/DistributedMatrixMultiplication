package edu.cooper.ece465.commons;

import java.util.Random;
import lombok.Getter;

public class Matrix {
  private int[][] array;
  @Getter private int row;
  @Getter private int col;

  public Matrix(int[][] array_) {
    array = array_;
    row = array_.length;
    col = array_[0].length;
    for (int i = 1; i < row; i++) {
      // TODO
      assert array[i].length == col;
    }
  }

  public Matrix(int row_, int col_) {
    row = row_;
    col = col_;
    array = new int[row][col];
  }

  public static Matrix createRandomMatrix(int row_, int col_) {
    int[][] array_ = new int[row_][col_];
    Random random = new Random();
    for (int i = 0; i < row_; i++) {
      for (int j = 0; j < col_; j++) {
        array_[i][j] = random.nextInt() % 1000;
      }
    }
    return new Matrix(array_);
  }

  public static Matrix I(int row_, int col_) {
    int[][] array_ = new int[row_][col_];
    for (int i = 0; i < row_; i++) array_[i][i] = 1;
    return new Matrix(array_);
  }

  public void incrementValue(int i, int j, int val) {
    array[i][j] += val;
  }

  public void setValue(int i, int j, int val) {
    array[i][j] = val;
  }

  public int getValue(int i, int j) {
    return array[i][j];
  }

  public void clear() {
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j] = 0;
      }
    }
  }

  public Matrix add(Matrix m) {
    Matrix ret = new Matrix(row, col);
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        ret.setValue(i, j, array[i][j] + m.getValue(i, j));
      }
    }
    return ret;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Matrix)) return false;
    Matrix c = (Matrix) o;
    if (row != c.getRow() || col != c.getCol()) return false;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        if (array[i][j] != c.getValue(i, j)) return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    String ret = "\n";
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        ret += array[i][j] + " ";
      }
      ret += "\n";
    }
    return ret;
  }

  public void incrementFromMatrix(Matrix m1, Matrix m2) {
    assert row == m1.row && row == m2.row && col == m1.col && col == m2.col;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j] += m1.getValue(i, j) + m2.getValue(i, j);
      }
    }
  }
}
