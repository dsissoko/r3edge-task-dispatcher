package com.r3edge.tasks.dispatcher;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default task executor that simply calls the task handler. This executor does
 * not apply any distributed locking.
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultTaskExecutor implements ITaskExecutor {

	private final TaskInvokerService taskInvokerService;
	// Suivi des tâches dispatchées
	private final Set<String> executedTaskIds = ConcurrentHashMap.newKeySet();

	/**
	 * Exécute la tâche donnée en utilisant le handler fourni.
	 *
	 * @param task    La tâche à exécuter.
	 * @param handler Le handler responsable de la logique de la tâche.
	 */
	@Override
	public void execute(Task task, TaskHandler handler) {
		log.info("✅ Mise en file d'attente via DefaultTaskScheduler : id={}, at={}", task.getId(), task.getAt());
		taskInvokerService.invokeNow(task);
	}

	@Override
	public String strategyKey() {
		return "default";
	}

	@Override
	/**
	 * Retourne un logger SLF4J standard adapté à la classe cible.
	 *
	 * @param clazz La classe pour laquelle le logger est créé.
	 * @return Un logger SLF4J classique.
	 */
	public Logger getSlf4JLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	@Override
	public void cancel(Task task) {
		log.info("Cancel de la tâche (TODO) {}", task);
	}

	@Override
	public Set<String> getExecutedTaskIds() {
		return Collections.unmodifiableSet(executedTaskIds);
	}

}
