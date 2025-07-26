package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskInvokerService {

    private final TaskHandlerRegistry registry;

    public void invoke(Task task) {
        TaskHandler handler = registry.getHandler(task.getType());
        if (handler == null) {
            throw new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType());
        }

        handler.handle(task);
    }
}
