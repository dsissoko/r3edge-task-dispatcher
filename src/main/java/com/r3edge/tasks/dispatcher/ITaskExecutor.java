package com.r3edge.tasks.dispatcher;

import java.util.Set;

import org.slf4j.Logger;

/**
 * SPI pour exécuter des tâches ponctuelles ou planifiées.
 * Chaque implémentation correspond à une stratégie d'exécution.
 */
public interface ITaskExecutor {
    /**
     * Executes a given task with a specific handler.
     * @param task The task to execute.
     * @param handler The handler to use for execution.
     */
    void execute(Task task, TaskHandler handler);
    /**
     * Returns the strategy key for this executor.
     * @return The strategy key.
     */
    String strategyKey();
    
    /**
     * Crée un logger compatible avec l'implémentation sous jacente
     *
     * @param clazz La classe cible pour laquelle le logger est créé.
     * @return Un logger adapté
     */	
	Logger getSlf4JLogger(Class<?> clazz);
	
    /**
     * Annule l'exécution d'une tâche cron ponctuelle.
     *
     * @param task  la tâche à annuler.
     */	
	void cancel(Task task);
	
    /**
     * Retourne les identifiants des tâches exécutées par cette stratégie.
     *
     * @return ensemble des identifiants de tâches exécutées
     */
	Set<String> getExecutedTaskIds();
}
