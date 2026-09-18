package com.internal.clientinfo.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ClientNotFoundException extends ResponseStatusException {

	public ClientNotFoundException(String id) {
		super(HttpStatus.NOT_FOUND, "Client '" + id + "' not found");
	}

}
