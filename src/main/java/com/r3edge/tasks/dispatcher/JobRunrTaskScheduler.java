package com.r3edge.tasks.dispatcher;

import org.jobrunr.scheduling.JobScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link ITaskScheduler} utilisant JobRunr pour la planification des tâches.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrTaskScheduler implements ITaskScheduler {

	private final JobScheduler scheduler;

	@Override
	public void schedule(Task task, TaskHandler handler) {
		if (task.getCron() != null) {
			String jobId = scheduler.scheduleRecurrently(task.getId(), task.getCron(), () -> handler.handle(task));
			log.info("✅ JobRunr CRON job enregistré: id={}, cron={}", task.getId(), task.getCron());
		} else {
			log.warn("⏭️ Tâche ignorée dans JobRunrTaskScheduler (pas de cron défini) : {}", task.getId());
		}
	}
	
	@Override
	public String strategyKey() {
		return "jobrunr";
	}
}
