package com.r3edge.tests.tasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.r3edge.tasks.dispatcher.core.ITaskExecutor;
import com.r3edge.tasks.dispatcher.core.ITaskScheduler;
import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskConfiguration;
import com.r3edge.tasks.dispatcher.core.TaskDispatcher;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;
import com.r3edge.tasks.dispatcher.core.TaskStrategyRouter;

public class TaskRefreshLifecycleTest {

    private TaskConfiguration taskConfiguration;
    private TaskHandlerRegistry registry;
    private TaskStrategyRouter strategyRouter;
    private TaskDispatcher dispatcher;

    @BeforeEach
    void setup() {
        taskConfiguration = mock(TaskConfiguration.class);
        registry = mock(TaskHandlerRegistry.class);
        strategyRouter = mock(TaskStrategyRouter.class);
        dispatcher = new TaskDispatcher(registry, taskConfiguration, strategyRouter);
    }

    @Test
    void refreshTasks_shouldCleanupAndRedispatch() {
        // GIVEN: deux tâches fictives
        Task task1 = Task.builder().id("task-001").type("cleanup").strategy("default").enabled(true).build();
        Task task2 = Task.builder().id("task-002").type("cleanup").strategy("jobrunr").enabled(true).build();

        when(taskConfiguration.getDefinitions()).thenReturn(List.of(task1, task2));

        // 💡 Utilise ton handler réel ici
        when(registry.getHandler(anyString())).thenReturn(Optional.of(new CleanUpHandler()));

        // Mocks des executors/schedulers
        ITaskExecutor mockExecutor = mock(ITaskExecutor.class);
        when(mockExecutor.getExecutedTaskIds()).thenReturn(Set.of("task-999"));

        ITaskScheduler mockScheduler = mock(ITaskScheduler.class);
        when(mockScheduler.getScheduledTaskIds()).thenReturn(Set.of("task-999"));

        when(strategyRouter.resolveExecutor(any())).thenReturn(mockExecutor);
        when(strategyRouter.resolveScheduler(any())).thenReturn(mockScheduler);
        when(strategyRouter.allExecutors()).thenReturn(List.of(mockExecutor));
        when(strategyRouter.allSchedulers()).thenReturn(List.of(mockScheduler));

        // WHEN: on déclenche un refresh
        dispatcher.refreshTasks();

        // THEN: on a bien appelé les bons composants
        verify(mockExecutor).cancel(any());
        verify(mockScheduler).unscheduleById(any());
        verify(taskConfiguration).getDefinitions();
        verify(strategyRouter, times(2)).resolveExecutor(any());
        verify(strategyRouter, times(2)).resolveScheduler(any());
    }
}
