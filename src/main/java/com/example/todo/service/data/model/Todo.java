package com.example.todo.service.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Todo app entity.
 */
@Entity
@ToString(exclude="user")
public @Data class Todo {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_fk")
    @JsonIgnore
    private User user;
    private String name;
    private String description;
    private Status status = Status.ACTIVE;
}
