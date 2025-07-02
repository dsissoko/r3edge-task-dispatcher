package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.r3edge.tasks.TestApplication;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * Vérifie le comportement du LockingTaskExecutor en présence d’un LockProvider.
 */
@SpringBootTest(classes = { TestApplication.class, LockingTaskExecutorIntegrationTest.TestShedLockConfiguration.class })
@ExtendWith(OutputCaptureExtension.class)
class LockingTaskExecutorIntegrationTest {

    @Autowired
    private TaskDispatcher dispatcher;

    @Autowired
    private LockProvider lockProvider;

    /**
     * Vérifie que le bean LockingTaskExecutor est bien présent dans le contexte.
     */
    @Test
    void shouldExposeLockingTaskExecutorBean() {
        assertThat(dispatcher).isNotNull();
        assertThat(lockProvider).isNotNull();
    }

    /**
     * Vérifie que la tâche avec distributedLock=true est bien exécutée
     * avec acquisition et libération du verrou.
     */
    @Test
    void shouldExecuteDistributedTaskWithLock(CapturedOutput output) {
        // Given
        Task task = Task.builder()
                .id("with-lock")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "Lock OK"))
                .build();

        // When
        dispatcher.dispatch(task);

        // Then
        assertThat(output.getOut())
                .contains("Attempting to acquire distributed lock for task with-lock")
                .contains("Distributed lock acquired for task with-lock")
                .contains("📣 PrintTaskHandler exécuté : Lock OK")
                .contains("Distributed lock released for task with-lock");
    }

    /**
     * Vérifie que la tâche avec distributedLock=false est bien exécutée
     * sans tentative d’acquisition de verrou.
     */
    @Test
    void shouldExecuteTaskWithoutLock(CapturedOutput output) {
        // Given
        Task task = Task.builder()
                .id("no-lock")
                .type("print")
                .enabled(true)
                .distributedLock(false)
                .meta(Map.of("message", "Pas de lock"))
                .build();

        // When
        dispatcher.dispatch(task);

        // Then
        assertThat(output.getOut())
                .contains("📣 PrintTaskHandler exécuté : Pas de lock")
                .doesNotContain("Attempting to acquire distributed lock")
                .doesNotContain("Distributed lock acquired")
                .doesNotContain("Distributed lock released");
    }

    /**
     * Vérifie que la tâche est ignorée si le verrou ne peut pas être acquis.
     */
    @Test
    void shouldSkipTaskIfLockNotAcquired(CapturedOutput output) {
        // Given : un verrou actif pour "locked-task"
        lockProvider.lock(new LockConfiguration(
                Instant.now(),
                "locked-task",
                Duration.ofSeconds(60),
                Duration.ofSeconds(1)
        ));

        Task task = Task.builder()
                .id("locked-task")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "Je dois être ignorée"))
                .build();

        // When
        dispatcher.dispatch(task);

        // Then
        assertThat(output.getOut())
                .contains("Could not acquire distributed lock for task locked-task. Skipping execution.")
                .doesNotContain("📣 PrintTaskHandler exécuté");
    }

    /**
     * Configuration de test : base H2 + ShedLock via JdbcTemplate.
     */
    @TestConfiguration
    @EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
    static class TestShedLockConfiguration {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:/shedlock_h2.sql")
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        @Primary
        public LockProvider lockProvider(DataSource dataSource) {
            return new JdbcTemplateLockProvider(dataSource);
        }
    }
}
