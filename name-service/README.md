# README.md

The instructions in this document is for running the service in local machines only.

## Running the service

To run the service:
```
$ mvn spring-boot:run -Dspring.profiles.active=local
```

On success, you should see this log.
```
2023-10-16 13:28:16.531  INFO 74213 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9091 (http) with context path ''
2023-10-16 13:28:16.536  INFO 74213 --- [           main] c.i.g.e.n.NameServiceApplication         : Started NameServiceApplication in 0.9 seconds (JVM running for 1.068)

```

## Test Query using GraphiQL

Open a browser and go to http://localhost:9091/graphiql.  Try this query:

```
query {
  person {
    firstName
    lastName
  }
}
```

## Registering to a local instance of graphql-gateway-java

Once the service is running locally, it can be registered to graphql-gateway-java. 

Start graphql-gateway-java if not yet started.  See instruction [here](https://github.com/graph-quilt/graphql-gateway-java)

```
$ cd src/main/resources/schema/
$ aws --endpoint-url=http://localhost:4572 s3 cp . s3://topics/graphql-gateway-java/dev/registrations/1.0.0/name.service --recursive
```

The command above should upload the service schema and config.json to localstack.  On successful upload, you
should see log like below:
```
upload: main/config.json to s3://topics/graphql-gateway-java/dev/registrations/1.0.0/name.service/main/config.json
upload: main/schema.graphqls to s3://topics/graphql-gateway-java/dev/registrations/1.0.0/name.service/main/schema.graphqls
```

Try the same query above on graphql-gateway-java's graphiql: http://localhost:7000/graphiql.

Try also a combined query for name-service and address-service, provided both are running and registered
to grahql-gateway-java:
```
query {
  person {
    firstName
    lastName
    address {
      city
      state
      zip
    }
  }
}
```

## Running with graphql-authorization-java
To run the service with graphql-authorization-java, "authz.instrumentation.enabled" in the application properties should be set to true.  

This will enable the authorization instrumentation as a configuration bean in the service.

Scopes are determined from the header attribute "x-userid" as shown below:

```
{
  "x-userId": "userId3"
}