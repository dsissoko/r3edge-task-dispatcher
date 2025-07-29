package com.r3edge.tasks.dispatcher;

/**
 * Écouteur du cycle de vie d'exécution d'une tâche.
 * Permet d’intercepter les événements liés à l'exécution : démarrage, succès, échec.
 */
public interface ITaskExecutionListener {

    /**
     * Appelé juste avant l'exécution de la tâche.
     *
     * @param task la tâche en cours de démarrage
     */
    void onStart(Task task);

    /**
     * Appelé après une exécution réussie.
     *
     * @param task la tâche exécutée avec succès
     */
    void onSuccess(Task task);

    /**
     * Appelé lorsqu'une exception est levée pendant l'exécution.
     *
     * @param task la tâche ayant échoué
     * @param exception l'exception levée
     */
    void onFailure(Task task, Throwable exception);
}
