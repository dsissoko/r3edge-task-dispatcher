package com.r3edge.tests.tasks;

import java.util.stream.Collectors;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler de test qui affiche un message depuis les métadonnées.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PrintTaskHandler implements TaskHandler {

    @Override
    public String getName() {
        return "print";
    }

    private String extractMeta(TaskDescriptor task) {
        if (task == null || task.getMeta() == null || task.getMeta().isEmpty()) return "n/a";
        return task.getMeta().entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\": \"" + e.getValue() + "\"")
            .collect(Collectors.joining(", ", "{", "}"));
    }

	@Override
	public void execute(TaskDescriptor task) {
		// Au choix : un logger agnostique
		Logger logger = LoggerFactory.getLogger(this.getClass());
		// ou un logger adpaté à la srategy mais qui rend le handler dépendant de l'infra choisie
		logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));
        String message = extractMeta(task);
        logger.info("🔄 Exécution de PrintTaskHandler avec les meta suivantes: {}", message);
	}
}
