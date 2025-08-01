package com.r3edge.tests.tasks;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SilentFailureTaskHandler implements TaskHandler {

    @Override
    public String getName() {
        return "silentfailure";
    }

	@Override
	public void execute(TaskDescriptor task) {
		// Au choix : un logger agnostique
		Logger logger = LoggerFactory.getLogger(this.getClass());
		// ou un logger adpaté à la srategy mais qui rend le handler dépendant de l'infra choisie
		logger = new JobRunrDashboardLogger(LoggerFactory.getLogger(this.getClass()));
        Object raw = task.getMeta() != null ? task.getMeta().get("message") : null;
        String msg = raw != null ? raw.toString() : "(no message)";
        logger.error("❌ {}", msg);
	}
}
