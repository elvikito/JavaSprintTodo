package com.example.todo.service.v1;

import com.example.todo.service.data.model.Status;
import com.example.todo.service.data.model.Todo;
import com.example.todo.service.data.model.User;
import com.example.todo.service.data.repository.TodoRepository;
import com.example.todo.service.data.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/v1/")
@Log
public class TodoController {
    @Autowired
    TodoRepository todoRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Get Todo
     * @param tId by id
     * @return todo or not found
     */
    @RequestMapping(value="/todo/{tId}/user/{uId}", method= RequestMethod.GET)
    public ResponseEntity<?> getTodo(@PathVariable Long tId, @PathVariable Long uId) {
        User user = userRepository.findOne(uId);
        Todo todo = todoRepository.findByUserAndId(user, tId).get();

        if(isNull(todo))
            return new ResponseEntity<>("Todo not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    /**
     * Get todos for user
     * @param uId user id
     * @return todo list or not found
     */
    @RequestMapping(value="/todo/user/{uId}", method= RequestMethod.GET)
    public ResponseEntity getTodos(@PathVariable Long uId) {
        User user = userRepository.findOne(uId);
        if(isNull(user))
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(todoRepository.findByUser(user), HttpStatus.OK);
    }

    /**
     * Mark Todo as complete
     * @param tId todo id
     * @return Ok or not found
     */
    @RequestMapping(value="/todo/complete/{tId}", method= RequestMethod.GET)
    public ResponseEntity<?> completeTodo(@PathVariable Long tId) {
        Todo todo = todoRepository.findOne(tId);

        if(isNull(todo))
            return new ResponseEntity<>("Todo not found", HttpStatus.NOT_FOUND);
        else {
            todo.setStatus(Status.COMPLETE);
            todoRepository.save(todo);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(value="/todo/user/{uId}", method= RequestMethod.POST)
    public ResponseEntity<?> createTodo(@PathVariable Long uId, @Valid @RequestBody Todo newTodo) {
        User user = userRepository.findOne(uId);

        if(isNull(user))
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        else {
            if(newTodo.getName().isEmpty()) {
                newTodo.setName("New Todo");
            }
                newTodo.setUser(user);
                newTodo.setId(null);

                todoRepository.save(newTodo);

                //	Set	the	location header	for	the	newly created resource
                HttpHeaders responseHeaders	= new HttpHeaders();
                URI newTodoUri = ServletUriComponentsBuilder.fromCurrentServletMapping().
                        path("v1/todo/"+newTodo.getId()+"/user/"+uId).buildAndExpand(newTodo.getId()).toUri();
                responseHeaders.setLocation(newTodoUri);

                return new ResponseEntity<>("Todo created", responseHeaders, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value="/todo/{tId}/user/{uId}", method= RequestMethod.PUT)
    public ResponseEntity<?> updateTodo(@PathVariable Long tId, @PathVariable Long uId, @Valid @RequestBody Todo updTodo) {//
        User user = userRepository.findOne(uId);

        if(Objects.isNull(user))
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        Optional<Todo> todo = todoRepository.findByUserAndId(user, tId);

        if(!todo.isPresent())
            return new ResponseEntity<>("Todo not found", HttpStatus.NOT_FOUND);
        else {
            todo.get().setName(updTodo.getName());
            todo.get().setDescription(updTodo.getDescription());
            todo.get().setStatus(updTodo.getStatus());

            todoRepository.save(todo.get());

            HttpHeaders responseHeaders	= new HttpHeaders();
            URI newTodoUri = ServletUriComponentsBuilder.fromCurrentServletMapping().
                    path("v1/todo/"+todo.get().getId()+"/user/"+uId).buildAndExpand(todo.get().getId()).toUri();
            responseHeaders.setLocation(newTodoUri);

            return new ResponseEntity<>("Todo updated", responseHeaders, HttpStatus.OK);
        }
    }

}
