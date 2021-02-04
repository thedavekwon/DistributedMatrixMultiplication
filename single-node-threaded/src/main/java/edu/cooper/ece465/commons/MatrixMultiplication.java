package edu.cooper.ece465.commons;

import java.util.Date;
import org.apache.log4j.Logger;

public abstract class MatrixMultiplication {
  private String type;
  protected Logger LOG = Logger.getLogger(this.getClass());

  public MatrixMultiplication(String type_) {
    type = type_;
  }

  public abstract void multiply(Matrix A, Matrix B, Matrix C);

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
