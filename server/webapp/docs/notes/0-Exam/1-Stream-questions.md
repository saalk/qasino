Back to [Index](0-index.md)

## Question and answer

### 1 - Stream sort() with and without lambda
The interface **Comparator** in java.utils is part of **Collections** framework and can be used in the **Collections.sort()**. 

instance methods:
- compareTo() is from the Comparable interface using default sort order
- compare() is from the Comparator interface using custom sort order

- Java 8 introduced several enhancements to the Comparator interface. Java 8 Comparator interface is a functional interface that contains only one abstract method.

These interfaces are present in java.util package. 
Internally the Sort method does call Compare method of the classes it is sorting. To compare two elements, it asks “Which is greater?” - the Compare method returns -1, 0, or 1 to say if it is less than, equal, or greater to the other. 
It uses this result to then determine if they should be swapped for their sort.
```java
class AgeComparator implements Comparator (or Comparable) <Student> {

    @Override
    public int compare(Student s1, Student s2) {
        return s1.age - s2.age; // only possible with int !!!       
        // return s1.age.compareTo(s2.age); // better but can give NPA
        // return s2.age.compareTo(s1.age); // descending !!
    }
    List<Student> students = getStudents();
    ...
    Collections.sort(students,new AgeComparator());
    students.forEach((s)->System.out.println(s));
}        
```         
```java
    // with anonymous Comparator class
    List<Student> students = getStudents();
    //sort by name
    Collections.sort(students, new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.name.compareTo(s2.name); // names are not int !!!
        }
    });
```
```java
    // with Comparator java8 static method comparing with Comparator static method or lambda
    List<Student> students = getStudents();
    //sort by age
    Comparator<Student> ageComparing = Comparator.comparing(Student::getAge); 
//  Comparator<Student> ageComparing = (o1, o2)->o1.getAge().compareTo(o2.getAge());

    Collections.sort(students, ageComparing); 
```

#### Question: rework the Comparator code with streams with and without lambda
Difference when sorting in streams:
- use sorted(...) for streams, not sort(...) for Collections
- Collections.sort(List<String> ..) cannot sort complex objects
- use stream().sorted() for sorting objects that @Override the toString() method
- else use Comparator as functional OR lambda expression
```java
    List<Student> students = getStudents();
    
    List<Student> sortedList = students.stream()
        .sorted((o1, o2) -> o1.getAge() - o2.getAge())
    //  .sorted(o1.getName().compareTo(o2.getName());)
        .collect(Collectors.toList());
    
    List<Student> sortedList = students.stream()
        .sorted(Comparator.comparingInt(User::getAge))
    //  .sorted(Comparator.comparing(User::getName))
    //  .sorted(Comparator.comparing(User::getName).reversed())
        .collect(Collectors.toList());
        
    sortedList.forEach(System.out::println);
```

#### Questions input

```java
public class Advanced {
    enum Gender {
        MALE, FEMALE
    }

    record Employee(String name, int age, int salary, Gender gender) {
    }

    public static void main(String[] args) {
        Employee employee1 = new Employee("John", 20, 2000, Gender.MALE);
        Employee employee2 = new Employee("Jane", 28, 2000, Gender.FEMALE);
        Employee employee3 = new Employee("Alex", 38, 2750, Gender.MALE);
        Employee employee4 = new Employee("Mary", 35, 3500, Gender.FEMALE);
        Employee employee5 = new Employee("Pedro", 40, 3100, Gender.MALE);

        List<Employee> employees = List.of(employee1, employee2, employee3, employee4, employee5);

        // ...
    }

}
```
#### Questions What is the total salary of male employees aged over 25?
By chaining filter and mapToDouble operations, we first filter male employees older than 25 and then map their salaries to double values. The sum operation then calculates the total salary of these filtered employees.
```java
double summed = employees
    .stream()
    .filter(employee -> employee.gender.equals(Gender.MALE) && employee.age > 25)
    .mapToDouble(Employee::salary)
    .sum();
assert summed == 2750 + 3100;
```
#### Questions Does a female employee under the age of 30 named ‘Jane’ exist?
We filter the employees to include only those who are female and under the age of 30 using the filter operation. Then, we check if any of these filtered employees have the name “Jane” using the anyMatch operation with a lambda expression.
```java
boolean existsFemaleEmployeeWithName = employees
    .stream()
    .filter(employee -> employee.gender.equals(Gender.FEMALE) && employee.age < 30)
    .anyMatch(employee -> employee.name.equals("Jane"));
assert existsFemaleEmployeeWithName;
```
#### Questions What is the total salary budget for all employees?
We first map each employee to their salary using the map operation. Then, we use the reduce operation to sum up all the salaries, starting with an initial value of 0.
```java
Integer totalSalaryBudget = employees
    .stream()
    .map(Employee::salary)
    .reduce(0, Integer::sum);
assert totalSalaryBudget == 2000 + 2000 + 2750 + 3500 + 3100;
```
#### Questions What are the top three highest salaries among the employees?
We first map each employee to their salary using the map operation, leave only unique ones with distinct, sort them in descending order with sorted, and then select the top three highest salaries with limit operation.
```java
List<Integer> top3HighestSalaries = employees
    .stream()
    .map(Employee::salary)
    .distinct()
    .sorted(Comparator.reverseOrder())
    .limit(3)
    .toList();
assert List.of(3500, 3100, 2750).equals(top3HighestSalaries);
```
#### Questions What is the total salary for each gender group among employees over the age of 20?
We filter the employees based on age criteria, collect them to a map grouping by gender to the total salary.
```java
Map<Gender, Integer> genderToTotalSalaryMap = employees
    .stream()
    .filter(employee -> employee.age > 20)
    .collect(Collectors.groupingBy(Employee::gender, Collectors.summingInt(Employee::salary)));
assert genderToTotalSalaryMap.get(Gender.MALE) == 2750 + 3100;
assert genderToTotalSalaryMap.get(Gender.FEMALE) == 2000 + 3500;
```
#### Questions What is the total salary of male employees aged over 25?
```java

```
#### Questions What is the total salary of male employees aged over 25?
```java

```