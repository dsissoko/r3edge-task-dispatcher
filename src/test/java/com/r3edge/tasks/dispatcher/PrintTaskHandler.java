package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler de test qui affiche un message depuis les métadonnées.
 */
@Slf4j

@Component
public class PrintTaskHandler implements TaskHandler {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "1234RRR";
	}

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
	    if (task == null || task.getMeta() == null) return "n/a";
	    Object m = task.getMeta().get("message");
	    return m != null ? m.toString() : "n/a";
	}
}
