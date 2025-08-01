package com.r3edge.tasks.dispatcher.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Spring pour la stratégie de tâches par défaut.
 */
@Configuration
@Slf4j
public class InMemoryExecutorConfig {

    /**
     * Fournit un planificateur de tâches basé sur un scheduler local.
     *
     * @param taskInvokerService service d'invocation de tâches
     * @return implémentation par défaut de {@link IScheduledExecutor}
     */
	@Bean
	public IScheduledExecutor defaultTaskScheduler(TaskInvokerService taskInvokerService) {
		return new InMemoryScheduledExecutor(taskInvokerService); // à créer si besoin
	}

    /**
     * Fournit un exécuteur de tâches basé sur l'invocation directe.
     *
     * @param taskInvokerService service d'invocation de tâches
     * @param config Configuration globale des tâches pour l’executor par défaut.
     * @return implémentation par défaut de {@link IFireAndForgetExecutor}
     */
	@Bean
	public IFireAndForgetExecutor defaultTaskExecutor(TaskInvokerService taskInvokerService, TaskDescriptorsProperties config) {
		return new InMemoryFireAndForgetExecutor(config, taskInvokerService);
	}
}
