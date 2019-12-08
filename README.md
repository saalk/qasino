# cards
- using GraphQL in a Spring Boot application with the [Marvel API] (https://developer.marvel.com/docs) to fetch data 

### Running the application
- 'mvn spring-boot:run`
- go to: http://localhost:8080/graphiql  

### To investigate
To make life easier use [variables] (https://graphql.org/learn/queries/#variables) in your GraphQL query. 
Create a custom [Resolver] (https://www.graphql-java-kickstart.com/tools/schema-definition/#resolvers-and-data-classes).   
Add an uppercase directive to your application. The directive will uppercase any
String field you for which you've set the directive in your schema. 
Please refer to [directives] (https://graphql.org/learn/queries/#directives) for more information on how
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
