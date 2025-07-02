package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.r3edge.tasks.TestApplication;

/**
 * Vérifie que les tâches peuvent être exécutées ou non selon la présence d'un LockProvider.
 * Ces tests couvrent les cas où aucun mécanisme de verrouillage distribué n'est présent.
 */
@SpringBootTest(
    classes = TestApplication.class,
    properties = {
            "spring.autoconfigure.exclude=net.javacrumbs.shedlock.spring.autoconfigure.ShedLockAutoConfiguration"
        }
	)
@ExtendWith(OutputCaptureExtension.class)
class NoLockProviderIntegrationTest {

    @Autowired
    private TaskDispatcher dispatcher;

    /**
     * Ce test vérifie qu'une tâche marquée comme "distributedLock=true" échoue
     * si aucun LockProvider n'est configuré dans le contexte.
     */
    @Test
    void shouldFailDistributedTaskWhenNoLockProviderConfigured(CapturedOutput output) {
        // Given
        Task distributedTask = Task.builder()
                .id("no-lock-distributed")
                .type("print")
                .enabled(true)
                .distributedLock(true)
                .meta(Map.of("message", "Execution sans lock distribué"))
                .build();

        // When + Then
        assertThatThrownBy(() -> dispatcher.dispatch(distributedTask))
                .isInstanceOf(TaskExecutionException.class)
                .hasMessageContaining("Failed to execute task")
                .hasCauseInstanceOf(TaskExecutionException.class)
                .cause()
                .hasMessageContaining("no LockProvider configured for task");

        assertThat(output.getOut()).doesNotContain("📣 PrintTaskHandler exécuté");
    }


    /**
     * Ce test vérifie qu'une tâche locale (distributedLock=false) est bien exécutée
     * sans LockProvider.
     */
    @Test
    void shouldExecuteNonDistributedTaskWithoutLockWhenNoProvider(CapturedOutput output) {
        // Given
        Task localTask = Task.builder()
                .id("no-lock-local")
                .type("print")
                .enabled(true)
                .distributedLock(false)
                .meta(Map.of("message", "Execution locale sans lock"))
                .build();

        // When
        dispatcher.dispatch(localTask);

        // Then
        assertThat(output.getOut())
                .contains("📣 PrintTaskHandler exécuté : Execution locale sans lock")
                .doesNotContain("lock acquired")
                .doesNotContain("lock released");
    }

    /**
     * Ce test vérifie qu’une tâche désactivée n’est pas exécutée,
     * et donc qu’aucun handler n’est appelé même si un LockProvider est attendu.
     */
    @Test
    void shouldNotExecuteDisabledTask(CapturedOutput output) {
        // Given
        Task disabledTask = Task.builder()
                .id("disabled-task")
                .type("print")
                .enabled(false) // 🚫
                .distributedLock(true) // même avec lock demandé
                .meta(Map.of("message", "Ne pas exécuter"))
                .build();

        // When
        dispatcher.dispatch(disabledTask);

        // Then
        assertThat(output.getOut())
                .contains("est désactivée, elle ne sera pas exécutée")
                .doesNotContain("📣 PrintTaskHandler exécuté");
    }

    /**
     * Ce test vérifie que le dispatcher échoue correctement
     * si on lui donne un type de tâche inconnu (sans handler enregistré).
     */
    @Test
    void shouldFailWhenNoHandlerFound(CapturedOutput output) {
        // Given
        Task unknownTask = Task.builder()
                .id("unknown-handler")
                .type("unknown-type")
                .enabled(true)
                .distributedLock(false)
                .build();

        // When + Then
        assertThatThrownBy(() -> dispatcher.dispatch(unknownTask))
                .isInstanceOf(TaskExecutionException.class)
                .hasMessageContaining("No handler found");

        assertThat(output.getOut())
                .contains("⚠️ Aucun handler trouvé pour le type");
    }
}
