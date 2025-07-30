package com.r3edge.tasks.dispatcher;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.Task;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

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

	@Override
	public void handle(Task task, Logger logger) {
		// TODO Auto-generated method stub
		
	}
}
