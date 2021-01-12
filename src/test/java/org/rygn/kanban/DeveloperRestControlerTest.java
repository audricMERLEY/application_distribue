package org.rygn.kanban;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rygn.kanban.controler.DeveloperRestControler;
import org.rygn.kanban.dao.DeveloperRepository;
import org.rygn.kanban.domain.Developer;
import org.rygn.kanban.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DeveloperRestControlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperRepository developerRepository;


    @Test
    public void findAllDeveloperControlerTest()throws Exception{
        List<Developer> developers = developerRepository.findAll();
        mockMvc.perform(get("/developers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(developers.size())))
                .andExpect(jsonPath("$[0].id",Matchers.equalTo(developers.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].firstname", Matchers.equalTo(developers.get(0).getFirstname())))
                .andExpect(jsonPath("$[0].lastname", Matchers.equalTo(developers.get(0).getLastname())))
                .andExpect(jsonPath("$[0].email", Matchers.equalTo(developers.get(0).getEmail())))
                .andExpect(jsonPath("$[0].password", Matchers.equalTo(developers.get(0).getPassword())));


    }
}
