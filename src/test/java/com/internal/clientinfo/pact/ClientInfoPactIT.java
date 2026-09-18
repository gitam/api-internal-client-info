package com.internal.clientinfo.pact;

import java.net.MalformedURLException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

/**
 * Verifies this service against every consumer contract in
 * {@code src/test/resources/pacts}, calling a running instance (e.g. a sandbox) set with
 * {@code -Dapi.baseUrl=...}.
 */
@Provider(ClientInfoPactIT.PROVIDER)
@PactFolder("pacts")
class ClientInfoPactIT {

	static final String PROVIDER = "client-info";

	@BeforeEach
	void setTarget(PactVerificationContext context) throws MalformedURLException {
		String baseUrl = System.getProperty("api.baseUrl", "http://localhost:8080");
		context.setTarget(HttpTestTarget.fromUrl(URI.create(baseUrl).toURL()));
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void verifyContract(PactVerificationContext context) {
		context.verifyInteraction();
	}

	// The sandbox serves the fixed seed data in clients.json, so these states need no
	// setup; they fail verification if a consumer relies on a state we don't know.

	@State("client C-1001 exists")
	void clientExists() {
	}

	@State("client C-9999 does not exist")
	void clientDoesNotExist() {
	}

}
