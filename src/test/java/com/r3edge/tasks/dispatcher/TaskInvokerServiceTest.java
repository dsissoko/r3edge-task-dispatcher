package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

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
				// TODO Auto-generated method stub
				
			}
        };

        registry = new TaskHandlerRegistry(List.of(dummyHandler));
        invoker = new TaskInvokerService(registry);
    }

    @Test
    void shouldInvokeHandlerSuccessfully() {
        Task task = Task.builder()
                .id("t1")
                .type("dummy")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatCode(() -> invoker.invokeNow(task)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenHandlerIsMissing() {
        Task task = Task.builder()
                .id("t2")
                .type("unknown-type")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.invokeNow(task))
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
				// TODO Auto-generated method stub
				
			}
        };

        registry.addHandler(failingHandler);

        Task task = Task.builder()
                .id("t3")
                .type("fail")
                .enabled(true)
                .meta(Map.of())
                .build();

        assertThatThrownBy(() -> invoker.invokeNow(task))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("boum");
    }
}

