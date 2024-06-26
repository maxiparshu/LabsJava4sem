package org.example.distanceapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class DistanceApplication {
  public static void main(final String[] args) {
    SpringApplication.run(DistanceApplication.class, args);
  }
}
