package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

/**
 * Default task executor that simply calls the task handler.
 * This executor does not apply any distributed locking.
 */
@Component
public class DefaultTaskExecutor {

    public void execute(Task task, TaskHandler handler) {
        handler.handle(task);
    }
}
