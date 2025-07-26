package com.r3edge.tasks.dispatcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(prefix = "r3edge.tasks", name = "strategy", havingValue = "default", matchIfMissing = true)
@Slf4j
public class DefaultTaskStrategyConfig {

	@Bean
	public ITaskScheduler defaultTaskScheduler() {
		return new DefaultTaskScheduler(); // à créer si besoin
	}

	@Bean
	public ITaskExecutor defaultTaskExecutor() {
		return new DefaultTaskExecutor(); // tu l’as déjà, à modifier pour implémenter ITaskExecutor
	}
}
