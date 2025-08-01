package com.r3edge.tasks.dispatcher.core;

import java.util.Set;

/**
 * SPI pour exécuter des tâches ponctuelles ou planifiées. Chaque implémentation
 * correspond à une stratégie d'exécution.
 */
public interface IFireAndForgetExecutor {
	/**
	 * Executes a given task with a specific handler.
	 * 
	 * @param task descriptor de la tâche à exécuter.
	 */
	void execute(TaskDescriptor task);

	/**
	 * Returns the strategy key for this executor.
	 * 
	 * @return The strategy key.
	 */
	String strategyKey();

	/**
	 * Annule l'exécution d'une tâche cron ponctuelle.
	 *
	 * @param task descriptor de la tâche à annuler.
	 */
	void cancel(TaskDescriptor task);

	/**
	 * Retourne les identifiants des tâches exécutées par cette stratégie.
	 *
	 * @return ensemble des identifiants de tâches exécutées
	 */
	Set<String> getExecutedTaskIds();
}
