package com.r3edge.tasks.dispatcher;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * À utiliser uniquement dans les classes liées à JobRunr.
 */
@Component
public class TasksUtils {

	/**
	 * Génére un UUID à partir de l'id fonctionnel de la tâche.
	 *
	 * @param task la tâche à déplanifier.
	 * @return UUID id unique de la tâche
	 */
	public static UUID getJobId(Task task) {
		return getJobId(task.getId());
	}

	/**
	 * Génére un UUID à partir de l'id fonctionnel de la tâche.
	 *
	 * @param jobId fonctionnel la tâche à déplanifier.
	 * @return UUID id unique de la tâche
	 */
	public static UUID getJobId(String jobId) {
		return UUID.nameUUIDFromBytes(("exec-" + jobId).getBytes(StandardCharsets.UTF_8));
	}
}
