package com.r3edge.tasks.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.r3edge.tasks.dispatcher.TaskConfiguration;

@AutoConfiguration
@EnableConfigurationProperties(TaskConfiguration.class)
@ComponentScan(basePackages = "com.r3edge.tasks.dispatcher")
public class TaskDispatcherAutoConfiguration {}