# Java frameworks
Jersey and Spring Boot are two popular frameworks in the Java ecosystem for building web applications.

## DI = Dependency injection / inversion principle
A technique where objects or functions receive other objects or services that is requires instead of creating them internally. This dminishes the inmportance of the new keyword. An injector, container introduces services to the client and the framework handles that.

## Containers
The container will create the objects, wire them together, configure them, and manage their complete life cycle from creation till destruction. The Spring container uses DI to manage the components that make up an application. Guice is another container based framework from google.

## Beans
Objects created by a container are called managed objects or beans. The spring container detects beans with annotations on the configuration classes.

## Jakarta EE specification (JAX-RS)
Jakarta RESTful web services is a specification that provides support for creating web services with REST architecture: a uniform interface using HHTP based methods and URI's (web adresses). It has annotations like @Path, @GET, @Consumes, @Produces and @?Param (Path, Query, Header). Known implementations are:
- Jersey from Oracle for json integratino
- Apache CXF, WebSphere IBM,

eg
@Path("/hello")
public class HelloResource {

@GET
public String sayHello() {
return "Hello World";
}
}

## Spring (now 6.0) framework
The Spring programming model does not embrace the Jakarta EE platform specification; rather, it integrates with carefully selected individual specifications from the traditional EE umbrella: Servlet 5/6.0, JPA 3.0/1 etc which have the Jakara namespace. In spring you use @Bean, @Service, @Repository, @Configuration, @Controller, @RequestMapping, @Autowired, @Component, @SpringBootApplication, @EnableAutoConfiguration.

eg
@RequestMapping(value = "/ex/foos", method = RequestMethod.GET)
@ResponseBody
public String getFoosBySimplePath() {
return "Get some Foos";
}

## Spring framework modules
The spring framework is an free inversion of control container for java platform from VMWare. Version 6 is based on Java 17 and jakarta. Modules are
- Spring-core with the IoC container
- Spring modules AOP, JDBC, MVC with hooks for Rest services
  Spring modules are packaged as JAR files in the central maven repository.

## Spring boot
Spring Boot is basically an extension of the Spring framework, which eliminates the boilerplate configurations required for setting up a Spring. It has a starter and embedded server and automatic config for spring functions.





