package com.internal.clientinfo.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

/**
 * Black-box API test against an already running instance (e.g. a sandbox).
 * The target is set with {@code -Dapi.baseUrl=...} and defaults to {@code http://localhost:8080}.
 */
class ClientApiIT {

	private RestClient client;

	@BeforeEach
	void setUp() {
		client = RestClient.create(System.getProperty("api.baseUrl", "http://localhost:8080"));
	}

	@Test
	void clientHasDateOfBirth() {
		Client alice = client.get().uri("/api/v1/clients/{id}", "C-1001").retrieve().body(Client.class);

		assertThat(alice).isNotNull();
		assertThat(alice.dateOfBirth()).isEqualTo(LocalDate.of(1985, 3, 14));
	}

	@Test
	void everyClientHasDateOfBirth() {
		Client[] clients = client.get().uri("/api/v1/clients").retrieve().body(Client[].class);

		assertThat(clients).isNotEmpty()
			.allSatisfy(c -> assertThat(c.dateOfBirth()).as("dateOfBirth of %s", c.id()).isNotNull());
	}

}
