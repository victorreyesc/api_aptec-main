package tarefas02.api.controllers;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import tarefas02.api.models.Task;
import tarefas02.api.models.User;
import tarefas02.api.repositories.TaskRepository;
import tarefas02.api.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("usuario_teste");
        testUser.setPassword("$2a$10$e8N8...hash");
        testUser = userRepository.save(testUser);

        testTask = new Task();
        testTask.setUser(testUser);
        testTask.setDescription("Estudar para a prova de QA");
        testTask = taskRepository.save(testTask);
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("GET /task/{id} - Deve retornar 200 OK com os dados da tarefa")
    void testFindById_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/{id}", testTask.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(testTask.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Estudar para a prova de QA"));
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("GET /task/{id} - Deve retornar 500/404 se a tarefa não existir")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/{id}", 99999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("GET /task/user/{userId} - Deve retornar 200 OK e a lista de tarefas do usuário")
    void testFindAllByUserId_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/user/{userId}", testUser.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Estudar para a prova de QA"));
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("POST /task - Deve criar tarefa e retornar 201 Created com cabeçalho Location")
    void testCreate_Success() throws Exception {
        User userDTO = new User();
        userDTO.setId(testUser.getId());

        Task newTask = new Task();
        newTask.setUser(userDTO);
        newTask.setDescription("Criar testes de integração automatizados");

        String jsonPayload = objectMapper.writeValueAsString(newTask);

        mockMvc.perform(MockMvcRequestBuilders.post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("PUT /task/{id} - Deve atualizar tarefa e retornar 204 No Content")
    void testUpdate_Success() throws Exception {
        Task updateTask = new Task();
        updateTask.setDescription("Descrição Atualizada da Tarefa");

        String jsonPayload = objectMapper.writeValueAsString(updateTask);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(username = "usuario_teste")
    @DisplayName("DELETE /task/{id} - Deve deletar tarefa e retornar 204 No Content")
    void testDelete_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/task/{id}", testTask.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
