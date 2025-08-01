package com.r3edge.tasks.dispatcher.core;

import java.util.Set;

/**
 * SPI pour planifier des tâches de façon récurrente. Peut être implémenté par
 * une autre lib (JobRunr, Quartz, etc).
 */
public interface IScheduledExecutor {

	/**
	 * Planifie l'exécution récurrente d'une tâche selon un cron.
	 *
	 * @param task descriptor de la tâche à planifier.
	 */
	void schedule(TaskDescriptor task);

	/**
	 * Retourne la clé de stratégie pour ce planificateur.
	 * 
	 * @return La clé de stratégie.
	 */
	String strategyKey();

	/**
	 * Déplanifie l'exécution récurrente d'une tâche cron.
	 *
	 * @param task descriptor de la tâche à déplanifier.
	 */
	void unschedule(TaskDescriptor task);

	/**
	 * Déplanifie l'exécution récurrente d'une tâche cron.
	 *
	 * @param taskId de la tâche à déplanifier.
	 */
	void unscheduleById(String taskId);

	/**
	 * Retourne l'ensemble des identifiants des tâches actuellement planifiées.
	 *
	 * @return ensemble d'identifiants de tâches
	 */
	Set<String> getScheduledTaskIds();
}
