package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

/**
 * Default task executor that simply calls the task handler.
 * This executor does not apply any distributed locking.
 */
@Component
public class DefaultTaskExecutor {

    /**
     * Exécute la tâche donnée en utilisant le handler fourni.
     *
     * @param task    La tâche à exécuter.
     * @param handler Le handler responsable de la logique de la tâche.
     */
    public void execute(Task task, TaskHandler handler) {
        handler.handle(task);
    }
}
