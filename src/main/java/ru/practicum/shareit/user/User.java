package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.Data;

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
}