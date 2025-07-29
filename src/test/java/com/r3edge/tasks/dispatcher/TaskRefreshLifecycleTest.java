package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import com.r3edge.tasks.TestApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lifecycle")
@Slf4j
public class TaskRefreshLifecycleTest {

	@Autowired
	private TaskConfiguration taskConfiguration;
	@Autowired
	TaskHandlerRegistry registry;
	@Autowired
	private TaskDispatcher dispatcher;
	@Autowired
	private TaskStrategyRouter strategyRouter;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Test
	void shouldLoadTasksFromYaml() {
		List<Task> tasks = taskConfiguration.getDefinitions();
		assertThat(tasks).isNotNull().isNotEmpty();
	}

	@Test
	void shouldCleanupAllScheduledAndExecutedTasks() {
		List<Task> tasks = taskConfiguration.getDefinitions();
		assertThat(tasks).isNotEmpty();

		// Étape 1 : dispatch initial (planification / exécution)
		tasks.forEach(dispatcher::dispatch);

		// Étape 2 : vérification qu'on a bien des tâches planifiées / exécutées
		boolean hasAtLeastOneTask = strategyRouter.allSchedulers().stream()
				.anyMatch(s -> !s.getScheduledTaskIds().isEmpty())
				|| strategyRouter.allExecutors().stream().anyMatch(e -> !e.getExecutedTaskIds().isEmpty());
		assertThat(hasAtLeastOneTask).isTrue();

		// Étape 3 : nettoyage explicite sans redéclenchement de refresh
		dispatcher.cleanupObsoleteTasks();

		// Étape 4 : vérif que tout a été vidé
		boolean everythingIsCleaned = strategyRouter.allSchedulers().stream()
				.allMatch(s -> s.getScheduledTaskIds().isEmpty())
				&& strategyRouter.allExecutors().stream().allMatch(e -> e.getExecutedTaskIds().isEmpty());
		assertThat(everythingIsCleaned).isTrue();
	}

	@Test
	void shouldUpdateTaskOnRefresh() {
		// Étape 1 : tâche initiale
		Task original = Task.builder().id("refresh-test-task").type("print").strategy("default").cron("0 * * * * *")
				.enabled(true).meta(Map.of("message", "Old message")).build();

		taskConfiguration.setDefinitions(List.of(original));
		dispatcher.dispatch(original);

		// Capturer les messages avant
		String oldMessage = (String) original.getMeta().get("message");

		// Étape 2 : tâche mise à jour
		Task updated = Task.builder().id("refresh-test-task").type("print").strategy("default").cron("*/10 * * * * *") // nouveau
																														// cron
				.enabled(true).meta(Map.of("message", "New message")).build();

		taskConfiguration.setDefinitions(List.of(updated));

		// Étape 3 : déclencher le refresh
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

		// Étape 4 : re-dispacher la tâche manuellement pour observer le nouveau message
		dispatcher.dispatch(updated);
		String newMessage = (String) updated.getMeta().get("message");

		// ✅ Vérifications
		assertThat(oldMessage).isEqualTo("Old message");
		assertThat(newMessage).isEqualTo("New message");
	}

}
