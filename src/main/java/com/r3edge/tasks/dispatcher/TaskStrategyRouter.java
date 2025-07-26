package com.r3edge.tasks.dispatcher;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TaskStrategyRouter {

    private final Map<String, ITaskExecutor> executors;
    private final Map<String, ITaskScheduler> schedulers;

    public TaskStrategyRouter(
        List<ITaskExecutor> executorBeans,
        List<ITaskScheduler> schedulerBeans
    ) {
        this.executors = executorBeans.stream()
            .collect(Collectors.toMap(ITaskExecutor::strategyKey, Function.identity()));

        this.schedulers = schedulerBeans.stream()
            .collect(Collectors.toMap(ITaskScheduler::strategyKey, Function.identity()));
    }

    public ITaskExecutor resolveExecutor(Task task) {
        return executors.getOrDefault(task.getStrategy(), executors.get("default"));
    }

    public ITaskScheduler resolveScheduler(Task task) {
        return schedulers.getOrDefault(task.getStrategy(), schedulers.get("default"));
    }
}
