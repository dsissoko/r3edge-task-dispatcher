package com.r3edge.tasks.dispatcher;

import java.util.Set;

import org.slf4j.Logger;

/**
 * Interface for executing tasks.
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
	Logger getSlf4JLogger(Class<?> target);
	
    /**
     * Annule l'exécution d'une tâche cron ponctuelle.
     *
     * @param task  la tâche à annuler.
     */	
	void cancel(Task task);
	
	Set<String> getExecutedTaskIds();
}
