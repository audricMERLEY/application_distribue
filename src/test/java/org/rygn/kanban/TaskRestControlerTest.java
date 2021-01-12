package org.rygn.kanban;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rygn.kanban.controler.TaskRestControler;
import org.rygn.kanban.dao.TaskRepository;
import org.rygn.kanban.domain.Developer;
import org.rygn.kanban.domain.Task;
import org.rygn.kanban.domain.TaskStatus;
import org.rygn.kanban.service.DeveloperService;
import org.rygn.kanban.service.TaskService;
import org.rygn.kanban.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class TaskRestControlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRestControler taskRestControler;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DeveloperService developerService;

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetTasksInJsonFormat() throws Exception {
        this.mockMvc.perform(
                get("/tasks").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(this.taskRepository.findAll().get(0).getId().intValue())));
    }

    @Test
    public void testAddTask() throws Exception {
        // Get the first developer
        Developer developer = this.developerService.findAllDevelopers().get(0);
        Task task = new Task();
        task.setTitle("Task test");
        task.setNbHoursForecast(1);
        task.setNbHoursReal(5);
        task.setType(this.taskService.findTaskType(Constants.TASK_TYPE_FEATURE_ID));
        task.addDeveloper(developer);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/task")
                .content(asJsonString(task))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andDo(print());

        Collection<Task> taskList = this.taskService.findAllTasks();
        Assert.assertNotNull(taskList);
        Boolean done = false;
        for(Task t : taskList){
            if(t.getTitle().equals("Task test")){
                done = true;
                break;
            }
        }
        Assert.assertTrue("Task wasn't added",done);
    }

    @Test
    public void moveTask() throws Exception {
        Task task = this.taskService.findAllTasks().iterator().next();

        // Move right
        TaskStatus taskStatus = new TaskStatus(task.getStatus().getId()+1,"Test");

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/task/"+task.getId())
                .content(asJsonString(taskStatus))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andDo(print());

        task = this.taskService.findTask(task.getId());
        Assert.assertNotNull(task);
        Assert.assertEquals(taskStatus.getId(),task.getStatus().getId());

        // Move left
        taskStatus = new TaskStatus(task.getStatus().getId()-1,"Test");

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/task/"+task.getId().toString())
                .content(asJsonString(taskStatus))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andDo(print());

        task = this.taskService.findTask(task.getId());
        Assert.assertNotNull(task);
        Assert.assertEquals(taskStatus.getId(),task.getStatus().getId());

        // move right too much
        task = this.taskService.findTask(task.getId());
        taskStatus = new TaskStatus(task.getStatus().getId()+3,"Test");

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/task/"+task.getId().toString())
                .content(asJsonString(taskStatus))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andDo(print());

        Task actualTask = this.taskService.findTask(task.getId());
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(task.getStatus().getId(),actualTask.getStatus().getId());


        // id not found
        task = this.taskService.findTask(task.getId());
        taskStatus = new TaskStatus(task.getStatus().getId()+1,"Test");
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/task/"+String.valueOf(task.getId() + 5))
                .content(asJsonString(taskStatus))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andDo(print());
    }
}
