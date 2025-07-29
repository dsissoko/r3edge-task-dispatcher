package com.r3edge.tasks.dispatcher;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation par défaut de {@link ITaskScheduler} utilisant un
 * {@link ThreadPoolTaskScheduler} pour planifier l'exécution des tâches selon
 * une expression cron.
 *
 * <p>
 * Chaque tâche est planifiée avec un {@link CronTrigger} si le champ
 * {@code cron} est défini dans l'objet {@link Task}.
 * </p>
 *
 * <p>
 * Un pool de 2 threads est utilisé pour l'exécution concurrente des tâches
 * planifiées.
 * </p>
 *
 * @see Task
 * @see CronTrigger
 */
@Slf4j
public class DefaultTaskScheduler implements ITaskScheduler {

	private final TaskScheduler scheduler;
	private final TaskInvokerService taskInvokerService;
	private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();
	private final ITaskExecutionListener listener;

	/**
	 * Initialise le planificateur avec un pool de 2 threads et un préfixe de
	 * nommage de threads {@code r3edge-task-}.
	 */
	public DefaultTaskScheduler(TaskInvokerService invokerService, ITaskExecutionListener listener) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(2);
		threadPoolTaskScheduler.setThreadNamePrefix("r3edge-task-");
		threadPoolTaskScheduler.initialize();
		this.scheduler = threadPoolTaskScheduler;
		this.taskInvokerService = invokerService;
		this.listener = listener;
	}

	@PreDestroy
	public void shutdown() {
		log.info("🧹 Nettoyage des tâches planifiées avant arrêt...");
		scheduled.values().forEach(future -> future.cancel(true));
		scheduled.clear();
		if (scheduler instanceof ThreadPoolTaskScheduler threadPool) {
			threadPool.shutdown();
		}
	}

	/**
	 * Planifie une tâche à exécuter selon l'expression cron définie dans le champ
	 * {@code cron} de la tâche. Si aucun cron n'est défini, la méthode ne fait
	 * rien.
	 *
	 * @param task    la tâche à planifier
	 * @param handler le gestionnaire de tâches à déclencher selon le cron
	 */
	@Override
	public void schedule(Task task, TaskHandler handler) {
		String cron = task.getCron();
		if (cron == null || cron.isBlank()) {
			log.warn("⏭️ Tâche id={} ignorée (pas de cron défini)", task.getId());
			return;
		}
		log.info("✅ Planification via DefaultTaskScheduler : id={}, cron={}", task.getId(), cron);
		ScheduledFuture<?> future = scheduler.schedule(taskInvokerService.toLambda(task).toRunnable(listener, task),
				new CronTrigger(cron));
		if (future != null)
			log.info("La tâche id={} a été planifiée avec le motif cron={}", task.getId(), cron);
		ScheduledFuture<?> previousFuture = scheduled.put(task.getId(), future);
		if (previousFuture != null) {
			log.warn("La tâche {} a été remplacée par une nouvelle version", task.getId());
			cancelFuture(previousFuture, task.getId());
		}
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
	public void unschedule(Task task) {
		ScheduledFuture<?> future = scheduled.remove(task.getId());
		cancelFuture(future, task.getId());
	}

	private void cancelFuture(ScheduledFuture<?> future, String taskId) {
		if (future != null) {
			future.cancel(true);
			log.info("❌ Tâche unschedulée dans DefaultTaskScheduler : id={}", taskId);
		} else {
			log.debug("ℹ️ Aucune tâche planifiée à unscheduler pour id={}", taskId);
		}
	}

	@Override
	public void unscheduleById(String taskId) {
		ScheduledFuture<?> future = scheduled.remove(taskId);
		if (future != null) {
			future.cancel(true);
		}
	}

	@Override
	public Set<String> getScheduledTaskIds() {
		return scheduled.keySet();
	}

}
