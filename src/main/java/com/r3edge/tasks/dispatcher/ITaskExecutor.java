package com.r3edge.tasks.dispatcher;

/**
 * Interface for executing tasks.
 */
public interface ITaskExecutor {
    /**
     * Executes a given task with a specific handler.
     * @param task The task to execute.
     * @param handler The handler to use for execution.
     */
    void execute(Task task, TaskHandler handler);
    /**
     * Returns the strategy key for this executor.
     * @return The strategy key.
     */
    String strategyKey();
}
