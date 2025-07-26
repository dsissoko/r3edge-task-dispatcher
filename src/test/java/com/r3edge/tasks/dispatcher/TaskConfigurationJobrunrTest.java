package com.r3edge.tasks.dispatcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.r3edge.tasks.TestApplication;

@SpringBootTest(classes = TestApplication.class,
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("jobrunr")
class TaskConfigurationJobrunrTest {

//    @TestConfiguration
//    static class JobrunrBeans {
//        @Bean
//        public StorageProvider storageProvider() {
//            return new InMemoryStorageProvider();
//        }
//
//        @Bean
//        public JobScheduler jobScheduler(StorageProvider storageProvider) {
//            return new JobScheduler(storageProvider);
//        }
//    }

    @Autowired
    ITaskExecutor executor;

    @Test
    void testJobrunrExecutorPresent() {
        assertNotNull(executor);
        System.out.println("✅ JobRunr executor: " + executor.getClass());
    }
}

