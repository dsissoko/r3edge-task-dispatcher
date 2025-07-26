package com.r3edge.tasks.dispatcher;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(prefix = "r3edge.tasks", name = "strategy", havingValue = "jobrunr")
@ConditionalOnClass(JobScheduler.class) //pour éviter toute erreur en cas d'absence des dépendances jobrunr
@Import(org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration.class)
@Slf4j
public class JobRunrTaskStrategyConfig {

	@Bean
	public ITaskScheduler jobRunrTaskScheduler(JobScheduler jobScheduler) {
		return new JobRunrTaskScheduler(jobScheduler);
	}

	@Bean
	public ITaskExecutor jobRunrTaskExecutor(JobScheduler jobScheduler, TaskInvokerService taskInvokerService) {
		return new JobRunrTaskExecutor(jobScheduler, taskInvokerService);
	}
}
