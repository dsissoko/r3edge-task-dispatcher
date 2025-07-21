package com.r3edge.tasks.dispatcher;

/**
 * Interface que chaque handler de tâche doit implémenter.
 * Un handler traite une tâche de type spécifique.
 */
public interface TaskHandler {
	
    /**
     * Retourne le type de tâche que ce gestionnaire peut traiter.
     *
     * @return le type de tâche sous forme de chaîne
     */
    String getType();

    /**
     * Traite la tâche spécifiée.
     *
     * @param task la tâche à traiter
     */
    void handle(Task task);

}
