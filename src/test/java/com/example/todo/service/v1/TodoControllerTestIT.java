package com.example.todo.service.v1;


import com.example.todo.service.TodoRestServiceApplication;
import com.example.todo.service.data.model.Status;
import com.example.todo.service.data.model.User;
import com.example.todo.service.data.repository.TodoRepository;
import com.example.todo.service.data.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TodoRestServiceApplication.class)
@WebAppConfiguration
public class TodoControllerTestIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetTodoListByUser() throws Exception {
        mockMvc.perform(get("/v1/todo/user/1")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTodoListByBadUserId() throws Exception {
        mockMvc.perform(get("/v1/todo/user/123")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTodoListByNullUserId() throws Exception {
        mockMvc.perform(get("/v1/todo/user/")).andDo(print())
        .andExpect(status().isNotFound());
    }

    @Test
    public void testTodoShouldBeComplete() throws Exception {
        User user = userRepository.findOne(new Long(1));

        mockMvc.perform(get("/v1/todo/complete/1"))
                .andExpect(status().isOk());

        assertEquals(todoRepository.findByUserAndId(user, new Long(1)).get().getStatus(), Status.COMPLETE);
    }

    @Test
    public void testShouldCreateNewTodo() throws Exception {
        // crate new todo and save location from header
        MvcResult result = mockMvc.perform(post("/v1/todo/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"new todo\", \"description\":\"test description\", \"status\":\"ACTIVE\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", anything()))
                .andExpect(header().string("Location", containsString("/v1/todo/")))
                .andReturn();

        String newTodoLocation = result.getResponse().getHeader("Location");
        mockMvc.perform(get(newTodoLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new todo"));
    }

    @Test
    public void testCurrentTodoShouldBeUpdate() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/v1/todo/1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"new todo\", \"description\":\"update\", \"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", anything()))
                .andExpect(header().string("Location", containsString("/v1/todo/")))
                .andReturn();
                mockMvc.perform(get(mvcResult.getResponse().getHeader("Location")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("update"));
    }

    @Test
    public void testAttemptUpdateTodoWithUnknownUser() throws Exception {
        mockMvc.perform(put("/v1/todo/1/user/100")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\": \"new todo\", \"description\":\"update\", \"status\":\"ACTIVE\"}"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testAttemptSetCompleteNullTodo() throws Exception {
        mockMvc.perform(get("/v1/todo/complete/100"))
                .andExpect(status().isNotFound());
    }
}
