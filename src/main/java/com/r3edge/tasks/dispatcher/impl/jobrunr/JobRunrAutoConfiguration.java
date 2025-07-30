package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration conditionnelle pour activer la stratégie JobRunr
 * uniquement si JobRunr est présent dans le classpath.
 */
@AutoConfiguration
@ConditionalOnClass(JobScheduler.class)
@Import(JobRunrTaskStrategyConfig.class)
public class JobRunrAutoConfiguration {
    // vide, mais safe
}
