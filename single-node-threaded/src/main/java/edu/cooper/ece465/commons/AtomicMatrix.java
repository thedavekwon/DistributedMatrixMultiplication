package edu.cooper.ece465.commons;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

public class AtomicMatrix {
  private AtomicInteger[][] array;
  @Getter private int row;
  @Getter private int col;

  public AtomicMatrix(AtomicInteger[][] array_) {
    array = array_;
    row = array_.length;
    col = array_[0].length;
    for (int i = 1; i < row; i++) {
      // TODO
      assert array[i].length == col;
    }
  }

  public AtomicMatrix(Matrix m) {
    row = m.getRow();
    col = m.getCol();
    array = new AtomicInteger[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j] = new AtomicInteger(m.getValue(i, j));
      }
    }
  }

  public AtomicMatrix(int row_, int col_) {
    row = row_;
    col = col_;
    array = new AtomicInteger[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j] = new AtomicInteger();
      }
    }
  }

  public static AtomicMatrix createRandomMatrix(int row_, int col_) {
    AtomicInteger[][] array_ = new AtomicInteger[row_][col_];
    Random random = new Random();
    for (int i = 0; i < row_; i++) {
      for (int j = 0; j < col_; j++) {
        array_[i][j] = new AtomicInteger(random.nextInt() % 1000);
      }
    }
    return new AtomicMatrix(array_);
  }

  public static AtomicMatrix I(int row_, int col_) {
    AtomicInteger[][] array_ = new AtomicInteger[row_][col_];
    for (int i = 0; i < row_; i++) {
      for (int j = 0; j < col_; j++) {
        if (i == j) {
          array_[i][j] = new AtomicInteger(1);
        } else {
          array_[i][j] = new AtomicInteger();
        }
      }
    }
    return new AtomicMatrix(array_);
  }

  public void incrementValue(int i, int j, int val) {
    array[i][j].addAndGet(val);
  }

  public void setValue(int i, int j, int val) {
    array[i][j].set(val);
  }

  public int getValue(int i, int j) {
    return array[i][j].get();
  }
  
  public void clear() {
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j].set(0);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AtomicMatrix)) return false;
    AtomicMatrix c = (AtomicMatrix) o;
    if (row != c.getRow() || col != c.getCol()) return false;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        if (array[i][j].get() != c.getValue(i, j)) return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    String ret = new String();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        ret += array[i][j] + " ";
      }
      ret += "\n";
    }
    return ret;
  }
}
