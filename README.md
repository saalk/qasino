# GraphQL Workshop

During this workshop you will learn the basics of using GraphQL in a Spring Boot application. We will use the 
[Marvel API](https://developer.marvel.com/docs) to fetch data based on some GraphQL queries.

#### API key
You will need an API Key which you can get from the [Marvel Developer portal](https://developer.marvel.com).
Once you have your public and private keys add them to the `application.properties` file in the `resources` directory.
You are now ready to make calls to the Marvel API.

To be able to call the Marvel API you will also need a MD5 Hash. You do not need to create the hash yourself.  
A Utility class `MD5Util` is provided to create the MD5 hash. For more information please refer to the 
[Authorizing and signing requests](https://developer.marvel.com/documentation/authorization) section of the Marvel 
documentation.

### Running the application
`mvn spring-boot:run`

# Assignment 1

#### 1.1 GraphQL Schema
In this first assignment you will create your GraphQL schema. You can find the schema file in the 
`src/main/resources/graphql/` folder. The schema will basically define what data can be queried. 
We have already created a domain model that corresponds to the following `Character` endpoints 
of the [Marvel API](https://developer.marvel.com/docs).
* `GET /v1/public/characters`
* `GET /v1/public/characters/{characterId}`

Finish the schema before continuing to the next part of this assignment. See the Resources section below for more info
about the GraphQL Schema.

#### 1.2 Create queries in GraphiQL
You can use the in-browser IDE, GraphiQL, to explore your GraphQL application.
Run your application and go to: http://localhost:8080/graphiql  

Create the following two queries in GraphiQL:
 * Get the name and description of the first 10 Charachters
 * Get the name and description of a single Charachter by ID

Play around with each query and see what other data you can retrieve.

#### 1.3 Variables
To make life easier use [variables](https://graphql.org/learn/queries/#variables) in your GraphQL query. 
For example, try to use a variable for the Character ID. In GraphiQL you can specify variables and easily use them in 
your queries.

# Assignment 2
In this assignment you will create a custom [Resolver](https://www.graphql-java-kickstart.com/tools/schema-definition/#resolvers-and-data-classes).  
For each Character, fetch the list of comic series that the Character appears in. Please note that the `series` 
field in our Character model is not the same as the series list that we're referring to here. The `series` field in the 
Character model is returning a brief summary of all series the Character appears in. There's however an endpoint that 
will give you a more detailed list of series for a Character i.e.,
[GET /v1/public/characters/{characterId}/series](https://developer.marvel.com/docs#!/public/getCharacterSeriesCollection_get_4).
You will need to add a field to your Schema that will be resolved to calling this endpoint.

# Assignment 3 (optional)
#### Directives
For this assignment you will try to add an uppercase directive to your application. The directive will uppercase any
String field you for which you've set the directive in your schema. 
Please refer to [directives](https://graphql.org/learn/queries/#directives) for more information on how
to implement them.

# Resources
### GraphQL
* [GraphQL](https://graphql.org/)
* [GraphQL documentation](https://graphql.org/learn/)
* [GraphQL Schema and Types](https://graphql.org/learn/schema/)
* [GraphQL Java](https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/)
* [GraphQL Java Kickstart](https://www.graphql-java-kickstart.com/)

### Marvel 
* [Developer portal](https://developer.marvel.com)
* [Interactive documentation](https://developer.marvel.com/docs)
* [Authorizing and signing requests](https://developer.marvel.com/documentation/authorization)

### Others
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)
