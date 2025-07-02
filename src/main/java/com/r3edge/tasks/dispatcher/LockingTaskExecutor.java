package com.r3edge.tasks.dispatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;

/**
 * Executor that applies distributed locking using ShedLock if a LockProvider is available
 * and the task is configured for distributed locking.
 */
@Slf4j
//@Component
//@Conditional(LockingTaskExecutor.LockProviderCondition.class)
public class LockingTaskExecutor {

    private final Optional<LockProvider> lockProvider;
    private final DefaultTaskExecutor defaultTaskExecutor;

    /**
     * Construit une nouvelle instance de LockingTaskExecutor.
     *
     * @param lockProvider        Le fournisseur de verrous distribués (optionnel).
     * @param defaultTaskExecutor L'exécuteur de tâches par défaut.
     */
    public LockingTaskExecutor(Optional<LockProvider> lockProvider, DefaultTaskExecutor defaultTaskExecutor) {
        this.lockProvider = lockProvider;
        this.defaultTaskExecutor = defaultTaskExecutor;
    }

    /**
     * Exécute une tâche en appliquant un verrou distribué si nécessaire.
     * Si la tâche est configurée pour utiliser un verrou distribué et qu'un LockProvider est disponible,
     * une tentative de verrouillage est effectuée avant l'exécution.
     * Sinon, la tâche est exécutée directement.
     *
     * @param task    La tâche à exécuter.
     * @param handler Le handler responsable de la logique de la tâche.
     */
    public void execute(Task task, TaskHandler handler) {
        if (task.isDistributedLock() && lockProvider.isPresent()) {
            log.info("Attempting to acquire distributed lock for task {}", task.getId());
            // Define lock configuration. Lock name should be unique per task.
            // Lock at most for 30 seconds, lock at least for 5 seconds (adjust as needed)
            LockConfiguration lockConfig = new LockConfiguration(
                Instant.now(),
                task.getId(),
                Duration.ofSeconds(30), // lockAtMostFor
                Duration.ofSeconds(5)  // lockAtLeastFor
            );

            Optional<net.javacrumbs.shedlock.core.SimpleLock> lock = lockProvider.get().lock(lockConfig);

            if (lock.isPresent()) {
                try {
                    log.info("Distributed lock acquired for task {}", task.getId());
                    defaultTaskExecutor.execute(task, handler);
                } finally {
                    lock.get().unlock();
                    log.info("Distributed lock released for task {}", task.getId());
                }
            } else {
                log.info("Could not acquire distributed lock for task {}. Skipping execution.", task.getId());
            }
        } else {
            // No distributed lock needed for this task, or no LockProvider configured.
            // Execute directly via the default executor.
            defaultTaskExecutor.execute(task, handler);
        }
    }

    /**
     * Condition to check if a LockProvider bean is available in the Spring context.
     * This ensures LockingTaskExecutor is only created when ShedLock is configured by the user.
     */
    static class LockProviderCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return context.getBeanFactory() != null && context.getBeanFactory().getBeansOfType(LockProvider.class).size() > 0;
        }
    }
}
