package com.internal.clientinfo.pact;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;

import com.internal.clientinfo.client.Client;

/**
 * Example consumer of this API. Writes its contract to {@code target/pacts}; copy it to
 * {@code src/test/resources/pacts} so {@link ClientInfoPactIT} verifies the provider
 * against it. Real consumers generate their pacts in their own repos the same way.
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = ClientInfoPactIT.PROVIDER)
class ExampleConsumerPactTests {

	private static final String CONSUMER = "example-consumer";

	@Pact(consumer = CONSUMER)
	V4Pact existingClient(PactDslWithProvider builder) {
		return builder.given("client C-1001 exists")
			.uponReceiving("a request for client C-1001")
			.method("GET")
			.path("/api/v1/clients/C-1001")
			.willRespondWith()
			.status(200)
			.body(newJsonBody((body) -> {
				body.stringValue("id", "C-1001");
				body.stringType("firstName", "Alice");
				body.stringType("lastName", "Morgan");
				body.stringType("email", "alice.morgan@example.com");
				body.stringMatcher("dateOfBirth", "\\d{4}-\\d{2}-\\d{2}", "1985-03-14");
				body.stringMatcher("status", "ACTIVE|SUSPENDED|CLOSED", "ACTIVE");
				body.booleanType("locked", false);
				body.object("address", (address) -> {
					address.stringType("city", "London");
					address.stringType("country", "GB");
				});
			}).build())
			.toPact(V4Pact.class);
	}

	@Pact(consumer = CONSUMER)
	V4Pact unknownClient(PactDslWithProvider builder) {
		return builder.given("client C-9999 does not exist")
			.uponReceiving("a request for an unknown client")
			.method("GET")
			.path("/api/v1/clients/C-9999")
			.willRespondWith()
			.status(404)
			.matchHeader("Content-Type", "application/problem\\+json.*", "application/problem+json")
			.body(newJsonBody((body) -> body.integerType("status", 404)).build())
			.toPact(V4Pact.class);
	}

	@Test
	@PactTestFor(pactMethod = "existingClient")
	void readsClient(MockServer mockServer) {
		Client client = RestClient.create(mockServer.getUrl())
			.get()
			.uri("/api/v1/clients/{id}", "C-1001")
			.retrieve()
			.body(Client.class);

		assertThat(client).isNotNull();
		assertThat(client.dateOfBirth()).isEqualTo(LocalDate.of(1985, 3, 14));
		assertThat(client.address().city()).isEqualTo("London");
	}

	@Test
	@PactTestFor(pactMethod = "unknownClient")
	void handlesUnknownClient(MockServer mockServer) {
		RestClient client = RestClient.create(mockServer.getUrl());

		assertThatThrownBy(() -> client.get().uri("/api/v1/clients/{id}", "C-9999").retrieve().body(Client.class))
			.isInstanceOf(HttpClientErrorException.NotFound.class);
	}

}
