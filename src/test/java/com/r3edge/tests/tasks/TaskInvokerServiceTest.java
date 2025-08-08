package com.r3edge.tests.tasks;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
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
            public String getName() {
                return "dummy";
            }

            @Override
            public void execute(TaskDescriptor task) {
                // comportement simulé OK
            }
        };

        registry = new TaskHandlerRegistry(List.of(dummyHandler));
        invoker = new TaskInvokerService(registry);
    }

    @Test
    void shouldInvokeHandlerSuccessfully() {
        TaskDescriptor task = TaskDescriptor.builder()
                .id("t1")
                .handler("dummy")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatCode(() -> invoker.execute(task)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenHandlerIsMissing() {
        TaskDescriptor task = TaskDescriptor.builder()
                .id("t2")
                .handler("unknown-type")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.execute(task))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Aucun handler pour la tâche");
    }

    @Test
    void shouldThrowIfHandlerFails() {
        TaskHandler failingHandler = new TaskHandler() {
            @Override
            public String getName() {
                return "fail";
            }

            @Override
            public void execute(TaskDescriptor task) {
                throw new RuntimeException("boum");
            }
        };

        registry.addHandler(failingHandler);

        TaskDescriptor task = TaskDescriptor.builder()
                .id("t3")
                .handler("fail")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.execute(task))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("boum");
    }
}
