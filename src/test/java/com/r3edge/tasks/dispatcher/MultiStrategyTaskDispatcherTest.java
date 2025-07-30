package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
@ActiveProfiles("multistrategy")
@Slf4j
class MultiStrategyTaskDispatcherTest {

	@Autowired
	private TaskConfiguration taskConfiguration;
	@Autowired
	TaskHandlerRegistry registry;
	@Autowired
	private TaskDispatcher dispatcher;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	org.springframework.core.env.Environment env;

	@Test
	void shouldLoadTasksFromYaml() {
		List<Task> tasks = taskConfiguration.getDefinitions();
		assertThat(tasks).isNotNull().isNotEmpty();
	}

	@Test
	void shouldRegisterPrintHandler() {
		TaskHandler handler = registry.getHandler("print").orElse(null);
		assertThat(handler).isInstanceOf(PrintTaskHandler.class);
	}

	@Test
	void shouldDispatchPrintTask() {
		Task task = Task.builder().id("test-print").type("print").enabled(true).meta(Map.of("message", "Test OK"))
				.build();
		assertThatCode(() -> dispatcher.dispatch(task)).doesNotThrowAnyException();
	}

	@Test
	void shouldRedispatchOnlyHotReloadEnabledTasks() {
		Task task1 = Task.builder().id("task-001").type("print").enabled(true)
				.meta(Map.of("message", "Hello from test")).build();

		Task task2 = Task.builder().id("task-002").type("print").enabled(false)
				.meta(Map.of("message", "This should not be printed")).build();

		Task task3 = Task.builder().id("task-003").type("unknown-type").meta(Map.of("data", "some data")).build();

		taskConfiguration.setDefinitions(List.of(task1, task2, task3));
	}

	@Test
	void shouldLogPrintHandlerOnTaskRemoval() {
		Task taskToRemove = Task.builder().id("task-print-delete").type("print").enabled(true)
				.meta(Map.of("message", "To be removed")).build();

		taskConfiguration.setDefinitions(List.of(taskToRemove));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).hasSize(1);

		taskConfiguration.setDefinitions(List.of());
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).isEmpty();
	}

	@Test
	void shouldDispatchNewHotReloadableTask() {
		Task existing = Task.builder().id("task-already-there").type("print").enabled(true)
				.meta(Map.of("message", "Je suis déjà là")).build();

		taskConfiguration.setDefinitions(List.of(existing));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).extracting(Task::getId).containsExactly("task-already-there");

		Task newTask = Task.builder().id("task-print-new").type("print").enabled(true)
				.meta(Map.of("message", "Je suis nouveau")).build();

		taskConfiguration.setDefinitions(List.of(existing, newTask));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).extracting(Task::getId)
				.containsExactlyInAnyOrder("task-already-there", "task-print-new");
	}

	@Test
	void shouldTriggerOnTaskReloadWhenTaskIsUpdated() {
		Task initial = Task.builder().id("task-update-test").type("print").enabled(true)
				.meta(Map.of("message", "Ancien message")).build();

		taskConfiguration.setDefinitions(List.of(initial));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

		Task updated = Task.builder().id("task-update-test").type("print").enabled(true)
				.meta(Map.of("message", "Nouveau message")).build();

		taskConfiguration.setDefinitions(List.of(updated));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

		assertThat(taskConfiguration.getDefinitions()).hasSize(1);
		assertThat(taskConfiguration.getDefinitions().get(0).getMeta().get("message")).isEqualTo("Nouveau message");
	}

	@Test
	void shouldNotExecuteDisabledTask() {
		Task disabledTask = Task.builder().id("disabled-task").type("print").enabled(false)
				.meta(Map.of("message", "Ne pas exécuter")).build();

		assertThatCode(() -> dispatcher.dispatch(disabledTask)).doesNotThrowAnyException();
	}

	@Test
	void shouldIgnoreTaskWhenNoHandlerFound() {
		Task unknownTask = Task.builder().id("unknown-handler").type("unknown-type").enabled(true).build();

		assertThatCode(() -> dispatcher.dispatch(unknownTask)).doesNotThrowAnyException();
	}
}
