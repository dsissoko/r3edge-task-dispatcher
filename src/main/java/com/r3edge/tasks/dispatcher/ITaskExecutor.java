package com.r3edge.tasks.dispatcher;

public interface ITaskExecutor {
    void execute(Task task, TaskHandler handler);
    String strategyKey();
}
