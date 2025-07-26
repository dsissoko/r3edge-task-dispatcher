package com.r3edge.tasks;

import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

//    @Bean
//    public StorageProvider storageProvider() {
//        return new InMemoryStorageProvider();
//    }
//
//    @Bean
//    public JobScheduler jobScheduler(StorageProvider storageProvider) {
//        return new JobScheduler(storageProvider);
//    }
}
