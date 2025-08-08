package com.r3edge.tests.tasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.r3edge.tasks.dispatcher.core.IFireAndForgetExecutor;
import com.r3edge.tasks.dispatcher.core.IScheduledExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;
import com.r3edge.tasks.dispatcher.core.TaskDispatcher;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;
import com.r3edge.tasks.dispatcher.core.TaskStrategyRouter;

public class TaskRefreshLifecycleTest {

    private TaskDescriptorsProperties taskConfiguration;
    private TaskHandlerRegistry registry;
    private TaskStrategyRouter strategyRouter;
    private TaskDispatcher dispatcher;

    @BeforeEach
    void setup() {
        taskConfiguration = mock(TaskDescriptorsProperties.class);
        registry = mock(TaskHandlerRegistry.class);
        strategyRouter = mock(TaskStrategyRouter.class);
        dispatcher = new TaskDispatcher(taskConfiguration, strategyRouter);
    }

    @Test
    void refreshTasks_shouldCleanupAndRedispatch() {
        // GIVEN: deux tâches fictives
        TaskDescriptor task1 = TaskDescriptor.builder().id("task-001").handler("cleanup").strategy("default").enabled(true).build();
        TaskDescriptor task2 = TaskDescriptor.builder().id("task-002").handler("cleanup").strategy("jobrunr").enabled(true).build();

        when(taskConfiguration.getDefinitions()).thenReturn(List.of(task1, task2));

        // 💡 Utilise ton handler réel ici
        when(registry.getHandler(anyString())).thenReturn(Optional.of(new CleanUpHandler()));

        // Mocks des executors/schedulers
        IFireAndForgetExecutor mockExecutor = mock(IFireAndForgetExecutor.class);
        when(mockExecutor.getExecutedTaskIds()).thenReturn(Set.of("task-999"));

        IScheduledExecutor mockScheduler = mock(IScheduledExecutor.class);
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
        verify(taskConfiguration, times(2)).getDefinitions();
        verify(strategyRouter, times(2)).resolveExecutor(any());
        verify(strategyRouter, times(2)).resolveScheduler(any());
    }
}
