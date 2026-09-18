package com.internal.clientinfo.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

/**
 * Starts the app on a random port and calls the API over real HTTP.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ClientApiIntegrationTests {

	@LocalServerPort
	private int port;

	private RestClient client;

	@BeforeEach
	void setUp() {
		client = RestClient.create("http://localhost:" + port);
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
