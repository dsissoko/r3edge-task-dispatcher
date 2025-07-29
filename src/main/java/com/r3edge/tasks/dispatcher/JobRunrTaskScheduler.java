package com.r3edge.tasks.dispatcher;

import java.util.UUID;

import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link ITaskScheduler} utilisant JobRunr pour la
 * planification des tâches récurrentes.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrTaskScheduler implements ITaskScheduler {

	private final JobScheduler scheduler;
	private final TaskInvokerService taskInvokerService;

	@Override
	public void schedule(Task task, TaskHandler handler) {
		String cron = task.getCron();
		if (cron != null && !cron.isBlank()) {
			var lambda = taskInvokerService.toLambda(task);
			scheduler.scheduleRecurrently(task.getId(), cron, lambda::run);
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
	/**
	 * Retourne un logger SLF4J redirigé vers le dashboard JobRunr.
	 *
	 * @param clazz La classe pour laquelle le logger est créé.
	 * @return Un logger compatible avec le dashboard JobRunr.
	 */
	public Logger getSlf4JLogger(Class<?> clazz) {
		return JobRunrUtils.jobRunrLogger(clazz);
	}
	
	@Override
	public void unscheduleById(String taskId) {
		scheduler.delete(JobRunrUtils.getJobId(taskId), "Déplanification demandée");
	}	

	@Override
	public void unschedule(Task task) {
		scheduler.delete(JobRunrUtils.getJobId(task), "Déplanification demandée");
	}

}
