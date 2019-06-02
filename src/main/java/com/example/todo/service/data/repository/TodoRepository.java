package com.example.todo.service.data.repository;

import com.example.todo.service.data.model.Todo;
import com.example.todo.service.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Todo Spring Data repository.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
    /**
     * Find all todo for user.
     * @param user query by.
     * @return todo list, if any.
     */
    List<Todo> findByUser(User user);

    /**
     * Find todo by user and todo's id.
     * @param user user's todo to find.
     * @param id todo id
     * @return todo if any
     */
    Optional<Todo> findByUserAndId(User user, Long id);
}
