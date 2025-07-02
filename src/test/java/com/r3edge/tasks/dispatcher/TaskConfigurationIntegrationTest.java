package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;

import com.r3edge.tasks.TestApplication;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(OutputCaptureExtension.class)
class TaskConfigurationIntegrationTest {

    @Autowired private TaskConfiguration taskConfiguration;
    @Autowired TaskHandlerRegistry registry;
    @Autowired private TaskDispatcher dispatcher;
    @Autowired private ApplicationEventPublisher eventPublisher;

    // --- 🔽 Tests basés sur le YAML initial
    @Test
    void shouldLoadTasksFromYaml() {
        List<Task> tasks = taskConfiguration.getDefinitions();
        assertThat(tasks).isNotNull().isNotEmpty();
    }

    @Test
    void shouldRegisterPrintHandler() {
        TaskHandler handler = registry.getHandler("print");
        assertThat(handler).isInstanceOf(PrintTaskHandler.class);
    }

    @Test
    void shouldDispatchPrintTask() {
        Task task = Task.builder()
                .id("test-print")
                .type("print")
                .enabled(true)
                .meta(Map.of("message", "Test OK"))
                .build();
        assertThatCode(() -> dispatcher.dispatch(task)).doesNotThrowAnyException();
    }

    // --- 🔽 Tests avec setup explicite des tâches

    @Test
    void shouldRedispatchOnlyHotReloadEnabledTasks(CapturedOutput output) {
        Task task1 = Task.builder()
                .id("task-001")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "Hello from test"))
                .build();

        Task task2 = Task.builder()
                .id("task-002")
                .type("print")
                .enabled(false)
                .hotReload(true)
                .meta(Map.of("message", "This should not be printed"))
                .build();

        Task task3 = Task.builder()
                .id("task-003")
                .type("unknown-type")
                .hotReload(true)
                .meta(Map.of("data", "some data"))
                .build();

        taskConfiguration.setDefinitions(List.of(task1, task2, task3));

        try {
            eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
        } catch (TaskExecutionException e) {
            assertThat(e.getMessage()).contains("No handler found for task type: unknown-type");
        }

        assertThat(output.getOut()).contains("📣 PrintTaskHandler exécuté : Hello from test");
        assertThat(output.getOut()).doesNotContain("This should not be printed");
    }

    @Test
    void shouldLogPrintHandlerOnTaskRemoval(CapturedOutput output) {
        Task taskToRemove = Task.builder()
                .id("task-print-delete")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "To be removed"))
                .build();

        taskConfiguration.setDefinitions(List.of(taskToRemove));
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

        assertThat(taskConfiguration.getDefinitions()).hasSize(1);

        taskConfiguration.setDefinitions(List.of()); // Suppression
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

        assertThat(output.getOut()).contains("🗑️ PrintTaskHandler : tâche supprimée → id=task-print-delete");
        assertThat(taskConfiguration.getDefinitions()).isEmpty();
    }

    @Test
    void shouldDispatchNewHotReloadableTask(CapturedOutput output) {
        Task existing = Task.builder()
                .id("task-already-there")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "Je suis déjà là"))
                .build();

        taskConfiguration.setDefinitions(List.of(existing));
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
        assertThat(taskConfiguration.getDefinitions()).extracting(Task::getId).containsExactly("task-already-there");
        assertThat(output.getOut()).contains("📣 PrintTaskHandler exécuté : Je suis déjà là");

        Task newTask = Task.builder()
                .id("task-print-new")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "Je suis nouveau"))
                .build();

        taskConfiguration.setDefinitions(List.of(existing, newTask));
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

        assertThat(output.getOut()).contains("📣 PrintTaskHandler exécuté : Je suis nouveau");
        assertThat(taskConfiguration.getDefinitions())
                .extracting(Task::getId)
                .containsExactlyInAnyOrder("task-already-there", "task-print-new");
    }

    @Test
    void shouldTriggerOnTaskReloadWhenTaskIsUpdated(CapturedOutput output) {
        Task initial = Task.builder()
                .id("task-update-test")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "Ancien message"))
                .build();

        taskConfiguration.setDefinitions(List.of(initial));
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

        Task updated = Task.builder()
                .id("task-update-test")
                .type("print")
                .enabled(true)
                .hotReload(true)
                .meta(Map.of("message", "Nouveau message"))
                .build();

        taskConfiguration.setDefinitions(List.of(updated));
        eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

        assertThat(output.getOut())
                .contains("🔁 PrintTaskHandler : tâche rechargée")
                .contains("id=task-update-test")
                .contains("ancien=Ancien message")
                .contains("nouveau=Nouveau message");
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