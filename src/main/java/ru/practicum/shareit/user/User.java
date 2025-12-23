package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "email", unique = true)
    private String email;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Item> usersItemsId = new HashSet<>();
}
