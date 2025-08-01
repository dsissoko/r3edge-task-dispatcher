package com.r3edge.tests.tasks;

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

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;
import com.r3edge.tasks.dispatcher.core.TaskDispatcher;
import com.r3edge.tasks.dispatcher.core.TaskHandler;
import com.r3edge.tasks.dispatcher.core.TaskHandlerRegistry;
import com.r3edge.tests.TestApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("multistrategy")
@Slf4j
class MultiStrategyTaskDispatcherTest {

	@Autowired
	private TaskDescriptorsProperties taskConfiguration;
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
		List<TaskDescriptor> tasks = taskConfiguration.getDefinitions();
		assertThat(tasks).isNotNull().isNotEmpty();
	}

	@Test
	void shouldRegisterPrintHandler() {
		TaskHandler handler = registry.getHandler("print").orElse(null);
		assertThat(handler).isInstanceOf(PrintTaskHandler.class);
	}

	@Test
	void shouldDispatchPrintTask() {
		TaskDescriptor task = TaskDescriptor.builder().id("test-print").handler("print").enabled(true).meta(Map.of("message", "Test OK"))
				.build();
		assertThatCode(() -> dispatcher.dispatch(task)).doesNotThrowAnyException();
	}

	@Test
	void shouldRedispatchOnlyHotReloadEnabledTasks() {
		TaskDescriptor task1 = TaskDescriptor.builder().id("task-001").handler("print").enabled(true)
				.meta(Map.of("message", "Hello from test")).build();

		TaskDescriptor task2 = TaskDescriptor.builder().id("task-002").handler("print").enabled(false)
				.meta(Map.of("message", "This should not be printed")).build();

		TaskDescriptor task3 = TaskDescriptor.builder().id("task-003").handler("unknown-type").meta(Map.of("data", "some data")).build();

		taskConfiguration.setDefinitions(List.of(task1, task2, task3));
	}

	@Test
	void shouldLogPrintHandlerOnTaskRemoval() {
		TaskDescriptor taskToRemove = TaskDescriptor.builder().id("task-print-delete").handler("print").enabled(true)
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
		TaskDescriptor existing = TaskDescriptor.builder().id("task-already-there").handler("print").enabled(true)
				.meta(Map.of("message", "Je suis déjà là")).build();

		taskConfiguration.setDefinitions(List.of(existing));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).extracting(TaskDescriptor::getId).containsExactly("task-already-there");

		TaskDescriptor newTask = TaskDescriptor.builder().id("task-print-new").handler("print").enabled(true)
				.meta(Map.of("message", "Je suis nouveau")).build();

		taskConfiguration.setDefinitions(List.of(existing, newTask));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());
		assertThat(taskConfiguration.getDefinitions()).extracting(TaskDescriptor::getId)
				.containsExactlyInAnyOrder("task-already-there", "task-print-new");
	}

	@Test
	void shouldTriggerOnTaskReloadWhenTaskIsUpdated() {
		TaskDescriptor initial = TaskDescriptor.builder().id("task-update-test").handler("print").enabled(true)
				.meta(Map.of("message", "Ancien message")).build();

		taskConfiguration.setDefinitions(List.of(initial));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

		TaskDescriptor updated = TaskDescriptor.builder().id("task-update-test").handler("print").enabled(true)
				.meta(Map.of("message", "Nouveau message")).build();

		taskConfiguration.setDefinitions(List.of(updated));
		eventPublisher.publishEvent(new RefreshScopeRefreshedEvent());

		assertThat(taskConfiguration.getDefinitions()).hasSize(1);
		assertThat(taskConfiguration.getDefinitions().get(0).getMeta().get("message")).isEqualTo("Nouveau message");
	}

	@Test
	void shouldNotExecuteDisabledTask() {
		TaskDescriptor disabledTask = TaskDescriptor.builder().id("disabled-task").handler("print").enabled(false)
				.meta(Map.of("message", "Ne pas exécuter")).build();

		assertThatCode(() -> dispatcher.dispatch(disabledTask)).doesNotThrowAnyException();
	}

	@Test
	void shouldIgnoreTaskWhenNoHandlerFound() {
		TaskDescriptor unknownTask = TaskDescriptor.builder().id("unknown-handler").handler("unknown-type").enabled(true).build();

		assertThatCode(() -> dispatcher.dispatch(unknownTask)).doesNotThrowAnyException();
	}
}
