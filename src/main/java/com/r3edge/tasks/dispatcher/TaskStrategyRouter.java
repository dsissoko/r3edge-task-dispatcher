package com.r3edge.tasks.dispatcher;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Route les tâches vers l'exécuteur ou le planificateur approprié en fonction de la stratégie définie dans la tâche.
 */
@Component
public class TaskStrategyRouter {

    private final Map<String, ITaskExecutor> executors;
    private final Map<String, ITaskScheduler> schedulers;

    /**
     * Construit un nouveau routeur de stratégie de tâches.
     * @param executorBeans La liste des exécuteurs de tâches disponibles.
     * @param schedulerBeans La liste des planificateurs de tâches disponibles.
     */
    public TaskStrategyRouter(
        List<ITaskExecutor> executorBeans,
        List<ITaskScheduler> schedulerBeans
    ) {
        this.executors = executorBeans.stream()
            .collect(Collectors.toMap(ITaskExecutor::strategyKey, Function.identity()));

        this.schedulers = schedulerBeans.stream()
            .collect(Collectors.toMap(ITaskScheduler::strategyKey, Function.identity()));
    }

    /**
     * Résout l'exécuteur de tâches approprié pour une tâche donnée.
     * @param task La tâche pour laquelle résoudre l'exécuteur.
     * @return L'exécuteur de tâches résolu.
     */
    public ITaskExecutor resolveExecutor(Task task) {
        return executors.getOrDefault(task.getStrategy(), executors.get("default"));
    }

    /**
     * Résout le planificateur de tâches approprié pour une tâche donnée.
     * @param task La tâche pour laquelle résoudre le planificateur.
     * @return Le planificateur de tâches résolu.
     */
    public ITaskScheduler resolveScheduler(Task task) {
        return schedulers.getOrDefault(task.getStrategy(), schedulers.get("default"));
    }
}
