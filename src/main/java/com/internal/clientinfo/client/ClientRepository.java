package com.internal.clientinfo.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

/**
 * In-memory store of mock clients, loaded once at startup from a JSON file.
 */
@Repository
public class ClientRepository {

	private final Map<String, Client> clients;

	public ClientRepository(ObjectMapper objectMapper, @Value("${client-info.data}") Resource data) throws IOException {
		try (InputStream in = data.getInputStream()) {
			this.clients = Arrays.stream(objectMapper.readValue(in, Client[].class))
				.collect(Collectors.toUnmodifiableMap(Client::id, Function.identity()));
		}
	}

	public Optional<Client> findById(String id) {
		return Optional.ofNullable(clients.get(id));
	}

	public List<Client> findAll() {
		return clients.values().stream().sorted((a, b) -> a.id().compareTo(b.id())).toList();
	}

}
