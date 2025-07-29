package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SilentFailureTaskHandler implements TaskHandler {

    @Override
    public String getType() {
        return "silentfailure";
    }

    @Override
    public void handle(Task task) {
        Object raw = task.getMeta() != null ? task.getMeta().get("message") : null;
        String msg = raw != null ? raw.toString() : "(no message)";
        log.error("❌ {}", msg);
    }
}
