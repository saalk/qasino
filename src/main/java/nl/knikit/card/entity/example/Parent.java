package nl.knikit.card.entity.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
//@Table(name = "PARENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parent implements Serializable {

    private static final long serialVersionUID = 1;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private long id;

    @Column(name = "enabled")
    private boolean enabled;

}

