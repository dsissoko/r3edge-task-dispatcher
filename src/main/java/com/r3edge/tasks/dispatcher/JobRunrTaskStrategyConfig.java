package com.r3edge.tasks.dispatcher;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration pour la stratégie de tâches JobRunr.
 */
@Configuration
@ConditionalOnProperty(prefix = "r3edge.tasks", name = "strategy", havingValue = "jobrunr")
@ConditionalOnClass(JobScheduler.class) //pour éviter toute erreur en cas d'absence des dépendances jobrunr
@Import(org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration.class)
@Slf4j
public class JobRunrTaskStrategyConfig {

	/**
	 * Crée et retourne une instance de {@link ITaskScheduler} pour JobRunr.
	 * @param jobScheduler Le planificateur de tâches JobRunr.
	 * @return Une instance de {@link JobRunrTaskScheduler}.
	 */
	@Bean
	public ITaskScheduler jobRunrTaskScheduler(JobScheduler jobScheduler) {
		return new JobRunrTaskScheduler(jobScheduler);
	}

	/**
	 * Crée et retourne une instance de {@link ITaskExecutor} pour JobRunr.
	 * @param jobScheduler Le planificateur de tâches JobRunr.
	 * @param taskInvokerService Le service d'invocation de tâches.
	 * @return Une instance de {@link JobRunrTaskExecutor}.
	 */
	@Bean
	public ITaskExecutor jobRunrTaskExecutor(JobScheduler jobScheduler, TaskInvokerService taskInvokerService) {
		return new JobRunrTaskExecutor(jobScheduler, taskInvokerService);
	}
}
