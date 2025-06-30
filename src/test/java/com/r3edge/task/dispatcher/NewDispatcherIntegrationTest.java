package com.r3edge.task.dispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestApplication.class, properties = {
        "tasks[0].id=task-001",
        "tasks[0].type=print",
        "tasks[0].enabled=true",
        "tasks[0].meta.message=Hello from test",
        "tasks[1].id=task-002",
        "tasks[1].type=print",
        "tasks[1].enabled=false",
        "tasks[1].meta.message=This should not be printed",
        "tasks[2].id=task-003",
        "tasks[2].type=unknown-type",
        "tasks[2].meta.data=some data"
})
public class NewDispatcherIntegrationTest {

    @Autowired
    private TaskDispatcher dispatcher;

    @MockBean
    private TaskHandlerRegistry taskHandlerRegistry;

    @MockBean
    private PrintTaskHandler printTaskHandler;

    @Autowired
    private TaskConfiguration taskConfiguration;

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
    void shouldDispatchEnabledTaskFromConfigAndCallHandler() throws Exception {
        // Arrange
        Task enabledTask = taskConfiguration.getDefinitions().stream()
                .filter(t -> "task-001".equals(t.getId()))
                .findFirst()
                .orElseThrow();

        // Act
        dispatcher.dispatch(enabledTask);

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(printTaskHandler, times(1)).handle(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getId()).isEqualTo("task-001");
    }

    @Test
    void shouldNotDispatchDisabledTaskFromConfig() throws Exception {
        // Arrange
        Task disabledTask = taskConfiguration.getDefinitions().stream()
                .filter(t -> "task-002".equals(t.getId()))
                .findFirst()
                .orElseThrow();

        // Act
        dispatcher.dispatch(disabledTask);

        // Assert
        verify(printTaskHandler, never()).handle(any(Task.class));
    }

    @Test
    void shouldThrowExceptionForUnknownTaskTypeFromConfig() throws Exception {
        // Arrange
        Task unknownTask = taskConfiguration.getDefinitions().stream()
                .filter(t -> "task-003".equals(t.getId()))
                .findFirst()
                .orElseThrow();

        // Configure the mock TaskHandlerRegistry to return null for unknown-type
        when(taskHandlerRegistry.getHandler("unknown-type")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dispatcher.dispatch(unknownTask))
                .isInstanceOf(TaskExecutionException.class)
                .hasMessageContaining("No handler found for task type: unknown-type");
    }
}
