package tarefas02.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tarefas02.api.models.Task;
import tarefas02.api.models.User;
import tarefas02.api.repositories.TaskRepository;
import tarefas02.api.services.exceptions.ObjectNotFoundException;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User(1L, "joaosilva", "password123");
        task = new Task(10L, user, "Estudar Testes de Software com JUnit 5");
    }

    @Test
    @DisplayName("Deve retornar uma tarefa pelo ID com sucesso (Caminho Feliz)")
    void testFindById_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Estudar Testes de Software com JUnit 5", result.getDescription());
        assertEquals(user, result.getUser());
        verify(taskRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando a tarefa não for encontrada pelo ID (Caminho Triste)")
    void testFindById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.findById(99L);
        });

        assertEquals("Tarefa não encontrada! Id:99,Tipo:tarefas02.api.models.Task", exception.getMessage());
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve buscar todas as tarefas por ID de Usuário via repositório")
    void testFindAllByUserId_Success() {
        when(taskRepository.findByUser_Id(1L)).thenReturn(List.of(task));

        List<Task> result = taskService.findAllByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Estudar Testes de Software com JUnit 5", result.get(0).getDescription());
        verify(taskRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Deve buscar tarefas por ID de Usuário validando existência prévia do Usuário no UserService")
    void testFindByUserId_Success() {
        when(userService.findById(1L)).thenReturn(user);
        when(taskRepository.findByUser_Id(1L)).thenReturn(List.of(task));

        List<Task> result = taskService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService, times(1)).findById(1L);
        verify(taskRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Deve criar e salvar uma nova tarefa com sucesso (Caminho Feliz)")
    void testCreate_Success() {
        Task newTask = new Task(null, user, "Nova Tarefa Importante");
        Task savedTask = new Task(11L, user, "Nova Tarefa Importante");

        when(userService.findById(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.create(newTask);

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertEquals("Nova Tarefa Importante", result.getDescription());
        assertEquals(user, result.getUser());

        verify(userService, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    @DisplayName("Deve falhar ao criar tarefa se o Usuário não for encontrado")
    void testCreate_UserNotFound() {
        Task newTask = new Task(null, user, "Nova Tarefa Sem Usuario");
        when(userService.findById(1L)).thenThrow(new ObjectNotFoundException("Usuário não encontrado! Id: 1"));

        assertThrows(ObjectNotFoundException.class, () -> {
            taskService.create(newTask);
        });

        verify(userService, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve atualizar a descrição de uma tarefa existente com sucesso")
    void testUpdate_Success() {
        Task updateInfo = new Task(10L, null, "Descrição Atualizada");
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.update(updateInfo);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Descrição Atualizada", result.getDescription());

        verify(taskRepository, times(1)).findById(10L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Deve deletar uma tarefa pelo ID com sucesso")
    void testDelete_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.delete(10L);

        verify(taskRepository, times(1)).findById(10L);
        verify(taskRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException customizada quando ocorrer erro de integridade ao deletar tarefa")
    void testDelete_ExceptionHandling() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        doThrow(new RuntimeException("Data integrity error")).when(taskRepository).deleteById(10L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.delete(10L);
        });

        assertEquals("Não é posspivel excuir pois não há tarefas relacionadas", exception.getMessage());
        verify(taskRepository, times(1)).findById(10L);
        verify(taskRepository, times(1)).deleteById(10L);
    }
}
