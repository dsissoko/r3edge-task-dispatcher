package com.r3edge.tasks.dispatcher;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CleanUpHandler implements TaskHandler{

	@Override
	public String getType() {
		return "cleanup";
	}

    @Override
    public void handle(Task task) {
        String message = extractMeta(task);
        log.info("🔄 Exécution de CleanUpHandler avec les meta suivantes: {}", message);
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
