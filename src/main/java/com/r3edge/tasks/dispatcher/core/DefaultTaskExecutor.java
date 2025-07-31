package com.r3edge.tasks.dispatcher.core;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

	private final TaskConfiguration taskConfiguration;
	private final TaskInvokerService taskInvokerService;
	// Suivi des tâches dispatchées
	private final Set<String> executedTaskIds = ConcurrentHashMap.newKeySet();
	private static final ScheduledExecutorService DELAY_EXECUTOR = Executors.newSingleThreadScheduledExecutor();


	/**
	 * Exécute la tâche donnée en utilisant le handler fourni.
	 *
	 * @param task    La tâche à exécuter.
	 * @param handler Le handler responsable de la logique de la tâche.
	 */
	@Override
	public void execute(Task task, TaskHandler handler) {
	    log.info("✅ Mise en file d'attente via DefaultTaskScheduler : id={}, at={}", task.getId(), task.getAt());
	    // Gestion du champ "at" pour exécution différée
	    if (task.getAt() != null) {
	        java.time.Instant now = java.time.Instant.now();
	        java.time.Instant at = task.getAt();
	        long delayMillis = Duration.between(now, at).toMillis();

	        if (delayMillis < 0) {
	        	log.warn("⚠️ Tâche {} avec date at dépassée : {}", task.getId(), at);
		        if (taskConfiguration.isSkipLateTasks()) {
		        	log.warn("⚠️ Tâche {} ignorée car date at dépassée : {}", task.getId(), at);
		        	return;
		        }	        	
	        }

	        DELAY_EXECUTOR.schedule(() -> {
	            log.info("▶️ Exécution différée (ou immédiate pour les tâches en retard) (at) de la tâche {} à {}", task.getId(), at);
	            taskInvokerService.execute(task, org.slf4j.LoggerFactory.getLogger(handler.getClass()));
	        }, delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS);

	        log.info("✅ Tâche {} planifiée pour exécution différée (ou immédiate pour les tâches en retard) à {}", task.getId(), at);
	    } else {
	        taskInvokerService.execute(task, org.slf4j.LoggerFactory.getLogger(handler.getClass()));
	    }
	    executedTaskIds.add(task.getId());
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
		log.debug("✅ Bean DefaultTaskStrategyConfig initialisé");
	}
}
