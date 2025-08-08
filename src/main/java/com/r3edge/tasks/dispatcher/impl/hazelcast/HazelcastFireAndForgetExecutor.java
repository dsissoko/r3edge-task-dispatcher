package com.r3edge.tasks.dispatcher.impl.hazelcast;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.r3edge.tasks.dispatcher.core.IFireAndForgetExecutor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskDescriptorsProperties;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Exécuteur distribué Hazelcast pour les tâches fire-and-forget.
 * 
 * Gère la planification, l'annulation et le suivi des tâches envoyées dans le cluster.
 * Utilise un IScheduledExecutorService pour lancer les HazelcastTaskJob.
 */
@RequiredArgsConstructor
@Slf4j
public class HazelcastFireAndForgetExecutor implements IFireAndForgetExecutor {

    private final TaskDescriptorsProperties taskConfiguration;
    private final IScheduledExecutorService scheduledExecutorService;
    private final TaskInvokerService invoker;

    // Map taskId -> ScheduledFuture<?> (références distribuées !)
    private final Map<String, ScheduledFuture<?>> distributedFutures = new ConcurrentHashMap<>();

    @Override
    public void execute(TaskDescriptor task) {
        String taskId = task.getId();
        ScheduledFuture<?> future;
        if (task.getAt() != null) {
            Instant now = Instant.now();
            Instant at = task.getAt();
            long delayMillis = java.time.Duration.between(now, at).toMillis();

            if (delayMillis < 0) {
                log.warn("⚠️ Tâche {} avec date at dépassée : {}", taskId, at);
                if (taskConfiguration.isSkipLateTasks()) {
                    log.warn("⚠️ Tâche {} ignorée car date at dépassée : {}", taskId, at);
                    return;
                }
            }
            future = scheduledExecutorService.schedule(
                    new HazelcastTaskJob(task, invoker),
                    delayMillis,
                    TimeUnit.MILLISECONDS
            );
            log.info("✅ Tâche {} planifiée avec Hazelcast pour exécution différée à {}", taskId, at);
        } else {
        	future = scheduledExecutorService.schedule(new HazelcastTaskJob(task, invoker), 0, TimeUnit.MILLISECONDS);

            log.info("✅ Tâche {} envoyée en Fire & Forget via Hazelcast", taskId);
        }
        distributedFutures.put(taskId, future);
    }

    @Override
    public String strategyKey() {
        return "hazelcast";
    }

    @Override
    public void cancel(TaskDescriptor task) {
        ScheduledFuture<?> future = distributedFutures.remove(task.getId());
        if (future != null) {
            boolean cancelled = future.cancel(false); // La méthode cancel(true) sur un ScheduledFuture Hazelcast ne supporte pas le flag mayInterruptIfRunning = true
            log.info("Cancel Hazelcast [{}] : {}", task.getId(), cancelled ? "OK" : "Déjà terminée ou impossible");
        } else {
            log.warn("Impossible de cancel Hazelcast [{}] : future inconnu", task.getId());
        }
    }

    @Override
    public Set<String> getExecutedTaskIds() {
        return Collections.unmodifiableSet(distributedFutures.keySet());
    }

    /**
     * Vérifie si une tâche avec l'identifiant donné est suivie via future distribué.
     *
     * @param taskId l'identifiant de la tâche
     * @return true si la tâche a un future distribué connu, false sinon
     */
    public boolean hasExecutedTask(String taskId) {
        return distributedFutures.containsKey(taskId);
    }

    @PostConstruct
    private void logActivation() {
        log.debug("✅ Bean HazelcastFireAndForgetExecutor initialisé");
    }
}
