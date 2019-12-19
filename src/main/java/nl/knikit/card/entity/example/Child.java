package nl.knikit.card.entity.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

//@Entity
//@Table(name = "CHILD")
@Data
@AllArgsConstructor
public class Child extends Parent {

    @Column(name = "ENUM_1")
    @Enumerated(EnumType.STRING)
    private Enum1 enum1;
    @Column(name = "FREQUENCY")
    private String frequency;
    @Column(name = "EMPTY")
    private boolean empty;
    @Column(name = "MAX_SIZE")
    private int maxSize;
}

