package com.internal.clientinfo.client;

import java.time.Instant;
import java.time.LocalDate;

public record Client(String id, String firstName, String lastName, String email, String phone, LocalDate dateOfBirth,
		Address address, ClientStatus status, boolean locked, String segment, String accountManager,
		Instant createdAt) {

	public record Address(String line1, String line2, String city, String postcode, String country) {
	}

}
