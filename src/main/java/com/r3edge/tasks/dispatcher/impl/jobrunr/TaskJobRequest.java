package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.lambdas.JobRequest;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.r3edge.tasks.dispatcher.core.Task;

import lombok.RequiredArgsConstructor;

/**
 * Représente une requête de tâche pour JobRunr, encapsulant la tâche à exécuter
 * ainsi que le nom de la classe de logger associée.
 */
@SuppressWarnings("serial")
@RequiredArgsConstructor
public class TaskJobRequest implements JobRequest {

    /**
     * Tâche encapsulée dans cette requête.
     */
	private final Task task;
    /**
     * Nom de la classe utilisée pour instancier le logger.
     */
	private final String loggerClassName;

	@Override
	public Class<? extends JobRequestHandler<TaskJobRequest>> getJobRequestHandler() {
		return TaskJobRequestHandler.class;
	}

    /**
     * Retourne la tâche à exécuter.
     *
     * @return la tâche {@link Task}
     */
	public Task getTask() {
		return task;
	}
	
    /**
     * Retourne un logger compatible avec le tableau de bord JobRunr,
     * basé sur le nom de la classe fourni.
     *
     * @return le logger {@link Logger}
     */
    public Logger getLogger() {
        return new JobRunrDashboardLogger(LoggerFactory.getLogger(loggerClassName));
    }
}
