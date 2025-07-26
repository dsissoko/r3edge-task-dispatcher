package com.r3edge.tasks.dispatcher;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation par défaut de {@link ITaskScheduler} utilisant un {@link ThreadPoolTaskScheduler}
 * pour planifier l'exécution des tâches selon une expression cron.
 *
 * <p>Chaque tâche est planifiée avec un {@link CronTrigger} si le champ {@code cron}
 * est défini dans l'objet {@link Task}.</p>
 *
 * <p>Un pool de 2 threads est utilisé pour l'exécution concurrente des tâches planifiées.</p>
 *
 * @see Task
 * @see CronTrigger
 */
@Slf4j
public class DefaultTaskScheduler implements ITaskScheduler {
	
    private final TaskScheduler scheduler;
    
    /**
     * Initialise le planificateur avec un pool de 2 threads et un préfixe
     * de nommage de threads {@code r3edge-task-}.
     */    
    public DefaultTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("r3edge-task-");
        threadPoolTaskScheduler.initialize();
        this.scheduler = threadPoolTaskScheduler;
    }

    /**
     * Planifie une tâche à exécuter selon l'expression cron définie dans le champ {@code cron} de la tâche.
     * Si aucun cron n'est défini, la méthode ne fait rien.
     *
     * @param task     la tâche à planifier
     * @param runnable le bloc d'exécution à déclencher selon le cron
     */
    @Override
    public void schedule(Task task, TaskHandler handler) {
        String cron = task.getCron();
        if (cron == null || cron.isBlank()) {
            log.warn("⏭️ Tâche ignorée (pas de cron défini) : {}", task.getId());
            return;
        }
        log.info("✅ Planification via DefaultTaskScheduler : id={}, cron={}", task.getId(), cron);
        scheduler.schedule(() -> handler.handle(task), new CronTrigger(cron));
    }
    
	@Override
	public String strategyKey() {
		return "default";
	}
}
