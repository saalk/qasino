# Java

## Java History
Java was created at Sun Microsystems in 1995. James Gosling and other researchers create a new language to allow communication between consumer electronic devices. In 2010 Oracle bought Sun. Java is free. Oracle decided to give away the rights for Java EE to the Eclipse Foundation (the language is still owned by Oracle). The Eclipse Foundation legally had to rename Java EE. That’s because Oracle has the rights over the “Java” brand.
So to choose the new name, the community voted and picked: Jakarta EE.

## Java Compiler and runtime
Java differs from other languages because its compiler turns it into bytecode which is then interpreted by a jre that was available for many platforms. So it does not need to be recompiled. Other languages are.
- Kotlin is an improved Java having rull safety and is fully interoperable with Java.
- Scala is also Java based and has functional programming merged in its syntax.
- Python is an interpreted language with exptensive libraries that executes code line by line, its more easier and concise but slower. Its best for data sience and web development but Java is better for performance critical applications and enterprise development.

## Java language
Java is a class based and object oriented language. It is a general purpose language (eg compare HTML that is only for web pages) and has a c/c++ style syntax.
Object is an instance or an element of a java class. In general there is one class per java file. The compiler actually creates .class files with bytecode. Object class is present in java.lang package. Every class in Java is directly or indirectly derived from the Object class. Objects in java have three phases: creation (with new keyword) and initialization (with constructor you assign values to it), use, and destruction. Declaration is the process of defining the variable, along with its type and name. There must be one class with a main method (public static) which is then the starting point of the application.

## Java portability
The main concept of Java was portability, called "write once, run everywhere". This was helpfull for internet where many different computer retrieved the same web page. Furthermore the automatic garbage collector handles memory management. If methods for non-existing objects are called a null-pointer exception is thrown.

## Java Versions
End of 1990 Sun was creating different versions of java
- Standard Edition SE for computers
- Micro Edition ME for devices,
- Jakarta Enterpise Edition EE for internet servers.

## Java language parts
- specification
- compatible implementation
- TCK

## Java vs Javascript
Javascript is not Java and is designed in 1995 by Netscape to run in webbrowers.



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





