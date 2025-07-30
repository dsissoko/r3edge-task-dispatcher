package com.r3edge.tasks.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.r3edge.tasks.dispatcher.core.TaskConfiguration;

/**
 * Configuration automatique du dispatcher de tâches.
 * Active la configuration des propriétés et scanne les composants nécessaires.
 */
@AutoConfiguration
@EnableConfigurationProperties(TaskConfiguration.class)
@ComponentScan(basePackages = "com.r3edge.tasks.dispatcher.core")
public class TaskDispatcherAutoConfiguration {}