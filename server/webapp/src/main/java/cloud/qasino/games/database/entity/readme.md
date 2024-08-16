JPA OneToMany : List vs Set
=
In a unidirectional @OneToMany association, Set is preferred.
* List: Allows duplicate elements in it.
* Set: All elements should be unique.


Unidirectional associations only have a relationship in one direction
=
TL;DR Unidirectional = only parent-side defines the OneToMany relationship

```java
public class Order {

    // one to many unidirectional mapping
    // default fetch type for OneToMany: LAZY
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Set<OrderItem> orderItems = new HashSet<>();
}
```

Bidirectional associations have a relationship in both directions
=
TL;DR Bidirectional = both sides define the relationship using also 'mappedBy' (@ManyToOne is managing)

```java
public class Order {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "order")
    private Set<OrderItem> orderItems = new HashSet<>();
}

public class OrderItem {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
```

Cascade
=======
Essentially cascade allows us to define which operation (persist, merge, remove)
on the parent entity should be cascaded to the related child entities.
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)


FetchType LAZY -> do not load order details in memory until get is called
==============
lazy loading is considered a bad practice in Hibernate
...
```java
@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
private Set<OrderDetail> orderDetail = new HashSet();
```
