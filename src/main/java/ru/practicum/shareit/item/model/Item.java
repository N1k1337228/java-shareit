package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner")
    private User owner;
    @Column(name = "available")
    private Boolean available;
    @OneToMany(mappedBy = "item",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comment;
}
