package com.r3edge.tasks.dispatcher;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler de test qui affiche un message depuis les métadonnées.
 */
@Slf4j
@Component
public class PrintTaskHandler implements TaskHandler {

    @Override
    public String getType() {
        return "print";
    }

    @Override
    public void handle(Task task) {
        String message = extractMeta(task);
        log.info("📣 Exécution de PrintTaskHandler avec les meta suivantes: {}", message);
    }
	
    private String extractMeta(Task task) {
        if (task == null || task.getMeta() == null || task.getMeta().isEmpty()) return "n/a";
        return task.getMeta().entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\": \"" + e.getValue() + "\"")
            .collect(Collectors.joining(", ", "{", "}"));
    }

	@Override
	public void handle(Task task, Logger logger) {
		// TODO Auto-generated method stub
		
	}
}
