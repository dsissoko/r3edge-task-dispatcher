package com.r3edge.tasks.dispatcher.impl.defaultspring;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.r3edge.tasks.dispatcher.ITaskExecutor;
import com.r3edge.tasks.dispatcher.Task;
import com.r3edge.tasks.dispatcher.TaskHandler;
import com.r3edge.tasks.dispatcher.TaskInvokerService;

import jakarta.annotation.PostConstruct;
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

	/**
	 * Annule l'exécution une tâche. Si aucune tâche n’est trouvée, l’appel est
	 * ignoré.
	 *
	 * @param task tâche à annuler.
	 */
	@Override
	public void cancel(Task task) {
		log.info("Cancel de la tâche (TODO) {}", task);
	}

	/**
	 * Retourne l’ensemble des identifiants des tâches actuellement planifiées.
	 *
	 * @return un ensemble d’identifiants de tâches.
	 */
	@Override
	public Set<String> getExecutedTaskIds() {
		return Collections.unmodifiableSet(executedTaskIds);
	}

	@PostConstruct
	private void logActivation() {
		log.debug("🔧 Bean DefaultTaskStrategyConfig initialisé");
	}
}
