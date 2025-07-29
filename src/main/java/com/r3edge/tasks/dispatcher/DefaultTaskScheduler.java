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
 * Planifie des tâches avec un cron via {@link ThreadPoolTaskScheduler}.
 */
@Slf4j
public class DefaultTaskScheduler implements ITaskScheduler {

	private final TaskScheduler scheduler;
	private final TaskInvokerService taskInvokerService;
	private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();
	private final ITaskExecutionListener listener;

	/**
	 * @param invokerService service chargé d'invoquer la tâche
	 * @param listener listener de cycle de vie des tâches
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

	/** Annule toutes les tâches et arrête le scheduler. */
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
	 * Planifie une tâche à exécuter selon son expression cron.
	 * Si une tâche avec le même identifiant est déjà planifiée, elle sera remplacée.
	 *
	 * @param task    La tâche à planifier. Elle doit avoir un champ {@code cron} non nul.
	 * @param handler Le gestionnaire à invoquer au moment de l’exécution.
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

	/**
	 * Retourne la clé de stratégie associée à ce scheduler.
	 * Cette valeur permet d'identifier dynamiquement cette implémentation.
	 *
	 * @return la clé "default".
	 */
	@Override
	public String strategyKey() {
		return "default";
	}

	/**
	 * Retourne un logger SLF4J standard adapté à la classe cible.
	 *
	 * @param clazz La classe pour laquelle le logger est créé.
	 * @return Un logger SLF4J classique.
	 */
	@Override
	public Logger getSlf4JLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	/**
	 * Annule l'exécution planifiée de la tâche spécifiée, si elle est actuellement planifiée.
	 *
	 * @param task la tâche à désinscrire.
	 */	
	@Override
	public void unschedule(Task task) {
		ScheduledFuture<?> future = scheduled.remove(task.getId());
		cancelFuture(future, task.getId());
	}

	/**
	 * Annule une tâche planifiée et logue l'action effectuée.
	 *
	 * @param future la tâche planifiée à annuler.
	 * @param taskId identifiant de la tâche, pour le logging.
	 */
	private void cancelFuture(ScheduledFuture<?> future, String taskId) {
		if (future != null) {
			future.cancel(true);
			log.info("❌ Tâche unschedulée dans DefaultTaskScheduler : id={}", taskId);
		} else {
			log.debug("ℹ️ Aucune tâche planifiée à unscheduler pour id={}", taskId);
		}
	}

	/**
	 * Annule l'exécution planifiée d'une tâche par son identifiant.
	 * Si aucune tâche n’est trouvée, l’appel est ignoré.
	 *
	 * @param taskId identifiant de la tâche à désinscrire.
	 */
	@Override
	public void unscheduleById(String taskId) {
		ScheduledFuture<?> future = scheduled.remove(taskId);
		if (future != null) {
			future.cancel(true);
		}
	}

	/**
	 * Retourne l’ensemble des identifiants des tâches actuellement planifiées.
	 *
	 * @return un ensemble d’identifiants de tâches.
	 */
	@Override
	public Set<String> getScheduledTaskIds() {
		return scheduled.keySet();
	}

}
