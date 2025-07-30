package com.r3edge.tasks.dispatcher.impl.jobrunr;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jobrunr.scheduling.BackgroundJobRequest;

import com.r3edge.tasks.dispatcher.core.ITaskExecutor;
import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;
import com.r3edge.tasks.dispatcher.core.TasksUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link ITaskExecutor} utilisant JobRunr pour l'exécution
 * des tâches.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrTaskExecutor implements ITaskExecutor {

	private final Set<String> executedTaskIds = ConcurrentHashMap.newKeySet();

	@Override
	public void execute(Task task, TaskHandler handler) {
		UUID jobId = TasksUtils.getJobId(task);
		if (task.getAt() != null) {
			BackgroundJobRequest.schedule(jobId, task.getAt(), new TaskJobRequest(task, handler.getClass().getName()));
		} else {
			BackgroundJobRequest.enqueue(jobId, new TaskJobRequest(task, handler.getClass().getName()));
		}
		executedTaskIds.add(task.getId());
	}

	@Override
	public String strategyKey() {
		return "jobrunr";
	}

	@Override
	public void cancel(Task task) {
		try {
			BackgroundJobRequest.delete(TasksUtils.getJobId(task));
			executedTaskIds.remove(task.getId());
			log.info("Task [{}] annulée", task.getId());
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
		log.debug("✅ Bean JobRunrTaskExecutor initialisé");
	}

}
