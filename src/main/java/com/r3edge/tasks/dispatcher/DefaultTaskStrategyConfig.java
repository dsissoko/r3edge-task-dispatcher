package com.r3edge.tasks.dispatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration par défaut pour la stratégie de tâches.
 */
@Configuration
//@ConditionalOnProperty(prefix = "r3edge.tasks", name = "strategy", havingValue = "default", matchIfMissing = true)
@Slf4j
public class DefaultTaskStrategyConfig {

	/**
	 * Crée et retourne une instance de {@link ITaskScheduler} par défaut.
	 * @return Une instance de {@link DefaultTaskScheduler}.
	 */
	@Bean
	public ITaskScheduler defaultTaskScheduler(TaskInvokerService taskInvokerService, ITaskExecutionListener listener) {
		return new DefaultTaskScheduler(taskInvokerService, listener); // à créer si besoin
	}

	/**
	 * Crée et retourne une instance de {@link ITaskExecutor} par défaut.
	 * @return Une instance de {@link DefaultTaskExecutor}.
	 */
	@Bean
	public ITaskExecutor defaultTaskExecutor(TaskInvokerService taskInvokerService) {
		return new DefaultTaskExecutor(taskInvokerService);
	}
}
