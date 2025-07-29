package com.r3edge.tasks.dispatcher;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jobrunr.jobs.lambdas.JobLambda;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de {@link ITaskExecutor} utilisant JobRunr pour l'exécution des tâches.
 */
@RequiredArgsConstructor
@Slf4j
public class JobRunrTaskExecutor implements ITaskExecutor {

    private final JobScheduler jobScheduler;
    private final TaskInvokerService taskInvokerService;
    private final Set<String> executedTaskIds = ConcurrentHashMap.newKeySet();

    @Override
    public void execute(Task task, TaskHandler handler) {
    	UUID jobId = JobRunrUtils.getJobId(task);
        if (task.getAt() != null) {
        	// TODO passer par taskInvokerService.toLambda(task)
            jobScheduler.schedule(jobId, task.getAt(), (JobLambda)taskInvokerService.toLambda(task));
        } else {
        	// TODO passer par taskInvokerService.toLambda(task)
            jobScheduler.enqueue(jobId, (JobLambda)taskInvokerService.toLambda(task));
        }
        executedTaskIds.add(task.getId());
    }
    
	@Override
	public String strategyKey() {
		return "jobrunr";
	}
	
	@Override
	/**
	 * Retourne un logger SLF4J redirigé vers le dashboard JobRunr.
	 *
	 * @param clazz La classe pour laquelle le logger est créé.
	 * @return Un logger compatible avec le dashboard JobRunr.
	 */
	public Logger getSlf4JLogger(Class<?> clazz) {
	    return JobRunrUtils.jobRunrLogger(clazz);
	}

	@Override
	public void cancel(Task task) {
	    try {
	        jobScheduler.delete(JobRunrUtils.getJobId(task));
	        log.info("Task [{}] annulée", task.getId());
	    } catch (Exception e) {
	        log.warn("Echec d'annualtion de la tâche [{}] : {}", task.getId(), e.getMessage());
	    }
	}

    @Override
    public Set<String> getExecutedTaskIds() {
        return Collections.unmodifiableSet(executedTaskIds);
    }

}
