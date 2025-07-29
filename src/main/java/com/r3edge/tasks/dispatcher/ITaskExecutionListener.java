package com.r3edge.tasks.dispatcher;

public interface ITaskExecutionListener {
    void onStart(Task task);
    void onSuccess(Task task);
    void onFailure(Task task, Throwable exception);
}

