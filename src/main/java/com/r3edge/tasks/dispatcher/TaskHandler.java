package com.r3edge.tasks.dispatcher;

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
    
    /**
     * Méthode appelée lorsqu'une tâche existante est rechargée ou supprimée.
     * @param previous L'ancienne définition de la tâche.
     * @param updated La nouvelle définition, ou null si la tâche a été supprimée.
     * @param removed true si la tâche a été supprimée.
     */
    void onTaskReload(Task previous, Task updated, boolean removed);
}
