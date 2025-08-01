package com.r3edge.tasks.dispatcher.impl.jobrunr;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jobrunr.scheduling.JobRequestScheduler;

import com.r3edge.tasks.dispatcher.core.IFireAndForgetExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;
import com.r3edge.tasks.dispatcher.core.TasksUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link IFireAndForgetExecutor} utilisant JobRunr pour l'exécution
 * des tâches.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrFireAndForgetExecutor implements IFireAndForgetExecutor {

	private final Set<String> executedTaskIds = ConcurrentHashMap.newKeySet();
	private final TaskDescriptorsProperties taskConfiguration;
	private final JobRequestScheduler jobRequestScheduler;

	@Override
	public void execute(TaskDescriptor task) {
		UUID jobId = TasksUtils.getJobId(task);
		if (task.getAt() != null) {
			java.time.Instant now = java.time.Instant.now();
			java.time.Instant at = task.getAt();
			long delayMillis = java.time.Duration.between(now, at).toMillis();

			if (delayMillis < 0) {
				log.warn("⚠️ Tâche {} avec date at dépassée : {}", task.getId(), at);
				if (taskConfiguration.isSkipLateTasks()) {
					log.warn("⚠️ Tâche {} ignorée car date at dépassée : {}", task.getId(), at);
					return;
				}
			}
			jobRequestScheduler.schedule(jobId, at, new TaskJobRequest(task));
			log.info("✅ Tâche {} planifiée avec JobRunr pour exécution différée à {}", task.getId(), at);
		} else {
			jobRequestScheduler.enqueue(jobId,new TaskJobRequest(task));
			log.info("✅ Tâche {} mise en file JobRunr (Fire & Forget)", task.getId());
		}
		executedTaskIds.add(task.getId());
	}

	@Override
	public String strategyKey() {
		return "jobrunr";
	}

	@Override
	public void cancel(TaskDescriptor task) {
		try {
			jobRequestScheduler.delete(TasksUtils.getJobId(task));
			executedTaskIds.remove(task.getId());
			log.info("TaskDescriptor [{}] annulée", task.getId());
		} catch (Exception e) {
			log.warn("Echec d'annualtion de la tâche [{}] : {}", task.getId(), e.getMessage());
		}
	}

	@Override
	public Set<String> getExecutedTaskIds() {
		return Collections.unmodifiableSet(executedTaskIds);
	}

	/**
	 * Vérifie si une tâche avec l'identifiant donné a déjà été exécutée.
	 *
	 * @param taskId l'identifiant de la tâche
	 * @return true si la tâche a été exécutée, false sinon
	 */
	public boolean hasExecutedTask(String taskId) {
		return executedTaskIds.contains(taskId);
	}

	@PostConstruct
	private void logActivation() {
		log.debug("✅ Bean JobRunrFireAndForgetExecutor initialisé");
	}

}
