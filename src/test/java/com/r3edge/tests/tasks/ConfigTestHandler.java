package com.r3edge.tests.tasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

import lombok.extern.slf4j.Slf4j;

@Component("config-test-handler")
@Slf4j
public class ConfigTestHandler implements TaskHandler {

    private final List<String> executedTasks = new CopyOnWriteArrayList<>();

    @Override
    public void execute(TaskDescriptor task) {
    	log.info("START Configuration de tâche");
        executedTasks.add(task.getId());
    }

    public List<String> getExecutedTasks() {
        return executedTasks;
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "configTest";
	}
}
