package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("\"Имейл должен быть указан\"");
        }

        if (isMailPresent(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null
                    && !newUser.getEmail().isBlank()
                    && !oldUser.getEmail().equals(newUser.getEmail())) {
                if (isMailPresent(newUser.getEmail())) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null && !oldUser.getUsername().equals(newUser.getUsername())) {
                oldUser.setUsername(newUser.getUsername());
            }

            if (newUser.getPassword() != null && !oldUser.getPassword().equals(newUser.getPassword())) {
                oldUser.setPassword(newUser.getPassword());
            }

            return oldUser;
        }

        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private boolean isMailPresent(String email) {
        return users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
