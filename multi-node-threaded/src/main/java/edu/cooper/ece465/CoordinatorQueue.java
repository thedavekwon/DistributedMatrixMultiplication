package edu.cooper.ece465;

import edu.cooper.ece465.commons.Matrix.MatrixIndexes;
import java.util.concurrent.ArrayBlockingQueue;
import org.javatuples.Pair;
import lombok.Getter;

public class CoordinatorQueue {
  private int N;
  @Getter private int count;
  @Getter private int i;
  private ArrayBlockingQueue<MatrixIndexes> queue;

  public CoordinatorQueue(int N_) {
    i = 0;
    count = 0;
    N = N_ / 2;
    queue = new ArrayBlockingQueue<MatrixIndexes>(8);
    
    queue.add(new MatrixIndexes(0, 0, 0, 0, 0, 0, N));
    queue.add(new MatrixIndexes(0, 0 + N, 0, 0 + N, 0, 0 + N, N));
    queue.add(new MatrixIndexes(0 + N, 0, 0, 0, 0 + N, 0, N));
    queue.add(new MatrixIndexes(0 + N, 0, 0, 0 + N, 0 + N, 0 + N, N));
    queue.add(new MatrixIndexes(0, 0 + N, 0 + N, 0, 0, 0, N));
    queue.add(new MatrixIndexes(0, 0 + N, 0 + N, 0 + N, 0, 0 + N, N));
    queue.add(new MatrixIndexes(0 + N, 0 + N, 0 + N, 0, 0 + N, 0, N));
    queue.add(new MatrixIndexes(0 + N, 0 + N, 0 + N, 0 + N, 0 + N, 0 + N, N));
  }

  public synchronized Pair<Integer, MatrixIndexes> poll() {
    i++;
    return new Pair<Integer, MatrixIndexes>(i, queue.poll());
  }

  public synchronized int getSize() {
    return queue.size();
  }

  public synchronized void incrementCount() {
    count++;
  }

  public Boolean isDone() {
    return count == 8;
  }
}
