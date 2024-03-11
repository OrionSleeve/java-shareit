package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Item e SET " +
            "e.name = COALESCE(:#{#item.name}, e.name), " +
            "e.description = COALESCE(:#{#item.description}, e.description), " +
            "e.available = COALESCE(:#{#item.available}, e.available) " +
            "WHERE e.id = :itemId AND e.owner.id = :ownerId")
    void updateItemFields(@Param("item") Item item, @Param("ownerId") Long ownerId, @Param("itemId") Long itemId);

    @Query("SELECT it FROM Item it " +
            "WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(it.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND it.available = true ")
    List<Item> searchItemByNameOrDescription(@Param("text") String text);

    List<Item> findAllByOwnerId(@Param("ownerId") long ownerId);
}
