package com.r3edge.tasks.dispatcher;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExceptionFailureTaskHandler implements TaskHandler {

    @Override
    public String getType() {
        return "exceptionfailure";
    }

    @Override
    public void handle(Task task) {
        Object raw = task.getMeta() != null ? task.getMeta().get("message") : null;
        String msg = raw != null ? raw.toString() : "(no message)";
        log.error("❌ {}", msg);
        throw new RuntimeException(msg);
    }

	@Override
	public void handle(Task task, Logger logger) {
		// TODO Auto-generated method stub
		
	}
}
