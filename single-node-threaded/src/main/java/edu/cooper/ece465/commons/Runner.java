package edu.cooper.ece465.commons;

import org.apache.log4j.Logger;

public class Runner extends Thread {
  private static final Logger LOG = Logger.getLogger(Runner.class);
  protected String toStringVal;

  public Runner() {
    toStringVal = "Runner Class: " + super.toString();
  }

  @Override
  public void run() {
    LOG.debug("Runner.run() starts.");

    for (int i = 0; i < 100; i++) {
      System.out.print(" " + i + " ");
    }

    LOG.debug("Runner.run() ends.");
  }

  @Override
  public String toString() {
    return toStringVal;
  }
}
