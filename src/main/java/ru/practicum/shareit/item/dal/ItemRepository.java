package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item,Integer> {

    //Optional<Item> findByUserId(Integer id);

    List<Item> findByOwnerId(Integer id);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = TRUE " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "     OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Item> searchItem(String text);

    @Query(value = "SELECT COUNT(i) FROM Item AS i WHERE i.owner.id = ?1")
    int getCountOfOwnersItems(int ownerId);

}
