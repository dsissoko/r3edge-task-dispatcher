package com.r3edge.tasks.dispatcher;

import java.util.Map;

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
        Map<String, Object> meta = task.getMeta();
        String message = (meta != null) ? (String) meta.get("message") : "Aucun message";
        log.info("📣 PrintTaskHandler exécuté : {}", message);
    }

	@Override
	public void onTaskReload(Task previous, Task updated, boolean removed) {
	    if (removed) {
	        log.info("🗑️ PrintTaskHandler : tâche supprimée → id={}, message={}",
	                previous.getId(),
	                extractMessage(previous));
	    } else {
	        log.info("🔁 PrintTaskHandler : tâche rechargée → id={}, ancien={}, nouveau={}",
	                previous.getId(),
	                extractMessage(previous),
	                extractMessage(updated));
	    }
	}
	
	private String extractMessage(Task task) {
	    if (task == null || task.getMeta() == null) return "n/a";
	    Object m = task.getMeta().get("message");
	    return m != null ? m.toString() : "n/a";
	}
}
