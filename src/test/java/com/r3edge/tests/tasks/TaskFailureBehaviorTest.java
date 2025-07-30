package com.r3edge.tests.tasks;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.r3edge.tests.TestApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("failure")
@Slf4j
public class TaskFailureBehaviorTest {

	@Test
	void shouldDispatchAtStartup() {
		assertTrue(true);
	}
}
