package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.scheduling.JobRequestScheduler;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.r3edge.tasks.dispatcher.core.IFireAndForgetExecutor;
import com.r3edge.tasks.dispatcher.core.IScheduledExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Spring pour la stratégie de tâches basée sur JobRunr.
 */
@Configuration
@Slf4j
public class JobRunrExecutorConfig {

    /**
     * Crée et retourne une instance de {@link IScheduledExecutor} pour JobRunr.
     *
     * @param jobScheduler Le planificateur de tâches JobRunr.
     * @param taskInvokerService service d'invocation des méthodes des jobs
     * @param storageProvider Le fournisseur de stockage utilisé par JobRunr.
     * @param jobRequestScheduler Le planificateur de tâches JobRunr.
     * @return Une instance de {@link JobRunrScheduledExecutor}.
     */
	@Bean
	public IScheduledExecutor jobRunrTaskScheduler(JobScheduler jobScheduler, TaskInvokerService taskInvokerService, StorageProvider storageProvider, JobRequestScheduler jobRequestScheduler) {
		return new JobRunrScheduledExecutor(jobRequestScheduler);
	}

	/**
	 * Crée et retourne une instance de {@link IFireAndForgetExecutor} pour JobRunr.
	 * 
	 * @param config Configuration globale des tâches utilisée pour l’executor JobRunr.
	 * @param jobRequestScheduler Le planificateur de tâches JobRunr.
	 * @return Une instance de {@link JobRunrFireAndForgetExecutor}.
	 */
	@Bean
	public IFireAndForgetExecutor jobRunrTaskExecutor(TaskDescriptorsProperties config, JobRequestScheduler jobRequestScheduler) {
		return new JobRunrFireAndForgetExecutor(config, jobRequestScheduler);
	}
}
