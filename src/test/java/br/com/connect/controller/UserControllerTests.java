package br.com.connect.controller;

import br.com.connect.database.DatabaseContainerConfiguration;
import br.com.connect.model.transport.CreateUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper mapper;

    @ClassRule
    public static MariaDBContainer<DatabaseContainerConfiguration> databaseContainer = DatabaseContainerConfiguration.getInstance();

    @BeforeAll
    public static void runBeforeAll() {
        mapper = new ObjectMapper();
        databaseContainer.start();
    }

    @Test
    @DisplayName("User Register | Success 201 | Conflict 409")
    void register() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("Ronyeri Marinho", "ronyeri@gmail.com", "(81) 99543-3493", "Marinho123");
        String userAsJson = mapper.writeValueAsString(createUserDTO);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user").contentType("application/json").content(userAsJson)).andExpect(status().isCreated());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user").contentType("application/json").content(userAsJson)).andExpect(status().isConflict());
    }
}
