package com.r3edge.tasks.autoconfig;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.r3edge.tasks.dispatcher.DefaultTaskExecutor;
import com.r3edge.tasks.dispatcher.LockingTaskExecutor;

import net.javacrumbs.shedlock.core.LockProvider;

/**
 * Configuration conditionnelle du LockingTaskExecutor si un LockProvider est présent.
 */
@Configuration
@ConditionalOnBean(LockProvider.class)
public class LockingTaskExecutorAutoConfiguration {

    /**
     * Crée un bean LockingTaskExecutor si un LockProvider est disponible dans le contexte Spring.
     *
     * @param lockProvider        Le fournisseur de verrous distribués (injecté par Spring).
     * @param defaultTaskExecutor L'exécuteur de tâches par défaut.
     * @return Une nouvelle instance de LockingTaskExecutor.
     */
    @Bean
    public LockingTaskExecutor lockingTaskExecutor(Optional<LockProvider> lockProvider,
                                                   DefaultTaskExecutor defaultTaskExecutor) {
        return new LockingTaskExecutor(lockProvider, defaultTaskExecutor);
    }
}
