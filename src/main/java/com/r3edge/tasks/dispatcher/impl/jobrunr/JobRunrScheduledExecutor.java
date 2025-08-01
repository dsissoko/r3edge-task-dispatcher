package com.r3edge.tasks.dispatcher.impl.jobrunr;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jobrunr.scheduling.JobRequestScheduler;

import com.r3edge.tasks.dispatcher.core.IScheduledExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link IScheduledExecutor} utilisant JobRunr pour la
 * planification des tâches récurrentes.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrScheduledExecutor implements IScheduledExecutor {

	private final Set<String> scheduledTaskIds = ConcurrentHashMap.newKeySet();
	private final JobRequestScheduler jobRequestScheduler;

	@Override
	public void schedule(TaskDescriptor task) {
		String cron = task.getCron();
		if (cron != null && !cron.isBlank()) {
			jobRequestScheduler.scheduleRecurrently(task.getId(), cron, new TaskJobRequest(task));
			scheduledTaskIds.add(task.getId());
			log.info("✅ JobRunr CRON job enregistré: id={}, cron={}", task.getId(), cron);
		} else {
			log.warn("⏭️ Tâche ignorée dans JobRunrScheduledExecutor (pas de cron défini) : {}", task.getId());
		}
	}

	@Override
	public String strategyKey() {
		return "jobrunr";
	}

	@Override
	public void unscheduleById(String taskId) {
		jobRequestScheduler.deleteRecurringJob(taskId);
		//BackgroundJobRequest.deleteRecurringJob(taskId);
		scheduledTaskIds.remove(taskId);
	}

	@Override
	public void unschedule(TaskDescriptor task) {
		unscheduleById(task.getId());
	}

	@Override
	public Set<String> getScheduledTaskIds() {
		return Collections.unmodifiableSet(scheduledTaskIds);
	}

	@PostConstruct
	private void logActivation() {
		log.debug("✅ Bean JobRunrScheduledExecutor initialisé");
	}

}
