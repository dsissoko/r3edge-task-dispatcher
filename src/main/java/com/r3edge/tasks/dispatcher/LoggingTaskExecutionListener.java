package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingTaskExecutionListener implements ITaskExecutionListener {

    @Override
    public void onStart(Task task) {
        log.info("▶️ Tâche {} démarrée", task.getId());
    }

    @Override
    public void onSuccess(Task task) {
        log.info("✅ Tâche {} terminée avec succès", task.getId());
    }

    @Override
    public void onFailure(Task task, Throwable exception) {
        log.error("❌ Tâche {} en erreur : {}", task.getId(), exception.toString(), exception);
    }
}
