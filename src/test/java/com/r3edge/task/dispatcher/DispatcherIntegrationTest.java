package com.r3edge.task.dispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestApplication.class)
public class DispatcherIntegrationTest {

    @Autowired
    private TaskDispatcher dispatcher;

    @MockBean
    private TaskHandlerRegistry taskHandlerRegistry;

    @MockBean
    private PrintTaskHandler printTaskHandler;

    @BeforeEach
    void setUp() {
        // Configure the mock PrintTaskHandler to call the real method when handle is invoked
        doCallRealMethod().when(printTaskHandler).handle(any(Task.class));
        // Also ensure getType returns the correct value
        when(printTaskHandler.getType()).thenReturn("print");

        // Configure the mock TaskHandlerRegistry to return our mocked PrintTaskHandler
        when(taskHandlerRegistry.getHandler("print")).thenReturn(printTaskHandler);
    }

    @Test
    void shouldDispatchEnabledTaskAndCallHandler() throws Exception {
        // Arrange
        List<Task> tasks = loadTasksFromTestYaml();
        Task enabledTask = tasks.stream().filter(t -> "task-001".equals(t.getId())).findFirst().orElseThrow();

        // Act
        dispatcher.dispatch(enabledTask);

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(printTaskHandler, times(1)).handle(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getId()).isEqualTo("task-001");
    }

    @Test
    void shouldNotDispatchDisabledTask() throws Exception {
        // Arrange
        List<Task> tasks = loadTasksFromTestYaml();
        Task disabledTask = tasks.stream().filter(t -> "task-002".equals(t.getId())).findFirst().orElseThrow();

        // Act
        dispatcher.dispatch(disabledTask);

        // Assert
        verify(printTaskHandler, never()).handle(any(Task.class));
    }

    @Test
    void shouldThrowExceptionForUnknownTaskType() throws Exception {
        // Arrange
        List<Task> tasks = loadTasksFromTestYaml();
        Task unknownTask = tasks.stream().filter(t -> "task-003".equals(t.getId())).findFirst().orElseThrow();

        // Configure the mock TaskHandlerRegistry to return null for unknown-type
        when(taskHandlerRegistry.getHandler("unknown-type")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dispatcher.dispatch(unknownTask))
                .isInstanceOf(TaskExecutionException.class)
                .hasMessageContaining("No handler found for task type: unknown-type");
    }

    private List<Task> loadTasksFromTestYaml() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/tasks-test.yml")) {
            return YamlTaskLoader.loadTasks(input);
        }
    }
}
