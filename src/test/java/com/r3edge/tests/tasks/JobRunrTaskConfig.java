package com.r3edge.tests.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.r3edge.tests.TestApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = TestApplication.class,
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("jobrunr")
@Slf4j
class JobRunrTaskConfig {

    @Autowired
    org.springframework.core.env.Environment env;

    @Test
    void shouldHaveSkipLateTrue() {
        assertThat(env.getProperty("r3edge.tasks.skip-late-tasks")).asBoolean().isTrue();
    }
}
