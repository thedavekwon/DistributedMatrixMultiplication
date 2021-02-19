package edu.cooper.ece465;

import java.util.concurrent.ArrayBlockingQueue;
import org.javatuples.Pair;
import lombok.Getter;

public class CoordinatorQueue {
  private ArrayBlockingQueue<Integer> taskQueue;
  private ArrayBlockingQueue<Integer> workerQueue;
  public CoordinatorQueue() {
    taskQueue = new ArrayBlockingQueue<Integer>(8);
    workerQueue = new ArrayBlockingQueue<Integer>(8);
    for (int i = 1; i < 9; i++) {
      taskQueue.add(i);
    }
  }

  public void removeTask(int index) {
    taskQueue.remove(index);
  }

  public Boolean isEmpty() {
    return taskQueue.isEmpty();
  }

  public int selectTask() {
    return taskQueue.peek();
  }
}
