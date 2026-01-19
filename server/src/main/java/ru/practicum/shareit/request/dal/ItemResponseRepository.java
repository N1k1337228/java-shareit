package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemResponse;

public interface ItemResponseRepository extends JpaRepository<ItemResponse,Integer> {
}
