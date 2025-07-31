package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.r3edge.tasks.dispatcher.core.ITaskExecutor;
import com.r3edge.tasks.dispatcher.core.ITaskScheduler;
import com.r3edge.tasks.dispatcher.core.TaskConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Spring pour la stratégie de tâches basée sur JobRunr.
 */
@Configuration
@Slf4j
public class JobRunrTaskStrategyConfig {

    /**
     * Crée et retourne une instance de {@link ITaskScheduler} pour JobRunr.
     *
     * @param jobScheduler Le planificateur de tâches JobRunr.
     * @param storageProvider Le fournisseur de stockage utilisé par JobRunr.
     * @return Une instance de {@link JobRunrTaskScheduler}.
     */
	@Bean
	public ITaskScheduler jobRunrTaskScheduler(JobScheduler jobScheduler, StorageProvider storageProvider) {
		return new JobRunrTaskScheduler();
	}

	/**
	 * Crée et retourne une instance de {@link ITaskExecutor} pour JobRunr.
	 * 
	 * @param config Configuration globale des tâches utilisée pour l’executor JobRunr.
	 * @return Une instance de {@link JobRunrTaskExecutor}.
	 */
	@Bean
	public ITaskExecutor jobRunrTaskExecutor(TaskConfiguration config) {
		return new JobRunrTaskExecutor(config);
	}
}
