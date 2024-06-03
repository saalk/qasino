Back - [Java index](0-index.md)

## Creational

Pattern Name | Description
- | -
Singleton | The singleton pattern restricts the initialization of a class to ensure that only one instance of the class can be created.
Factory | The factory pattern takes out the responsibility of instantiating a object from the class to a Factory class.
Abstract Factory | Allows us to create a Factory for factory classes.
Builder | Creating an object step by step and a method to finally get the object instance.
Prototype | Creating a new object instance from another similar instance and then modify according to our requirements.

### singleton - never needed
First of all, let's distinguish between Single Object and Singleton. The latter is one of many possible implementations of the former. And the Single Object's problems are different from Singleton's problems. Single Objects are not inherently bad and sometimes are the only way to do things. In short:
**Single Object** - I need just one instance of an object in a program
**Singleton** - create a class with a static field. Add a static method returning this field. Lazily instantiate a field on the first call. Always return the same object.
A singleton should be used when managing access to a resource which is shared by the entire application
- Logging
- Reading configuration files
- DB connection

```java
public class Singleton {
    // has static ref for itself
    private static Singleton instance;
    private Singleton() {}
    // has static method of creating instance since constructor is private
    public static Singleton instance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### Factory - somtimes needed
The factory design pattern is used when we have a superclass with multiple sub-classes and based on input, we need to return one of the sub-class. This pattern takes out the responsibility of the instantiation of a class from the client program to the factory class.

The Factory pattern is useful when you 
- need to create multiple objects that share similar functionality, 
- -but differ in the implementation details. 

Instead of creating each object manually, the Factory pattern centralizes object creation and promotes code reusability.

The Factory pattern consists of three components:

1. **Common Interface**: This is the interface that all objects created by the Factory pattern implement. In our example, this would be the Vehicle interface.
2. **Concrete Classes**: These are the classes that implement the Vehicle interface. In our example, these would be the Car and Motorcycle classes.
3. **Factory Class**: This is the class responsible for creating objects that implement the Vehicle interface. In our example, this would be the VehicleFactory class.

```java
// common interface
public interface Vehicle {
  void start();
  void stop();
}
// concrete classes
public class Car implements Vehicle {
  @Override  public void start() { System.out.println("Starting car");  }
  @Override  public void stop() { System.out.println("Stopping car");  }
}
public class Motorcycle implements Vehicle {
  @Override  public void start() { System.out.println("Starting motorcycle");  }
  @Override  public void stop() { System.out.println("Stopping motorcycle");  }
}
// the factory class
public class VehicleFactory {
  public static Vehicle createVehicle(String type) {
  if (type.equalsIgnoreCase("car")) {
    return new Car();
  } else if (type.equalsIgnoreCase("motorcycle")) {
    return new Motorcycle();
  } else {
    throw new IllegalArgumentException("Invalid vehicle type: " + type);
  }
  }
}
```
Pros:
* Centralized object creation
* Code reusability
* Flexibility
* Testing 
  - easily replace objects with mock objects during testing, 
  - which can help you isolate and test individual components of your application.

Cons:
* Complexity: The Factory pattern can add complexity to your code, especially if you have a large number of concrete classes or if the creation logic is complex. In these cases, the Factory class can become quite large and difficult to manage.
* Tight coupling: The Factory pattern can lead to tight coupling between the Factory class and the concrete classes. If you need to make changes to the concrete classes, you may also need to make changes to the Factory class, which can increase the likelihood of errors. 
* Overall, the Factory pattern is a powerful tool for managing object creation in your code. By centralizing object creation, promoting code reusability, and allowing for flexibility and testability, the Factory pattern can help you build flexible and maintainable code in Java.

**Keep it simple:** Keep the Factory class simple and focused on creating objects. Avoid adding too much business logic to the Factory class, as this can make it harder to maintain and change in the future.

### Abstract Factory Patter vs normal Factory Pattern

The main difference between a "factory method" and an "abstract factory" is that the factory method is a method, and an abstract factory is an object.


To show you the difference, here is a factory method in use:

```java
// Factory Pattern
class A {
    public void doSomething() {
        Foo f = makeFoo();
        f.whatever();   
    }

    protected Foo makeFoo() {
        return new RegularFoo();
    }
}

class B extends A {
    protected Foo makeFoo() {
        //subclass is overriding the factory method
        //to return something different
        return new SpecialFoo();
    }
}

// And here is an abstract factory in use:
class A {
 
    private Factory factory;
    public A(Factory factory) {
        this.factory = factory;
    }

    public void doSomething() {
        //The concrete class of "f" depends on the concrete class
        //of the factory passed into the constructor. If you provide a
        //different factory, you get a different Foo object.
        Foo f = factory.makeFoo();
        f.whatever();
    }
}

interface Factory {
    
    Foo makeFoo();
    Bar makeBar();
    Aycufcn makeAmbiguousYetCommonlyUsedFakeClassName();
}

//need to make concrete factories that implement the "Factory" interface here
```

The abstract factory is an object that has multiple factory methods on it. Looking at the first half of your quote:
... with the Abstract Factory pattern, a class delegates the responsibility of object instantiation to another object via composition ...
What they're saying is that there is an object A, who wants to make a Foo object. Instead of making the Foo object itself (e.g., with a factory method), it's going to get a different object (the abstract factory) to create the Foo object.

### Builder - always needed
