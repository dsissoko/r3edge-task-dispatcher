package com.r3edge.tasks.dispatcher;

/**
 * SPI pour planifier des tâches de façon récurrente. Peut être implémenté par
 * une autre lib (JobRunr, Quartz, etc).
 */
public interface ITaskScheduler {

	/**
	 * Planifie l'exécution récurrente d'une tâche selon un cron.
	 *
	 * @param task     la tâche à planifier
	 * @param handler le gestionnaire de tâches
	 */
	void schedule(Task task, TaskHandler handler);
	/**
	 * Retourne la clé de stratégie pour ce planificateur.
	 * @return La clé de stratégie.
	 */
	String strategyKey();
}
