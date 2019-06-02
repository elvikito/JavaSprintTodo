package com.example.todo.service;

import com.example.todo.service.data.model.Status;
import com.example.todo.service.data.model.Todo;
import com.example.todo.service.data.model.User;
import com.example.todo.service.data.repository.TodoRepository;
import com.example.todo.service.data.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.util.Arrays.asList;

/**
 * Loads default data for application.
 */
@Service
@Log
public class ApplicationInitializer {
	@Autowired
	UserRepository userRepository;

	@Autowired
	TodoRepository todoRepository;

	User demoUser;
	Todo todo1, todo2;

	@PostConstruct
	public void onInit() {
		if (userRepository.count() != 0) {
			log.info("Data is present. No data loading is performed.");

			return;
		}

		createDefaultUser();
		createDefaultTodo();

		log.info("Default data was loaded, user: demo@user.com 123");
	}

	private void createDefaultTodo() {
		todo1 = new Todo();
		todo1.setName("The first todo!");
		todo1.setDescription("Cool description.");
		todo1.setStatus(Status.COMPLETE);

		todo2 = new Todo();
		todo2.setName("Uncompleted todo!");
		todo2.setDescription("Todo # 2");

		demoUser.addTodo(todo1);
		demoUser.addTodo(todo2);

		todoRepository.save(asList(todo1, todo2));
	}

	private void createDefaultUser() {
		demoUser = new User();
		demoUser.setEmail("demo@user.com");
		demoUser.setPassword("123");

		userRepository.save(demoUser);
	}
}
