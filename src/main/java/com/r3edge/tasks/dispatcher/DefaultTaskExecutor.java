package com.r3edge.tasks.dispatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * Default task executor that simply calls the task handler.
 * This executor does not apply any distributed locking.
 */
@Slf4j
public class DefaultTaskExecutor implements ITaskExecutor {

    /**
     * Exécute la tâche donnée en utilisant le handler fourni.
     *
     * @param task    La tâche à exécuter.
     * @param handler Le handler responsable de la logique de la tâche.
     */
	@Override
    public void execute(Task task, TaskHandler handler) {
		log.info("Lancement de la tâche {}", task);
        handler.handle(task);
    }

	@Override
	public String strategyKey() {
		return "default";
	}
}
