package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
