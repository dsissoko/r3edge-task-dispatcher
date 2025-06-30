package com.r3edge.task.dispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskHandlerRegistryTest {

    @Mock
    private TaskHandler mockHandler;

    private TaskHandlerRegistry registry;

    @BeforeEach
    void setUp() {
        when(mockHandler.getType()).thenReturn("test-type");
        registry = new TaskHandlerRegistry(Collections.singletonList(mockHandler));
    }

    @Test
    void shouldRegisterHandlersOnStartup() {
        // Act
        TaskHandler handler = registry.getHandler("test-type");

        // Assert
        assertThat(handler).isNotNull();
        assertThat(handler).isEqualTo(mockHandler);
    }

    @Test
    void shouldReturnNullForUnknownHandler() {
        // Act
        TaskHandler handler = registry.getHandler("unknown-type");

        // Assert
        assertThat(handler).isNull();
    }
}
