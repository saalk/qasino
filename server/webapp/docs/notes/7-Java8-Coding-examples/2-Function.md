Back to [Index](0-index.md)
# Function
## Java 8 - Function
Java 8 introduced the Function interface (from the java.util.function package), which represents a function that takes one argument and produces a result. The Function interface has a method andThen that facilitates function composition.
```java
 Function<Integer, Integer> addTwo = x -> x + 2;
 ```

### Example

```java
import java.util.function.Function;
public class FunctionCompositionExample {
    public static void main(String[] args) {
        // Create functions for adding 2 and multiplying by 3
        Function<Integer, Integer> addTwo = x -> x + 2;
        Function<Integer, Integer> multiplyByThree = x -> x * 3;
        // Function composition using "andThen"
        Function<Integer, Integer> addAndMultiply = addTwo.andThen(multiplyByThree);
        // Apply the composed function to a value
        int result = addAndMultiply.apply(5);
        System.out.println("Result: " + result); // Output: Result: 21
    }
}
```
Function composition is an elegant way to combine simple functions into more complex ones, making your code more modular, readable, and maintainable. It allows you to build sophisticated data transformation pipelines in a clean and declarative manner. 

### Currying
Currying is a functional programming technique where a function that takes multiple arguments is transformed into a sequence of functions, each taking a single argument. In other words, instead of providing all the arguments at once, currying allows you to apply the function partially, one argument at a time, producing a new function with each application until all arguments are provided, and the final result is obtained.
```java
import java.util.function.Function;

public class CurryingExample {
public static void main(String[] args) {
// Define a function that takes two arguments and returns their sum
Function<Integer, Function<Integer, Integer>> add = x -> y -> x + y;
// Apply currying to the "add" function
Function<Integer, Integer> add5 = add.apply(5);
// Apply the partially applied function
int result = add5.apply(3);
System.out.println("Result: " + result); // Output: Result: 8
}
}
```