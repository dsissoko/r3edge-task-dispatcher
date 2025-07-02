package com.r3edge.tasks.dispatcher;

import com.r3edge.tasks.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(OutputCaptureExtension.class)
@DisplayName("No LockProvider Integration Tests")
class NoLockProviderIntegrationTest {

    @Autowired
    private TaskDispatcher taskDispatcher;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CapturedOutput output;

    // Mock handler to verify execution
    private PrintTaskHandler mockPrintTaskHandler;

    @BeforeEach
    void setUp() {
        mockPrintTaskHandler = mock(PrintTaskHandler.class);
        TaskHandlerRegistry registry = applicationContext.getBean(TaskHandlerRegistry.class);
        registry.registerHandler("print", mockPrintTaskHandler);

        output.clear();
    }

    @Test
    @DisplayName("Should NOT have LockingTaskExecutor bean when no LockProvider is present")
    void shouldNotHaveLockingTaskExecutorBean() {
        assertThat(applicationContext.containsBean("lockingTaskExecutor")).isFalse();
    }

    @Test
    @DisplayName("Should execute distributedLock=true task via DefaultTaskExecutor when no LockProvider")
    void shouldExecuteDistributedTaskWithoutLockWhenNoProvider() {
        Task distributedTask = Task.builder()
                .id("test-distributed-task-no-provider")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "This task should be executed without lock"))
                .build();

        taskDispatcher.dispatch(distributedTask);

        // Verify that the handler was called
        verify(mockPrintTaskHandler, times(1)).handle(distributedTask);

        // Verify logs do NOT contain lock messages
        assertThat(output.getOut()).doesNotContain("Attempting to acquire distributed lock");
        assertThat(output.getOut()).doesNotContain("Distributed lock acquired");
        assertThat(output.getOut()).doesNotContain("Distributed lock released");
    }

    @Test
    @DisplayName("Should execute distributedLock=false task via DefaultTaskExecutor when no LockProvider")
    void shouldExecuteNonDistributedTaskWithoutLockWhenNoProvider() {
        Task nonDistributedTask = Task.builder()
                .id("test-non-distributed-task-no-provider")
                .type("print")
                .enabled(true)
                .distributedLock(false)
                .meta(Map.of("message", "This task should be executed without lock"))
                .build();

        taskDispatcher.dispatch(nonDistributedTask);

        // Verify that the handler was called
        verify(mockPrintTaskHandler, times(1)).handle(nonDistributedTask);

        // Verify logs do NOT contain lock messages
        assertThat(output.getOut()).doesNotContain("Attempting to acquire distributed lock");
        assertThat(output.getOut()).doesNotContain("Distributed lock acquired");
        assertThat(output.getOut()).doesNotContain("Distributed lock released");
    }
}
