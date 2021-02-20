package edu.cooper.ece465;

import java.util.concurrent.ArrayBlockingQueue;

public class CoordinatorQueue {
  private ArrayBlockingQueue<Integer> taskQueue;

  private int workerId;

  public CoordinatorQueue() {
    taskQueue = new ArrayBlockingQueue<Integer>(8);
    for (int i = 1; i < 9; i++) {
      taskQueue.add(i);
    }
  }

  public synchronized int getCurrentWorkerId() {
    return ++workerId;
  }

  public synchronized void push(int index) {
    taskQueue.add(index);
  }

  public synchronized Boolean isEmpty() {
    return taskQueue.isEmpty();
  }

  public synchronized int pop() {
    return taskQueue.poll();
  }
}
