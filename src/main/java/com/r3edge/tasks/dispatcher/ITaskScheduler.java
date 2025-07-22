package com.r3edge.tasks.dispatcher;

/**
 * SPI pour planifier des tâches de façon récurrente.
 * Peut être implémenté par une autre lib (JobRunr, Quartz, etc).
 */
public interface ITaskScheduler {

    /**
     * Planifie l'exécution récurrente d'une tâche selon un cron.
     *
     * @param task la tâche à planifier
     * @param runnable méthode à planifier
     */
    void schedule(Task task, Runnable runnable);
}
