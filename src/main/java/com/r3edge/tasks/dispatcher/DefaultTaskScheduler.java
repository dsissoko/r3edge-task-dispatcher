package com.r3edge.tasks.dispatcher;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultTaskScheduler implements ITaskScheduler {
	
    private final TaskScheduler scheduler;
    
    public DefaultTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("r3edge-task-");
        threadPoolTaskScheduler.initialize();
        this.scheduler = threadPoolTaskScheduler;
    }

	@Override
	public void schedule(Task task, Runnable runnable) {
        String cron = task.getCron();
        if (cron == null || cron.isBlank()) return;
        log.info("Lancement de la planification de {}", task);
        scheduler.schedule(runnable, new CronTrigger(cron));
	}
}
