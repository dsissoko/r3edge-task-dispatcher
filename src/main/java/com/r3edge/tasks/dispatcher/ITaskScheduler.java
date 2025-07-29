package com.r3edge.tasks.dispatcher;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;

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
	
    /**
     * Crée un logger compatible avec l'implémentation sous jacente
     *
     * @param clazz La classe cible pour laquelle le logger est créé.
     * @return Un logger adapté
     */	
	Logger getSlf4JLogger(Class<?> clazz);
	
	
    /**
     * Déplanifie l'exécution récurrente d'une tâche cron.
     *
     * @param task  la tâche à déplanifier.
     */	
	void unschedule(Task task);
	
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
	default Set<String> getScheduledTaskIds() {
	    return Collections.emptySet();
	}
}
