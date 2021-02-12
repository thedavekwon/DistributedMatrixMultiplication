package edu.cooper.ece465.commons;

import com.google.protobuf.ByteString;
import edu.cooper.ece465.Indexes;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Matrix implements Serializable {
  private static final long serialVersionUID = 1L;
  private int[][] array;
  @Getter private int row;
  @Getter private int col;

  public Matrix(int[][] array_) {
    array = array_;
    row = array_.length;
    col = array_[0].length;
    for (int i = 1; i < row; i++) {
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

  public static Matrix like(Matrix m) {
    return new Matrix(m.getRow(), m.getCol());
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

  public ByteString toByteString() throws IOException {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    ObjectOutputStream o = new ObjectOutputStream(b);
    o.writeObject(this);
    ByteString data = ByteString.copyFrom(b.toByteArray());
    b.close();
    o.close();
    return data;
  }

  public static Matrix fromByteString(ByteString data) throws IOException, ClassNotFoundException {
    ByteArrayInputStream b = new ByteArrayInputStream(data.toByteArray());
    ObjectInputStream o = new ObjectInputStream(b);
    Matrix matrix = (Matrix) o.readObject();
    b.close();
    o.close();
    return matrix;
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

  public void incrementFromMatrices(Matrix m1, Matrix m2) {
    assert row == m1.row && row == m2.row && col == m1.col && col == m2.col;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        array[i][j] += m1.getValue(i, j) + m2.getValue(i, j);
      }
    }
  }

  public void incrementFromMatrixIndexes(Matrix m, MatrixIndexes indexes) {
    for (int i = indexes.getC_i(); i < indexes.getC_i() + indexes.getSize() / 2; i++) {
      for (int j = indexes.getC_j(); j < indexes.getC_j() + indexes.getSize() / 2; j++) {
        array[i][j] += m.getValue(i, j);
      }
    }
  }

  @AllArgsConstructor
  public static class MatrixIndexes {
    @Getter private int A_i, A_j, B_i, B_j, C_i, C_j, size;

    public static MatrixIndexes fromIndexes(Indexes indexes) {
      return new MatrixIndexes(
          indexes.getAI(),
          indexes.getAJ(),
          indexes.getBI(),
          indexes.getBJ(),
          indexes.getCI(),
          indexes.getCJ(),
          indexes.getSize());
    }

    public Indexes toIndexes() {
      return Indexes.newBuilder()
          .setAI(A_i)
          .setAJ(A_j)
          .setBI(B_i)
          .setBJ(B_j)
          .setCI(C_i)
          .setCJ(C_j)
          .setSize(size)
          .build();
    }

    @Override
    public String toString() {
      return A_i + ", " + A_j + ", " + B_i + ", " + B_j + ", " + C_i + ", " + C_j + ", " + size;
    }
  }
}
