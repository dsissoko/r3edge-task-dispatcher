package com.r3edge.task.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

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
}
