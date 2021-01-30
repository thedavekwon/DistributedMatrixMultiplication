package edu.cooper.ece465.commons;

import java.util.Random;

public class Consumer implements Runnable {
  private Drop drop;

  public Consumer(Drop drop) {
    this.drop = drop;
  }

  public void run() {
    Random random = new Random();
    for (String message = drop.take(); !message.equals("DONE"); message = drop.take()) {
      System.out.format("Consumer %s MESSAGE RECEIVED: %s%n", toString(), message);
      try {
        Thread.sleep(random.nextInt(1000));
      } catch (InterruptedException e) {
      }
    }
  }
}
