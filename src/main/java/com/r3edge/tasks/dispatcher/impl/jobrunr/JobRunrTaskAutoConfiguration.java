package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auto-configuration conditionnelle pour activer la stratégie JobRunr
 * uniquement si JobRunr est présent dans le classpath.
 */
@AutoConfiguration
@ConditionalOnClass(JobScheduler.class)
@ComponentScan("com.r3edge.tasks.dispatcher.impl.jobrunr")
public class JobRunrTaskAutoConfiguration {
}
