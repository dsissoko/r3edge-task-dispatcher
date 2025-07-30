// TaskHandlerRegistryTest.java
package com.r3edge.tests.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;

class TaskHandlerRegistryTest {

    private TaskHandlerRegistry registry;

    @BeforeEach
    void setup() {
        registry = new TaskHandlerRegistry(List.of());
    }

    @Test
    void shouldReturnNullIfNotRegistered() {
    	assertThat(registry.getHandler("unknown")).isEmpty();
    }

    @Test
    void shouldRegisterAndRetrieveHandler() {
        TaskHandler handler = new TaskHandler() {
            @Override
            public String getType() {
                return "print";
            }

            @Override
            public void handle(Task task) {
            }

			@Override
			public void handle(Task task, Logger logger) {
				// TODO Auto-generated method stub
				
			}
        };

        registry.addHandler(handler);

        assertThat(registry.getHandler("print")).hasValue(handler);
    }
}