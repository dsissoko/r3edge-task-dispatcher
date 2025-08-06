package com.r3edge.tasks.dispatcher.impl.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auto-configuration Spring Boot pour activer la stratégie Hazelcast.
 * 
 * Ne s'active que si HazelcastInstance est disponible dans le classpath.
 * Scanne automatiquement le package Hazelcast pour déclarer les beans.
 */
@AutoConfiguration
@ConditionalOnClass(HazelcastInstance.class)
@ComponentScan("com.r3edge.tasks.dispatcher.impl.hazelcast")
public class HazelcastTaskAutoConfiguration {
}
