package com.intuit.graphql.example.addressservice.datafetchers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address {

  private String city;
  private String state;
  private String zip;

}
