package com.r3edge.task.dispatcher;

/**
 * Interface que chaque handler de tâche doit implémenter.
 * Un handler traite une tâche de type spécifique.
 */
public interface TaskHandler {
    /**
     * Retourne le type de tâche que ce handler peut traiter.
     */
    String getType();

    /**
     * Méthode exécutée lorsqu'une tâche est dispatchée à ce handler.
     */
    void handle(Task task);
}
