package edu.cooper.ece465;

import static org.junit.Assert.assertTrue;

import edu.cooper.ece465.commons.AtomicMatrix;
import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledAtomicParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledNaiveParallelMultiplication;
import edu.cooper.ece465.threadpool.ThreadPooledParallelMultiplication;
import edu.cooper.ece465.threads.NaiveAtomicParallelMultiplication;
import edu.cooper.ece465.threads.ParallelMultiplication;
import org.junit.Before;
import org.junit.Test;

public class MatrixMultiplicationTest {
  private int N = 32;
  private Matrix A;
  private Matrix B;
  private Matrix C;
  private AtomicMatrix AA;
  private AtomicMatrix AB;
  private AtomicMatrix AC;

  @Before
  public void setUp() {
    A = Matrix.createRandomMatrix(N, N);
    B = Matrix.I(N, N);
    C = new Matrix(N, N);

    AA = new AtomicMatrix(A);
    AB = new AtomicMatrix(B);
    AC = new AtomicMatrix(C);
  }

  @Test
  public void SerialMatrixMultiplicationTest() {
    SerialMatrixMultiplication.multiply(A, B, C);
    assertTrue("SerialMatrixMultiplication", A.equals(C));
  }

  @Test
  public void ParallelMultiplicationTest() throws InterruptedException {
    ParallelMultiplication.multiply(A, B, C);
    assertTrue("ParallelMultiplication", A.equals(C));
  }

  @Test
  public void ThreadPooledNaiveParallelMultiplicationTest() {
    ThreadPooledNaiveParallelMultiplication.multiply(A, B, C);
    assertTrue("ThreadPooledNaiveParallelMultiplication", A.equals(C));
  }

  @Test
  public void ThreadPooledParallelMultiplicationTest() {
    ThreadPooledParallelMultiplication.multiply(A, B, C);
    assertTrue("ThreadPooledParallelMultiplication", A.equals(C));
  }

  @Test
  public void NaiveAtomicParallelMultiplicationTest() throws InterruptedException {
    NaiveAtomicParallelMultiplication.multiply(AA, AB, AC);
    assertTrue("NaiveAtomicParallelMultiplication", AA.equals(AC));
  }

  @Test
  public void ThreadPooledAtomicParallelMultiplicationTest() {
    ThreadPooledAtomicParallelMultiplication.multiply(AA, AB, AC);
    assertTrue("ThreadPooledAtomicParallelMultiplication", AA.equals(AC));
  }
}
