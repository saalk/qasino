Back - [Java index](0-index.md)

## Structural Design Patterns
There are 7 structural design patterns defined in the Gangs of Four design patterns book.

Pattern Name | Description
- | - 
Adapter | Provides an interface between two unrelated entities so that they can work together.
Composite | Used when we have to implement a part-whole hierarchy. For example, a diagram made of other pieces such as circle, square, triangle, etc.
Proxy | Provide a surrogate or placeholder for another object to control access to it.
Flyweight | Caching and reusing object instances, used with immutable objects. For example, string pool.
Facade | Creating a wrapper interfaces on top of existing interfaces to help client applications.
Bridge | The bridge design pattern is used to decouple the interfaces from implementation and hiding the implementation details from the client program.
Decorator | The decorator design pattern is used to modify the functionality of an object at runtime.

```java
CloseableHttpClient httpClient = HttpClientBuilder.create().build();
// send basic get
httpClient.execute(new HttpGet("http://www.google.com"),
    response -> {
        //handle response
        assertThat(response.getCode()).isEqualTo(200);
        String bodyAsString = EntityUtils.toString(response.getEntity());
        assertThat(bodyAsString, notNullValue());
        return response;
    }
);
```
## Decorator Pattern
Modify the functionality of an object at runtime
// Venkat DEVOXX 2204 - camera is a decorator pattern - different lenses, filter etc
// you can concat functions -> data clensers, mask, uncompress

```java
import java.awt.Color;
import java.util.function.Function;

public class Camera {
    private Function <Color, Color> filter; 
    public Color snap(Color input);
    
    public Camera(Function<Color, Color>... filters) {
        filter.Stream.of(filters)
                .reduce(input -> input, (result, aFilter) -> result.andThen(aFilter));
        filter.Stream.of(filterS)
                .reduce()
    }
}
    public static void process(Camera camera) {
        print snap;
}

// process(new Camera(Color::brighter, Color::darker)); // Color -> color:brighter()

```

## Behavioral Design Patterns

Pattern Name | Description
- | - 
Template| Method	used to create a template method stub and defer some of the steps of implementation to the subclasses.
Mediator | used to provide a centralized communication medium between different objects in a system.
Chain of Responsibility | used to achieve loose coupling in software design where a request from the client is passed to a chain of objects to process them.
Observer | useful when you are interested in the state of an object and want to get notified whenever there is any change.
Strategy | Strategy pattern is used when we have multiple algorithm for a specific task and client decides the actual implementation to be used at runtime.
Command | Command Pattern is used to implement lose coupling in a request-response model.
State | State design pattern is used when an Object change it’s behavior based on it’s internal state.
Visitor | Visitor pattern is used when we have to perform an operation on a group of similar kind of Objects.
Interpreter | defines a grammatical representation for a language and provides an interpreter to deal with this grammar.
Iterator | used to provide a standard way to traverse through a group of Objects.
Memento | The memento design pattern is used when we want to save the state of an object so that we can restore later on.