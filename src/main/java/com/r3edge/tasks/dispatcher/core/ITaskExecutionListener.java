package com.r3edge.tasks.dispatcher.core;

import org.slf4j.Logger;

/**
 * Écouteur du cycle de vie d'exécution d'une tâche.
 * Permet d’intercepter les événements liés à l'exécution : démarrage, succès, échec.
 */
public interface ITaskExecutionListener {

    /**
     * Appelé juste avant l'exécution de la tâche.
     *
     * @param task la tâche en cours de démarrage
     * @param logger le logger contextuel pour cette exécution
     */
    void onStart(Task task, Logger logger);

    /**
     * Appelé après une exécution réussie.
     *
     * @param task la tâche exécutée avec succès
     * @param logger le logger contextuel pour cette exécution
     */
    void onSuccess(Task task, Logger logger);

    /**
     * Appelé lorsqu'une exception est levée pendant l'exécution.
     *
     * @param task la tâche ayant échoué
     * @param exception l'exception levée
     * @param logger le logger contextuel pour cette exécution
     */
    void onFailure(Task task, Throwable exception, Logger logger);
}
