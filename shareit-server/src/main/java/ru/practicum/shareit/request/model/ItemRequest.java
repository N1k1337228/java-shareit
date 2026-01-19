package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "description")
    private String requestDescription;
    @Column(name = "time_of_create")
    private LocalDateTime timeOfCreate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @OneToMany(mappedBy = "itemRequest", fetch = FetchType.LAZY)
    //@JoinColumn(name = "response_id")
    private List<ItemResponse> itemResponseList;
}
