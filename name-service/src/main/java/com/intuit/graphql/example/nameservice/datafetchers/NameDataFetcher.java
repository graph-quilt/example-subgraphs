package com.intuit.graphql.example.nameservice.datafetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;

@DgsComponent
public class NameDataFetcher {
  @DgsQuery
  public Person person() {
    return new Person("Tony", "Stark");
  }
}
