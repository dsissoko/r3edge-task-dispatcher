package com.r3edge.tasks.dispatcher;

import org.jobrunr.scheduling.JobScheduler;

import lombok.RequiredArgsConstructor;

/**
 * Implémentation de {@link ITaskExecutor} utilisant JobRunr pour l'exécution des tâches.
 */
@RequiredArgsConstructor
public class JobRunrTaskExecutor implements ITaskExecutor {

    private final JobScheduler jobScheduler;
    private final TaskInvokerService taskInvokerService;

    @Override
    public void execute(Task task, TaskHandler handler) {
        if (task.getAt() != null) {
            jobScheduler.schedule(task.getAt(), () -> taskInvokerService.invoke(task));
        } else {
            jobScheduler.enqueue(() -> taskInvokerService.invoke(task));
        }
    }
    
	@Override
	public String strategyKey() {
		return "jobrunr";
	}
}
