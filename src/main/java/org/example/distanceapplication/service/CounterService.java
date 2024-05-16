package org.example.distanceapplication.service;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterService {
  public CounterService() { }
  private static final AtomicInteger COUNT = new AtomicInteger(0);

  public static synchronized int increment() {
    return COUNT.incrementAndGet();
  }
}
