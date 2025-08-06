package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.jobs.lambdas.JobRequest;
import org.jobrunr.jobs.lambdas.JobRequestHandler;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente une requête de tâche pour JobRunr, encapsulant la tâche à exécuter
 * ainsi que le nom de la classe de logger associée.
 */
@SuppressWarnings("serial")
@NoArgsConstructor
@AllArgsConstructor
public class TaskJobRequest implements JobRequest {

    /**
     * Descriptor de Tâche encapsulé dans cette requête.
     */
	@Getter @Setter
	private TaskDescriptor taskDescriptor;

	@Override
	public Class<? extends JobRequestHandler<TaskJobRequest>> getJobRequestHandler() {
		return TaskJobRequestHandler.class;
	}
}
