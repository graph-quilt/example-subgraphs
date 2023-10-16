package com.intuit.graphql.example.addressservice.datafetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsQuery;

@DgsComponent
public class AddressDataFetcher {

  // This is needed for recursive stitching to trigger
  // field resolution for Query.person's child fields
  @DgsQuery
  public Person person() {
    return new Person();
  }


  @DgsData(parentType = "Person", field = "address")
  public Address address() {
    return new Address("San Diego", "CA", "92129");
  }

}
