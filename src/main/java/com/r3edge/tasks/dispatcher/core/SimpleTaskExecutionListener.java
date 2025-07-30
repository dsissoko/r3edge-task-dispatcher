package com.r3edge.tasks.dispatcher.core;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Implémentation de {@link ITaskExecutionListener} qui logue
 * les événements du cycle de vie d'une tâche (démarrage, succès, échec).
 */
@Component
public class SimpleTaskExecutionListener implements ITaskExecutionListener {

    @Override
    public void onStart(Task task, Logger logger) {
        logger.info("▶️ Tâche {} démarrée", task.getId());
    }

    @Override
    public void onSuccess(Task task, Logger logger) {
        logger.info("✅ Tâche {} terminée avec succès", task.getId());
    }

    @Override
    public void onFailure(Task task, Throwable exception, Logger logger) {
        logger.error("❌ Tâche {} en erreur : {}", task.getId(), exception.toString(), exception);
    }
}
