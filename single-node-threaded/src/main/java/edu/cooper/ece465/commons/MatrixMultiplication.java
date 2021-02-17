package edu.cooper.ece465.commons;

import java.util.Date;
import org.apache.log4j.Logger;

public abstract class MatrixMultiplication {
  private String type;
  protected int split;
  protected Logger LOG = Logger.getLogger(this.getClass());

  public MatrixMultiplication(String type_, int split_) {
    type = type_;
    split = split_;
  }
  
  public MatrixMultiplication(String type_) {
    type = type_;
    split = 4;
  }

  public abstract void multiplyWithIndices(
      Matrix A, Matrix B, Matrix C, int A_i, int A_j, int B_i, int B_j, int C_i, int C_j, int size);

  public void multiply(Matrix A, Matrix B, Matrix C) {
    multiplyWithIndices(A, B, C, 0, 0, 0, 0, 0, 0, C.getRow());
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
