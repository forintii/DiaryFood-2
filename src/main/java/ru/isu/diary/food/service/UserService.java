package ru.isu.diary.food.service;

import ru.isu.diary.food.entity.User;

import java.util.UUID;

public interface UserService {
    void register(String email, String password);
    User getUserById(UUID id);
    User getUserByEmail(String email);
}
