package com.r3edge.task.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@Slf4j
@SpringBootTest(classes = TestApplication.class)
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
        doCallRealMethod().when(printTaskHandler).handle(any(Task.class));
        when(printTaskHandler.getType()).thenReturn("print");
        when(taskHandlerRegistry.getHandler("print")).thenReturn(printTaskHandler);
    }

    @Test
    void shouldLoadTasksFromYaml() {
        assertNotNull(taskConfiguration.getDefinitions());
        taskConfiguration.getDefinitions().forEach(task -> {
            log.info("Task type: {}", task.getType());
        });
    }

    @Test
    void shouldDispatchEnabledTaskFromConfigAndCallHandler() throws Exception {
        Task enabledTask = taskConfiguration.getDefinitions().stream()
            .filter(t -> "task-001".equals(t.getId()))
            .findFirst()
            .orElseThrow();

        dispatcher.dispatch(enabledTask);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(printTaskHandler, times(1)).handle(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getId()).isEqualTo("task-001");
    }

    @Test
    void shouldNotDispatchDisabledTaskFromConfig() throws Exception {
        Task disabledTask = taskConfiguration.getDefinitions().stream()
            .filter(t -> "task-002".equals(t.getId()))
            .findFirst()
            .orElseThrow();

        dispatcher.dispatch(disabledTask);

        verify(printTaskHandler, never()).handle(any(Task.class));
    }

    @Test
    void shouldThrowExceptionForUnknownTaskTypeFromConfig() throws Exception {
        Task unknownTask = taskConfiguration.getDefinitions().stream()
            .filter(t -> "task-003".equals(t.getId()))
            .findFirst()
            .orElseThrow();

        when(taskHandlerRegistry.getHandler("unknown-type")).thenReturn(null);

        assertThatThrownBy(() -> dispatcher.dispatch(unknownTask))
            .isInstanceOf(TaskExecutionException.class)
            .hasMessageContaining("No handler found for task type: unknown-type");
    }
}
