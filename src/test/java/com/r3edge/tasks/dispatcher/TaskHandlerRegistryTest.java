// TaskHandlerRegistryTest.java
package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        };

        registry.addHandler(handler);

        assertThat(registry.getHandler("print")).hasValue(handler);
    }
}