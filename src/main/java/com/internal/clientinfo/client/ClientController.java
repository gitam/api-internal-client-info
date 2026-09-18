package com.internal.clientinfo.client;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

	private final ClientRepository repository;

	public ClientController(ClientRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/{id}")
	public Client get(@PathVariable String id) {
		return repository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
	}

	@GetMapping
	public List<Client> search(@RequestParam(required = false) String email,
			@RequestParam(required = false) ClientStatus status,
			@RequestParam(required = false) String segment) {
		Stream<Client> result = repository.findAll().stream();
		if (email != null) {
			result = result.filter(c -> c.email().equalsIgnoreCase(email));
		}
		if (status != null) {
			result = result.filter(c -> c.status() == status);
		}
		if (segment != null) {
			result = result.filter(c -> c.segment().equalsIgnoreCase(segment));
		}
		return result.toList();
	}

}
