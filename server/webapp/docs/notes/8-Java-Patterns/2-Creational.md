Back - [Java index](0-index.md)

## Creational

Pattern Name | Description
- | -
Singleton | The singleton pattern restricts the initialization of a class to ensure that only one instance of the class can be created.
Factory | The factory pattern takes out the responsibility of instantiating a object from the class to a Factory class.
Abstract Factory | Allows us to create a Factory for factory classes.
Builder | Creating an object step by step and a method to finally get the object instance.
Prototype | Creating a new object instance from another similar instance and then modify according to our requirements.

### Singleton - never needed
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

### Prototype
The Prototype pattern is generally used when we have an instance of the class (prototype) and we’d like to create new objects by just copying the prototype.
When we’re trying to clone, we should decide between making a **shallow** or a **deep copy.**

**Shallow Copy**: A shallow copy of an object copies the values of the object's fields to a new object. If the field is a primitive type, a direct value copy is performed. However, if the field is a reference to another object, only the reference address is copied. Therefore, both the original and the cloned object will refer to the same actual object. Shallow copying is often not suitable when the cloned objects need to be completely independent.

**Deep Copy**: A deep copy involves copying not only the object itself but also the objects referenced by the object. This ensures that the cloned object and the original object do not share references to the same objects for their fields. Deep copying is used when the objects' independence from each other is required.

Say you have an object, and you want to create an exact copy of it. 
How would you do it? 
1. First, you have to create a new object of the same class. 
2. Then you have to go through all the fields of the original object and copy their values over to the new object.
3. But there’s a catch. Not all objects can be copied that way because some of the object’s fields may be private and not visible from outside of the object itself.

The Prototype pattern delegates the cloning process to the actual objects that are being cloned. 
The pattern declares a common interface. The implementation of the clone method is very similar in all classes. 

1. The method creates an object of the current class and carries over all of the field values of the old object into the new one. 
2. You can even copy private fields because most programming languages let objects access private fields of other objects that belong to the same class.
3. Create the prototype interface and declare the clone method in it. 
4. Or just add the method to all classes of an existing class hierarchy, if you have one.
5. A prototype class must define the alternative constructor that accepts an object of that class as an argument. 
6. The constructor must copy the values of all fields defined in the class from the passed object into the newly created instance. 
7. If you’re changing a subclass, you must call the parent constructor to let the superclass handle the cloning of its private fields.

Note, that every class must explicitly override the cloning method and use its own class name along with the new operator. 
Otherwise, the cloning method may produce an object of a parent class.

Optionally, create a centralized prototype registry to store a catalog of frequently used prototypes.

```java
// implementing a deep copy via the clone() method
public class Animal implements Cloneable {
  private String species;
  private int age;

  public Animal(String species, int age) {
    this.species = species;
    this.age = age;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}

public class Person implements Cloneable {
  private String name;
  private int age;
  private List hobbies;

  public Person(String name, int age, List hobbies) {
    this.name = name;
    this.age = age;
    this.hobbies = new ArrayList<>(hobbies); 
    // Ensures a copy of the list, not just the reference !!!!
  }

  @Override
  public Person clone() throws CloneNotSupportedException {
    Person cloned = (Person) super.clone();
    cloned.hobbies = new ArrayList<>(this.hobbies); // Deep copying of mutable fields.
    return cloned;
  }
}
```

The Cloneable interface in Java is a marker interface (an interface with no methods) that indicates that a class allows cloning of its instances. If a class does not implement Cloneable, calling the clone() method on its instance will result in a CloneNotSupportedException.

1. Efficient Object Creation
   can be much more efficient than creating new ones from scratch

2. Avoiding Repeated Initialization
   Using the Prototype Pattern, objects can be created ready-to-use with an already established default state, copied from an existing object.

3. Reduced Subclassing
   Prototype can reduce the need for creating specific factory classes that are often necessary for factory method patterns. By cloning objects, code can be made independent of the specific types of products that need to be created, thus minimizing the number of required subclasses.

4. Adding and Removing Objects at Runtime
   Because the prototyping involves actual objects, not classes, new object types can be added and removed during runtime, allowing for more dynamic systems that can adapt to changing environments or requirements without needing code changes.

5. Hide Complexity of Creating Objects

6. Flexibility in Object Composition
   Objects can be composed of parts that are themselves prototypes

7. Prototype Pattern and Multithreading
   When dealing with a multithreaded application environment, the Prototype Pattern can help in maintaining each thread's unique instance of object creation. It simplifies the management of concurrent object configurations and minimizes thread interference during object creation.

Cons
Cloning does not call constructors, which can lead to incomplete object initialization.
The Cloneable interface is a marker interface and doesn't enforce the implementation of clone(), potentially leading to runtime errors.

Key Differences:
Factory Method leverages inheritance and relies on a subclass to handle the object instantiation. Prototype leverages composition and an existing object for creating new instances.
Use Factory Method when you need flexibility about which components are instantiated and how they are configured, but when you do not necessarily need to start with a prototype object.
