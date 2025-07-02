package com.r3edge.tasks.dispatcher;

import com.r3edge.tasks.TestApplication;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbc.template.JdbcTemplateLockProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(OutputCaptureExtension.class)
@DisplayName("LockingTaskExecutor Integration Tests")
class LockingTaskExecutorIntegrationTest {

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
        // We need to mock the PrintTaskHandler to verify its calls
        // and ensure it's not actually printing to console during these tests
        mockPrintTaskHandler = mock(PrintTaskHandler.class);
        // Register the mock handler with the registry
        // This assumes TaskHandlerRegistry is accessible or can be mocked/spied
        // For simplicity, let's assume we can get the real registry and replace its handler
        TaskHandlerRegistry registry = applicationContext.getBean(TaskHandlerRegistry.class);
        registry.registerHandler("print", mockPrintTaskHandler);

        // Clear output before each test
        output.clear();
    }

    @TestConfiguration
    @EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
    static class TestShedLockConfiguration {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:/shedlock_h2.sql") // Script to create shedlock table
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        @Primary // Ensure this LockProvider is picked up
        public LockProvider lockProvider(DataSource dataSource) {
            return new JdbcTemplateLockProvider(
                    JdbcTemplateLockProvider.Configuration.builder()
                            .withJdbcTemplate(new JdbcTemplate(dataSource))
                            .using
                            .withTableName("shedlock")
                            .build()
            );
        }
    }

    @Test
    @DisplayName("Should have LockingTaskExecutor bean when LockProvider is present")
    void shouldHaveLockingTaskExecutorBean() {
        assertThat(applicationContext.containsBean("lockingTaskExecutor")).isTrue();
        assertThat(applicationContext.getBean(LockingTaskExecutor.class)).isNotNull();
    }

    @Test
    @DisplayName("Should acquire and release lock for distributedLock=true task")
    void shouldAcquireAndReleaseLockForDistributedTask() {
        Task distributedTask = Task.builder()
                .id("test-distributed-task")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "This task should be locked"))
                .build();

        taskDispatcher.dispatch(distributedTask);

        // Verify that the handler was called
        verify(mockPrintTaskHandler, times(1)).handle(distributedTask);

        // Verify logs for lock acquisition and release
        assertThat(output.getOut()).contains("Attempting to acquire distributed lock for task test-distributed-task");
        assertThat(output.getOut()).contains("Distributed lock acquired for task test-distributed-task");
        assertThat(output.getOut()).contains("Distributed lock released for task test-distributed-task");
    }

    @Test
    @DisplayName("Should not acquire lock for distributedLock=false task")
    void shouldNotAcquireLockForNonDistributedTask() {
        Task nonDistributedTask = Task.builder()
                .id("test-non-distributed-task")
                .type("print")
                .enabled(true)
                .distributedLock(false)
                .meta(Map.of("message", "This task should not be locked"))
                .build();

        taskDispatcher.dispatch(nonDistributedTask);

        // Verify that the handler was called
        verify(mockPrintTaskHandler, times(1)).handle(nonDistributedTask);

        // Verify logs do NOT contain lock messages
        assertThat(output.getOut()).doesNotContain("Attempting to acquire distributed lock");
        assertThat(output.getOut()).doesNotContain("Distributed lock acquired");
        assertThat(output.getOut()).doesNotContain("Distributed lock released");
    }

    @Test
    @DisplayName("Should skip execution if lock cannot be acquired")
    void shouldSkipExecutionIfLockCannotBeAcquired() {
        // Simulate another instance holding the lock
        LockProvider lockProvider = applicationContext.getBean(LockProvider.class);
        lockProvider.lock(new net.javacrumbs.shedlock.core.LockConfiguration(
                "test-concurrent-task",
                Duration.ofSeconds(60), // lockAtMostFor
                Duration.ofSeconds(1)   // lockAtLeastFor
        ));

        Task concurrentTask = Task.builder()
                .id("test-concurrent-task")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "This task should be skipped"))
                .build();

        taskDispatcher.dispatch(concurrentTask);

        // Verify that the handler was NOT called
        verify(mockPrintTaskHandler, never()).handle(concurrentTask);

        // Verify logs for skipping execution
        assertThat(output.getOut()).contains("Could not acquire distributed lock for task test-concurrent-task. Skipping execution.");
    }
}
