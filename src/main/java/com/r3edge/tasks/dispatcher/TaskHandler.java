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

    
    /**
     * Fournit une lambda exécutable représentant la logique métier différée de la tâche.
     * Optionnel, mais requis pour une intégration propre avec des systèmes comme JobRunr.
     *
     * @param task La tâche à encapsuler.
     * @return une TaskLambda ou null si non supporté.
     */
    default TaskLambda toLambda(Task task) {
        return () -> handle(task);
    }
}
