package edu.cooper.ece465;

import static org.junit.Assert.assertTrue;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
import edu.cooper.ece465.threads.NonsquareParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;
import org.junit.Before;
import org.junit.Test;

public class MatrixMultiplicationComplexTest {
  private int N = 512;
  private Matrix A;
  private Matrix B;
  private Matrix C;

  @Before
  public void setUp() {
    A = Matrix.createRandomMatrix(N, N);
    B = Matrix.I(N, N);
    C = new Matrix(N, N);
  }

  @Test
  public void SerialMatrixMultiplicationTest() {
    new SerialMatrixMultiplication().multiply(A, B, C);
    assertTrue("SerialMatrixMultiplication", A.equals(C));
  }

  @Test
  public void ParallelMultiplicationTest() throws InterruptedException {
    new ParallelMultiplication().multiply(A, B, C);
    assertTrue("ParallelMultiplication", A.equals(C));
  }

  @Test
  public void ThreadPooledNaiveParallelMultiplicationTest() {
    new ThreadPooledNaiveParallelMultiplication().multiply(A, B, C);
    assertTrue("ThreadPooledNaiveParallelMultiplication", A.equals(C));
  }

  @Test
  public void ThreadPooledParallelMultiplicationTest() {
    new ThreadPooledParallelMultiplication().multiply(A, B, C);
    assertTrue("ThreadPooledParallelMultiplication", A.equals(C));
  }

  @Test
  public void NonsqaureParallelMultiplicationTest() {
    new NonsquareParallelMultiplication().multiply(A, B, C);
    assertTrue("NonsqaureParallelMultiplication", A.equals(C));
  }
}
