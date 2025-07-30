package com.r3edge.tasks.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.r3edge.tasks.dispatcher.core.TaskConfiguration;
import com.r3edge.tasks.dispatcher.impl.jobrunr.JobRunrTaskAutoConfiguration;

/**
 * Configuration automatique du dispatcher de tâches.
 * Active la configuration des propriétés et scanne les composants nécessaires.
 */
@AutoConfiguration
@EnableConfigurationProperties(TaskConfiguration.class)
@ComponentScan(basePackages = "com.r3edge.tasks.dispatcher.core")
@Import(JobRunrTaskAutoConfiguration.class) // sera ignorée si @ConditionalOnClass n’est pas satisfaite
public class TaskDispatcherAutoConfiguration {}