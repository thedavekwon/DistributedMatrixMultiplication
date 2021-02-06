package edu.cooper.ece465.threads;

import edu.cooper.ece465.commons.Matrix;
import edu.cooper.ece465.commons.MatrixMultiplication;
import edu.cooper.ece465.commons.SerialMatrixMultiplication;
import lombok.AllArgsConstructor;

public class NonsquareParallelMultiplication extends MatrixMultiplication {
    public NonsquareParallelMultiplication() {
        super(NonsquareParallelMultiplication.class.toString());
    }

    public void multiply(Matrix A, Matrix B, Matrix C) {
        Thread t = new Thread(new ParallelMultiply(A, B, C, 0, 0, 0, 0, 0, 0, A.getRow(), B.getCol(), A.getCol()));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    private class ParallelMultiply implements Runnable {
        private Matrix A;
        private Matrix B;
        private Matrix C;
        // A_size == n
        // B_size == p
        // common == m 
        private int A_i, A_j, B_i, B_j, C_i, C_j, A_size, B_size, common;

        public void run() {
            int dim_check = Math.max(A_size, Math.max(B_size, common));
            if (dim_check <= A.getRow() / 4) {
                SerialMatrixMultiplication.multiplyWithIndex(A, B, C, A_i, A_j, B_i, B_j, C_i, C_j, A_size, B_size, common);
            } else {
                if (A_size == Math.max(A_size, Math.max(B_size, common))) {
                    int split = A_size / 2;
                    Matrix C1 = new Matrix(C.getRow(), C.getCol());
                    Thread[] threads = new Thread[] {
                            new Thread(new ParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, split, B_size, common)),
                            new Thread(new ParallelMultiply(A, B, C1, A_i + split, A_j, B_i, B_j, C_i,
                                    C_j + split, split, B_size, common))};
                    for (Thread thread : threads)
                        thread.start();
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            LOG.error(e);
                            e.printStackTrace();
                        }
                    }
                    // C.incrementFromMatrices(C1, C2);

                } else if (B_size == Math.max(A_size, Math.max(B_size, common))) {
                    int split = B_size / 2;
                    Matrix C1 = new Matrix(C.getRow(), C.getCol());
                    Thread[] threads = new Thread[] {
                            new Thread(new ParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, A_size, split, common)),
                            new Thread(new ParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j + split, C_i,
                                    C_j + split, split, B_size, common))};
                    for (Thread thread : threads)
                        thread.start();
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            LOG.error(e);
                            e.printStackTrace();
                        }
                    }
                    // C.incrementFromMatrices(C1, C2);

                } else if (common == Math.max(A_size, Math.max(B_size, common))) {
                    int split_horiz = A_size / 2;
                    int split_vert = B_size / 2;
                    Matrix C1 = new Matrix(C.getRow(), C.getCol());
                    Matrix C2 = new Matrix(C.getRow(), C.getCol());
                    Thread[] threads = new Thread[] {
                            new Thread(new ParallelMultiply(A, B, C1, A_i, A_j, B_i, B_j, C_i, C_j, split_horiz, split_vert, common)),
                            new Thread(new ParallelMultiply(A, B, C2, A_i + split_horiz, A_j, B_i, B_j, C_i,
                                    C_j + split_horiz, split_horiz, split_vert, common))};
                    for (Thread thread : threads)
                        thread.start();
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            LOG.error(e);
                            e.printStackTrace();
                        }
                    }
                     C.incrementFromMatrices(C1, C2);

                }
            }
        }
    }
}
