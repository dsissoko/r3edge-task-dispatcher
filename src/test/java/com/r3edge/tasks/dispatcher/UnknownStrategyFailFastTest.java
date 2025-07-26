package com.r3edge.tasks.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.r3edge.tasks.TestApplication;

class UnknownStrategyFailFastTest {

	@Test
	void shouldFailFastWhenStrategyIsUnknown() {
	    Throwable thrown = catchThrowable(() -> {
	        try (ConfigurableApplicationContext context =
	                     new SpringApplicationBuilder(TestApplication.class)
	                             .profiles("dummy") // dummy = stratégie inconnue
	                             .run()) {
	            // rien ici, car ça ne devrait jamais passer
	        }
	    });

	    assertThat(thrown)
	        .isInstanceOf(Exception.class)
	        .hasMessageContaining("ITaskExecutor"); // ou un message plus générique selon ton design
	}
}
