package com.r3edge.tasks.dispatcher.impl.jobrunr;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jobrunr.scheduling.BackgroundJobRequest;
import org.slf4j.Logger;

import com.r3edge.tasks.dispatcher.ITaskScheduler;
import com.r3edge.tasks.dispatcher.Task;
import com.r3edge.tasks.dispatcher.TaskHandler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link ITaskScheduler} utilisant JobRunr pour la
 * planification des tâches récurrentes.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrTaskScheduler implements ITaskScheduler {

	private final Set<String> scheduledTaskIds = ConcurrentHashMap.newKeySet();

	@Override
	public void schedule(Task task, TaskHandler handler) {
		String cron = task.getCron();
		if (cron != null && !cron.isBlank()) {
			BackgroundJobRequest.scheduleRecurrently(task.getId(), cron, new TaskJobRequest(task, handler.getClass().getName()));
			scheduledTaskIds.add(task.getId());
			log.info("✅ JobRunr CRON job enregistré: id={}, cron={}", task.getId(), cron);
		} else {
			log.warn("⏭️ Tâche ignorée dans JobRunrTaskScheduler (pas de cron défini) : {}", task.getId());
		}
	}

	@Override
	public String strategyKey() {
		return "jobrunr";
	}

	@Override
	public void unscheduleById(String taskId) {
		BackgroundJobRequest.deleteRecurringJob(taskId);
		scheduledTaskIds.remove(taskId);
	}

	@Override
	public void unschedule(Task task) {
		unscheduleById(task.getId());
	}

	@Override
	public Set<String> getScheduledTaskIds() {
		return Collections.unmodifiableSet(scheduledTaskIds);
	}

	@PostConstruct
	private void logActivation() {
		log.debug("🔧 Bean JobRunrTaskScheduler initialisé");
	}

}
