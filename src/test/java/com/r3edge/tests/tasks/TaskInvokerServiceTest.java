package com.r3edge.tests.tasks;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.r3edge.tasks.dispatcher.core.ITaskExecutionListener;
import com.r3edge.tasks.dispatcher.core.SimpleTaskExecutionListener;
import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

/**
 * Tests unitaires pour TaskInvokerService.
 * 
 * Vérifie l'exécution instrumentée, la gestion des handlers manquants et des exceptions dans les handlers.
 */
class TaskInvokerServiceTest {

    private TaskHandlerRegistry registry;
    private TaskInvokerService invoker;

    @BeforeEach
    void setUp() {
        TaskHandler dummyHandler = new TaskHandler() {
            @Override
            public String getType() {
                return "dummy";
            }

            @Override
            public void handle(Task task) {
                // comportement simulé OK
            }

            @Override
            public void handle(Task task, Logger logger) {
                // comportement simulé OK
            }
        };

        registry = new TaskHandlerRegistry(List.of(dummyHandler));
        ITaskExecutionListener listener = new SimpleTaskExecutionListener();
        invoker = new TaskInvokerService(registry, listener);
    }

    @Test
    void shouldInvokeHandlerSuccessfully() {
        Task task = Task.builder()
                .id("t1")
                .type("dummy")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatCode(() -> invoker.execute(task, null)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenHandlerIsMissing() {
        Task task = Task.builder()
                .id("t2")
                .type("unknown-type")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.execute(task, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Aucun handler pour le type");
    }

    @Test
    void shouldThrowIfHandlerFails() {
        TaskHandler failingHandler = new TaskHandler() {
            @Override
            public String getType() {
                return "fail";
            }

            @Override
            public void handle(Task task) {
                throw new RuntimeException("boum");
            }

            @Override
            public void handle(Task task, Logger logger) {
                throw new RuntimeException("boum");
            }
        };

        registry.addHandler(failingHandler);

        Task task = Task.builder()
                .id("t3")
                .type("fail")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.execute(task, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("boum");
    }
}
