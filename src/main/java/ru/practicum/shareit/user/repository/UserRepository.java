package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query("UPDATE User e SET " +
            "e.name = COALESCE(:#{#user.name}, e.name), " +
            "e.email = COALESCE(:#{#user.email}, e.email) " +
            "WHERE e.id = :userId")
    void updateUserFields(@Param("user") User user, @Param("userId") Long userId);

}
