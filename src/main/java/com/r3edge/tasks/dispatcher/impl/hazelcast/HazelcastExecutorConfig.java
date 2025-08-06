package com.r3edge.tasks.dispatcher.impl.hazelcast;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.r3edge.tasks.dispatcher.core.IFireAndForgetExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Spring pour l'exécution distribuée des tâches via Hazelcast.
 * 
 * Déclare un bean IFireAndForgetExecutor utilisant un IScheduledExecutorService Hazelcast.
 * S'active si HazelcastInstance est présent dans le classpath.
 */
@ConditionalOnBean(HazelcastInstance.class)
@Configuration
@Slf4j
public class HazelcastExecutorConfig {

    /**
     * Crée et retourne une instance de {@link IFireAndForgetExecutor} pour Hazelcast.
     *
     * @param hazelcastInstance L’instance Hazelcast du cluster.
     * @param config Configuration globale des tâches.
     * @return Une instance de {@link HazelcastFireAndForgetExecutor}.
     */
    @Bean
    public IFireAndForgetExecutor hazelcastTaskExecutor(
            HazelcastInstance hazelcastInstance,
            TaskDescriptorsProperties config
    ) {
        IScheduledExecutorService scheduledExecutorService = hazelcastInstance.getScheduledExecutorService("r3edge-dispatcher");
        return new HazelcastFireAndForgetExecutor(
                config,
                scheduledExecutorService
        );
    }
}
