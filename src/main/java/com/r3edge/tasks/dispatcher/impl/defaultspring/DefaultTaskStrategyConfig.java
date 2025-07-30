package com.r3edge.tasks.dispatcher.impl.defaultspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.r3edge.tasks.dispatcher.core.ITaskExecutionListener;
import com.r3edge.tasks.dispatcher.core.ITaskExecutor;
import com.r3edge.tasks.dispatcher.core.ITaskScheduler;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Spring pour la stratégie de tâches par défaut.
 */
@Configuration
//@ConditionalOnProperty(prefix = "r3edge.tasks", name = "strategy", havingValue = "default", matchIfMissing = true)
@Slf4j
public class DefaultTaskStrategyConfig {

    /**
     * Fournit un planificateur de tâches basé sur un scheduler local.
     *
     * @param taskInvokerService service d'invocation de tâches
     * @param listener listener d'exécution des tâches
     * @return implémentation par défaut de {@link ITaskScheduler}
     */
	@Bean
	public ITaskScheduler defaultTaskScheduler(TaskInvokerService taskInvokerService, ITaskExecutionListener listener) {
		return new DefaultTaskScheduler(taskInvokerService, listener); // à créer si besoin
	}

    /**
     * Fournit un exécuteur de tâches basé sur l'invocation directe.
     *
     * @param taskInvokerService service d'invocation de tâches
     * @return implémentation par défaut de {@link ITaskExecutor}
     */
	@Bean
	public ITaskExecutor defaultTaskExecutor(TaskInvokerService taskInvokerService) {
		return new DefaultTaskExecutor(taskInvokerService);
	}
}
