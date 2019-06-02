package com.example.todo.service.data.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Todo app user entity.
 */
@Entity
public @Data class User {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotEmpty(message="Email can't be empty")
    @Email(message="Email should have @email format")
    private String email;
    @NotEmpty(message="Password can't be empty")
    private String password;
    @Temporal(TemporalType.TIMESTAMP)
    private Date since;
    @OneToMany(fetch = FetchType.EAGER, cascade= CascadeType.PERSIST, mappedBy = "user")
    private List<Todo> todos = new ArrayList<>();

    /**
     * Add todo to a list.
     * @param todo some todo.
     */
    public void addTodo(Todo todo) {
        todo.setUser(this);
        todos.add(todo);
    }

    @PrePersist
    private void initSinceDate() {
        if(isNull(since))
            since = new Date();
    }
}
