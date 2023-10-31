package com.intuit.graphql.example.nameservice.datafetchers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person {
  private String firstName;
  private String lastName;
  private int age;
  private boolean isMarried;
  private boolean isAlive;
}
