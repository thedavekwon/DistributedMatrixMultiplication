package edu.cooper.ece465.commons;

import edu.cooper.ece465.commons.Matrix.MatrixIndexes;
import java.util.Date;
import org.apache.log4j.Logger;

public abstract class MatrixMultiplication {
  private String type;
  protected Logger LOG = Logger.getLogger(this.getClass());

  public MatrixMultiplication(String type_) {
    type = type_;
  }

  public abstract void multiplyWithIndexes(
      Matrix A, Matrix B, Matrix C, int A_i, int A_j, int B_i, int B_j, int C_i, int C_j, int size);

  public void multiplyWithMatrixIndexes(Matrix A, Matrix B, Matrix C, MatrixIndexes indexes) {
    multiplyWithIndexes(
        A,
        B,
        C,
        indexes.getA_i(),
        indexes.getA_j(),
        indexes.getB_i(),
        indexes.getB_j(),
        indexes.getC_i(),
        indexes.getC_j(),
        indexes.getSize());
  }

  public void multiply(Matrix A, Matrix B, Matrix C) {
    multiplyWithIndexes(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow());
  }

  public long multiplyWithTimeMeasure(Matrix A, Matrix B, Matrix C) {
    Date start = new Date();
    LOG.debug(type + ".multiply() - start");
    multiply(A, B, C);
    LOG.debug(type + ".multiply() - end");
    Date end = new Date();
    LOG.info(type + " Time taken in milli seconds: " + (end.getTime() - start.getTime()));
    return end.getTime() - start.getTime();
  }
}
